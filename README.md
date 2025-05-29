# LegalJava - AI-Powered Legal Assistant

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2+-green.svg)](https://spring.io/projects/spring-boot)
[![React](https://img.shields.io/badge/React-18+-blue.svg)](https://reactjs.org/)
[![FastAPI](https://img.shields.io/badge/FastAPI-0.100+-red.svg)](https://fastapi.tiangolo.com/)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15+-blue.svg)](https://www.postgresql.org/)

A comprehensive AI-powered legal assistant that can ingest legal PDFs, embed them into a vector index, and answer citation-backed questions through a modern chat interface. Built with enterprise-grade architecture using Spring Boot, FastAPI, React, and PostgreSQL with pgvector.

## 🏗️ Architecture

- **Backend**: Spring Boot 3.2 (Java) with PostgreSQL
- **RAG Service**: FastAPI (Python) with LangChain and OpenAI embeddings
- **Frontend**: React 18 with TypeScript and Tailwind CSS
- **Database**: PostgreSQL 15 with pgvector extension
- **Orchestration**: Docker Compose for seamless deployment

## ✨ Features

- **📄 Document Ingestion**: Upload and process legal PDF documents
- **🔍 Vector Search**: Semantic search using OpenAI embeddings and pgvector
- **💬 Chat Interface**: Interactive legal question answering with citations
- **📊 Document Management**: Full CRUD operations for legal documents
- **🔒 Enterprise Security**: CORS configuration and secure API endpoints
- **🐳 Containerized**: Complete Docker setup for easy deployment

## 🚀 Quick Start

### Prerequisites

- Docker and Docker Compose
- OpenAI API key

### One-Command Startup

1. **Clone the repository**:

   ```bash
   git clone https://github.com/yourusername/legaljava.git
   cd legaljava
   ```

2. **Set up environment**:

   ```bash
   cp .env.example .env
   # Edit .env and add your OPENAI_API_KEY
   ```

3. **Start all services**:

   ```bash
   # On Windows
   scripts\start.bat

   # On Linux/Mac
   chmod +x scripts/start.sh
   ./scripts/start.sh

   # Or directly with Docker Compose
   docker-compose up --build
   ```

4. **Access the application**:
   - 📱 **Frontend**: http://localhost:5173
   - 🔧 **Backend API**: http://localhost:8080/api
   - 🤖 **RAG Service**: http://localhost:8000
   - 🗄️ **Database**: localhost:5432

## 📋 Usage

### 1. Upload Legal Documents

- Navigate to the "Document Upload" tab
- Drag and drop PDF files or click to select
- Documents are automatically processed and embedded

### 2. Ask Legal Questions

- Use the "Legal Chat" interface
- Ask questions about uploaded documents
- Receive AI-generated responses with citations

### 3. Manage Documents

- View all uploaded documents via the API
- Search documents by keywords
- Delete documents as needed

## 🛠️ Development Setup

### Manual Setup (Without Docker)

#### Backend (Spring Boot)

```bash
cd backend
./gradlew bootRun
# Runs on http://localhost:8080
```

#### RAG Service (Python)

```bash
cd py-rag
pip install -r requirements.txt
uvicorn main:app --reload --port 8000
# Runs on http://localhost:8000
```

#### Frontend (React)

```bash
cd ui
npm install
npm run dev
# Runs on http://localhost:5173
```

#### Database (PostgreSQL with pgvector)

```bash
# Install PostgreSQL 15+
# Enable pgvector extension
# Run initialization script: infra/db/init.sql
```

## 📁 Project Structure

```
legaljava/
├── backend/                 # Spring Boot application
│   ├── src/main/java/      # Java source code
│   ├── src/main/resources/ # Configuration files
│   └── build.gradle        # Gradle build configuration
├── py-rag/                 # Python RAG service
│   ├── main.py            # FastAPI application
│   └── requirements.txt   # Python dependencies
├── ui/                    # React frontend
│   ├── src/               # TypeScript source code
│   └── package.json       # Node.js dependencies
├── infra/                 # Infrastructure configuration
│   └── db/init.sql        # Database initialization
├── docker/                # Docker configurations
│   ├── Dockerfile.backend
│   ├── Dockerfile.py
│   └── Dockerfile.frontend
└── docker-compose.yml     # Multi-service orchestration
```

## 🔧 API Endpoints

### Backend (Spring Boot) - Port 8080

#### Chat Endpoints

- `POST /api/chat/query` - Process legal queries
- `GET /api/chat/health` - Health check
- `GET /api/chat/history/{userId}` - Get user query history
- `GET /api/chat/session/{sessionId}` - Get session queries

#### Document Endpoints

- `GET /api/documents` - List all documents
- `GET /api/documents/{id}` - Get document by ID
- `POST /api/documents` - Create new document
- `POST /api/documents/upload` - Upload PDF document
- `GET /api/documents/search?keyword=` - Search documents
- `DELETE /api/documents/{id}` - Delete document

### RAG Service (FastAPI) - Port 8000

- `GET /` - Service status
- `GET /health` - Health check
- `POST /query` - Process RAG queries
- `POST /ingest` - Ingest document embeddings

## 🔍 Database Schema

### legal_documents

```sql
id              BIGSERIAL PRIMARY KEY
title           VARCHAR(500) NOT NULL
content         TEXT NOT NULL
file_path       VARCHAR(1000)
document_type   VARCHAR(100)
upload_date     TIMESTAMP DEFAULT CURRENT_TIMESTAMP
metadata        JSONB
embedding       vector(1536)  -- OpenAI embedding dimension
created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP
updated_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP
```

### legal_queries

```sql
id              BIGSERIAL PRIMARY KEY
query_text      TEXT NOT NULL
response        TEXT
citations       TEXT[]
confidence_score DECIMAL(3,2)
created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP
updated_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP
```

## 🧪 Testing

```bash
# Backend tests
cd backend
./gradlew test

# Frontend tests
cd ui
npm test

# API testing
curl http://localhost:8080/api/chat/health
curl http://localhost:8000/health
```

## 🚀 Deployment

### Production Deployment

1. **Set production environment variables**
2. **Build optimized containers**:
   ```bash
   docker-compose -f docker-compose.prod.yml up --build
   ```

### Environment Variables

| Variable                 | Description                           | Required |
| ------------------------ | ------------------------------------- | -------- |
| `OPENAI_API_KEY`         | OpenAI API key for embeddings and LLM | Yes      |
| `POSTGRES_DB`            | Database name                         | Yes      |
| `POSTGRES_USER`          | Database username                     | Yes      |
| `POSTGRES_PASSWORD`      | Database password                     | Yes      |
| `SPRING_PROFILES_ACTIVE` | Spring Boot profile                   | No       |
| `RAG_SERVICE_URL`        | URL for Python RAG service            | No       |

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🙏 Acknowledgments

- [OpenAI](https://openai.com/) for embeddings and language models
- [LangChain](https://langchain.com/) for RAG framework
- [pgvector](https://github.com/pgvector/pgvector) for vector database capabilities
- [Spring Boot](https://spring.io/projects/spring-boot) for enterprise Java framework

## 📞 Support

For support and questions, please open an issue on GitHub or contact the development team.

---

**Made with ❤️ for the legal and developer communities**

```bash
npm start
```

The application will be available at `http://localhost:3000` by default.

## 🛠 Project Structure

```
legaljava-agent/
├── src/
│   ├── agent/           # Core agent functionality
│   │   ├── auth/        # Authentication middleware
│   │   ├── controllers/ # Request handlers
│   │   ├── events/      # Event handlers
│   │   └── middleware/  # Express middleware
│   ├── legal/           # Legal domain logic
│   │   ├── corpus/      # Legal corpus management
│   │   ├── services/    # Business logic services
│   │   └── models/      # Data models
│   ├── llm/             # LLM integration
│   │   ├── chains/      # LangChain chains
│   │   ├── embeddings/  # Embedding models
│   │   ├── models/      # LLM models
│   │   └── prompts/     # Prompt templates
│   └── utils/           # Utility functions
├── data/                # Data storage
│   ├── labor_code/      # Labor code documents
│   ├── regulations/     # Regulatory documents
│   ├── cases/           # Legal cases
│   └── vector-db/       # Vector database storage
├── docs/                # Documentation
│   ├── api/             # API documentation
│   └── guides/          # How-to guides
└── tests/               # Test suites
    ├── fixtures/        # Test fixtures
    ├── unit/            # Unit tests
    ├── integration/     # Integration tests
    └── e2e/             # End-to-end tests
```

## 🤝 Contributing

We welcome contributions! Please see our [Contributing Guide](CONTRIBUTING.md) for details.

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🙏 Acknowledgments

- OpenAI for their powerful language models
- LangChain for the LLM orchestration framework
- The open-source community for valuable tools and libraries
