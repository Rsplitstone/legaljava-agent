from fastapi import FastAPI, HTTPException
from pydantic import BaseModel
from typing import List, Optional
import os
import asyncpg
import asyncio
import numpy as np
from langchain_openai import OpenAIEmbeddings
from langchain_community.text_splitter import RecursiveCharacterTextSplitter
from langchain_openai import ChatOpenAI
import uvicorn
import re
from decimal import Decimal

app = FastAPI(title="LegalJava RAG Service", version="1.0.0")

class QueryRequest(BaseModel):
    query: str
    userId: Optional[str] = None
    sessionId: Optional[str] = None

class QueryResponse(BaseModel):
    response: str
    citations: List[str]
    sessionId: Optional[str] = None
    confidence: Optional[float] = None

class SummarizeRequest(BaseModel):
    reportContent: str

class SummarizeResponse(BaseModel):
    summary: str
    disabilityRating: Optional[float] = None
    workRestrictions: Optional[str] = None
    treatmentRecommendations: Optional[str] = None

class PgVectorRAGService:
    def __init__(self):
        self.embeddings = None
        self.db_pool = None
        self.llm = None
        self.initialize_rag()
    
    def initialize_rag(self):
        """Initialize the RAG components"""
        openai_api_key = os.getenv("OPENAI_API_KEY")
        if not openai_api_key:
            print("Warning: OPENAI_API_KEY not set. RAG service will use mock responses.")
            return
        
        try:
            # Initialize embeddings
            self.embeddings = OpenAIEmbeddings(openai_api_key=openai_api_key)
            
            # Initialize LLM (ChatOpenAI)
            self.llm = ChatOpenAI(
                openai_api_key=openai_api_key,
                temperature=0.1,
                max_tokens=1000
            )
            
            print("RAG service initialized successfully")
        except Exception as e:
            print(f"Error initializing RAG service: {e}")
    
    async def get_db_connection(self):
        """Get database connection"""
        if not self.db_pool:
            database_url = os.getenv("DATABASE_URL", "postgresql://postgres:legaljava@db:5432/legaljava")
            self.db_pool = await asyncpg.create_pool(database_url)
        return self.db_pool
    
    async def similarity_search(self, query_embedding: List[float], k: int = 5) -> List[dict]:
        """Perform similarity search using pgvector"""
        pool = await self.get_db_connection()
        
        async with pool.acquire() as conn:
            # Convert embedding to pgvector format
            embedding_str = '[' + ','.join(map(str, query_embedding)) + ']'
            
            # Perform similarity search
            rows = await conn.fetch("""
                SELECT id, title, content, document_type, metadata,
                       embedding <-> $1::vector as distance
                FROM legal_documents 
                WHERE embedding IS NOT NULL
                ORDER BY embedding <-> $1::vector
                LIMIT $2
            """, embedding_str, k)
            
            return [dict(row) for row in rows]
    
    async def store_embedding(self, document_id: int, content: str, embedding: List[float]):
        """Store document embedding in database"""
        pool = await self.get_db_connection()
        
        async with pool.acquire() as conn:
            embedding_str = '[' + ','.join(map(str, embedding)) + ']'
            await conn.execute("""
                UPDATE legal_documents 
                SET embedding = $1::vector 
                WHERE id = $2
            """, embedding_str, document_id)
    
    async def process_query(self, query: str) -> QueryResponse:
        """Process a legal query and return response with citations"""
        if not self.embeddings or not self.llm:
            # Mock response when RAG is not properly initialized
            return QueryResponse(
                response=f"Mock response for query: '{query}'. This is a placeholder response indicating that the RAG service is not fully configured. Please ensure OPENAI_API_KEY is set and the vector database is populated.",
                citations=["Mock citation 1", "Mock citation 2"],
                confidence=0.8
            )
        
        try:
            # Generate embedding for the query
            query_embedding = self.embeddings.embed_query(query)
            
            # Perform similarity search
            similar_docs = await self.similarity_search(query_embedding)
            
            if not similar_docs:
                return QueryResponse(
                    response="I don't have enough information in my knowledge base to answer your query accurately. Please upload relevant legal documents first.",
                    citations=[],
                    confidence=0.1
                )
            
            # Prepare context from similar documents
            context_parts = []
            citations = []
            
            for doc in similar_docs:
                context_parts.append(f"Document: {doc['title']}\nContent: {doc['content'][:500]}...")
                citations.append(doc['title'])
            
            context = "\n\n".join(context_parts)
            
            # Generate response using LLM
            prompt = f"""
            Based on the following legal documents, please answer the user's question.
            Provide a clear, accurate response and reference specific documents when possible.
            
            Context:
            {context}
            
            Question: {query}
            
            Answer:
            """
            
            response = self.llm.invoke(prompt)
            
            return QueryResponse(
                response=response.content.strip() if hasattr(response, 'content') else str(response).strip(),
                citations=citations,
                confidence=0.9
            )
        except Exception as e:
            raise HTTPException(status_code=500, detail=f"Error processing query: {str(e)}")

