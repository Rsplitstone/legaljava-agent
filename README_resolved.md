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
- **📅 Task Scheduling**: Kanban-style task management with drag-and-drop
- **🔔 Real-time Alerts**: Socket.io integration for live notifications
- **♿ Accessibility**: WCAG compliant UI components

## 🚀 Quick Start

### Prerequisites

- Docker and Docker Compose
- OpenAI API key

### One-Command Startup

1. **Clone the repository**:

   ```bash
   git clone https://github.com/Rsplitstone/legaljava-agent.git
   cd legaljava-agent
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

### 4. Task Management

- Use the Kanban-style Schedule page for task management
- Drag and drop tasks between columns
- Real-time updates via WebSocket

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
│   │   └── com/legaljava/  # Controllers, services, entities
│   ├── src/main/resources/ # Configuration files
│   └── build.gradle        # Gradle build configuration
├── py-rag/                 # Python RAG service
│   ├── main.py            # FastAPI application
│   └── requirements.txt   # Python dependencies
├── ui/                    # React frontend
│   ├── src/               # TypeScript source code
│   │   ├── components/    # React components
│   │   ├── pages/         # Page components
│   │   ├── services/      # API services
│   │   └── sidebar/       # Sidebar components
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

#### File Upload Endpoints

- `GET /api/files` - List all uploaded files
- `POST /api/files/upload` - Upload new files
- `PATCH /api/files/{id}` - Update file metadata

#### Kanban Task Endpoints

- `GET /api/columns` - Get all Kanban columns and tasks
- `POST /api/columns` - Create new column
- `PATCH /api/columns/{id}` - Update column or task

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

## 🎨 Frontend Components

### Core Components

- **FileDrop**: Drag-and-drop file upload with progress tracking
- **Schedule**: Kanban-style task management with DnD
- **ChatPanel**: Real-time chat interface with Socket.io
- **Dashboard**: Main dashboard with analytics
- **Layout**: Application layout with navigation

### Services

- **fetchEvents**: Calendar event retrieval service
- **createEvent**: Event creation service
- **api**: Base API configuration

## 🧪 Testing

```bash
# Backend tests
cd backend
./gradlew test

# Frontend tests
cd ui
npm test

# API testing
curl http://localhost:8080/api/files
curl http://localhost:8080/api/columns
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
- [React Query](https://tanstack.com/query) for data fetching
- [Hello Pangea DnD](https://github.com/hello-pangea/dnd) for drag-and-drop functionality

## 📞 Support

For support and questions, please open an issue on GitHub or contact the development team.

---

**Made with ❤️ for the legal and developer communities**