# Initialize RAG service
rag_service = PgVectorRAGService()

@app.get("/")
async def root():
    return {"message": "LegalJava RAG Service is running"}

@app.get("/health")
async def health_check():
    return {"status": "healthy", "service": "LegalJava RAG"}

@app.post("/query", response_model=QueryResponse)
async def process_query(request: QueryRequest):
    """Process a legal query and return AI-generated response with citations"""
    try:
        response = await rag_service.process_query(request.query)
        response.sessionId = request.sessionId
        return response
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))

@app.post("/ingest")
async def ingest_document(document_id: int, content: str):
    """Generate and store embeddings for a document"""
    if not rag_service.embeddings:
        raise HTTPException(status_code=503, detail="Embedding service not initialized")
    
    try:
        # Split the document into chunks
        text_splitter = RecursiveCharacterTextSplitter(
            chunk_size=1000,
            chunk_overlap=200
        )
        chunks = text_splitter.split_text(content)
        
        # Generate embeddings for all chunks and combine them
        chunk_embeddings = []
        for chunk in chunks:
            embedding = rag_service.embeddings.embed_query(chunk)
            chunk_embeddings.append(embedding)
        
        # Average the embeddings (simple approach)
        if chunk_embeddings:
            avg_embedding = np.mean(chunk_embeddings, axis=0).tolist()
            await rag_service.store_embedding(document_id, content, avg_embedding)
        
        return {"message": f"Document {document_id} processed successfully", "chunks": len(chunks)}
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Error processing document: {str(e)}")

@app.post("/summarize_ame_report", response_model=SummarizeResponse)
async def summarize_ame_report(request: SummarizeRequest):
    """Summarize an AME report and extract key information"""
    try:
        if not rag_service.llm:
            # Return mock response if OpenAI is not configured
            return SummarizeResponse(
                summary="Mock summary: The patient shows signs of work-related injury with recommended treatment plan.",
                disabilityRating=15.5,
                workRestrictions="Light duty, no lifting over 20 pounds",
                treatmentRecommendations="Physical therapy 3x per week for 6 weeks"
            )
        
        # Enhanced prompt for AME report analysis
        prompt = f"""
As a medical legal expert, analyze this AME (Agreed Medical Examiner) report and provide:

1. A comprehensive summary (2-3 paragraphs)
2. Recommended disability rating percentage (if mentioned)
3. Work restrictions (if any)
4. Treatment recommendations (if any)

Report Content:
{request.reportContent}

Please provide your analysis in the following format:
SUMMARY: [Your summary here]
DISABILITY_RATING: [Percentage if found, or NONE]
WORK_RESTRICTIONS: [Restrictions if found, or NONE]
TREATMENT_RECOMMENDATIONS: [Recommendations if found, or NONE]
"""
        
        # Get AI response
        ai_response = rag_service.llm.invoke(prompt)
        
        # Parse the response
        summary = extract_section(ai_response.content if hasattr(ai_response, 'content') else str(ai_response), "SUMMARY")
        disability_rating = extract_disability_rating(ai_response.content if hasattr(ai_response, 'content') else str(ai_response))
        work_restrictions = extract_section(ai_response.content if hasattr(ai_response, 'content') else str(ai_response), "WORK_RESTRICTIONS")
        treatment_recommendations = extract_section(ai_response.content if hasattr(ai_response, 'content') else str(ai_response), "TREATMENT_RECOMMENDATIONS")
        
        return SummarizeResponse(
            summary=summary,
            disabilityRating=disability_rating,
            workRestrictions=work_restrictions if work_restrictions != "NONE" else None,
            treatmentRecommendations=treatment_recommendations if treatment_recommendations != "NONE" else None
        )
        
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Error summarizing AME report: {str(e)}")

def extract_section(text: str, section_name: str) -> str:
    """Extract a section from the AI response"""
    pattern = f"{section_name}:\\s*(.+?)(?=\\n[A-Z_]+:|$)"
    match = re.search(pattern, text, re.DOTALL)
    if match:
        return match.group(1).strip()
    return "No information extracted"

def extract_disability_rating(text: str) -> Optional[float]:
    """Extract disability rating percentage from the AI response"""
    # Look for DISABILITY_RATING section first
    disability_section = extract_section(text, "DISABILITY_RATING")
    if disability_section and disability_section != "NONE":
        # Extract percentage from the section
        percentage_match = re.search(r'(\d+(?:\.\d+)?)', disability_section)
        if percentage_match:
            return float(percentage_match.group(1))
    
    # Fallback: look for percentage patterns in the entire text
    percentage_patterns = [
        r'(\d+(?:\.\d+)?)\s*%\s*disability',
        r'disability\s*rating\s*of\s*(\d+(?:\.\d+)?)',
        r'(\d+(?:\.\d+)?)\s*percent\s*disability'
    ]
    
    for pattern in percentage_patterns:
        match = re.search(pattern, text, re.IGNORECASE)
        if match:
            return float(match.group(1))
    
    return None

if __name__ == "__main__":
    uvicorn.run(app, host="0.0.0.0", port=8000)
