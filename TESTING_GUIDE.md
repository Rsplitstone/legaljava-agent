# LegalJava Prototype Testing Guide

## Quick Start

### Option 1: Full Docker Setup (Recommended for Testing)

```bash
# Start all services
docker-compose up -d

# Check status
docker-compose ps

# View logs
docker-compose logs -f

# Stop all services
docker-compose down
```

### Option 2: Development Mode (Recommended for UI Development)

1. **Start Database Only**

   ```bash
   docker-compose up -d db
   ```

2. **Start Backend** (choose one)

   ```bash
   # Option A: Docker
   docker-compose up -d backend

   # Option B: Native (for development)
   cd backend
   ./gradlew bootRun
   ```

3. **Start RAG Service** (choose one)

   ```bash
   # Option A: Docker
   docker-compose up -d py-rag

   # Option B: Native (for development)
   cd py-rag
   pip install -r requirements.txt
   uvicorn main:app --reload --host 0.0.0.0 --port 8000
   ```

4. **Start Frontend** (recommended for UI development)
   ```bash
   cd ui
   npm install
   npm run dev
   ```

### Option 3: Use Helper Scripts

```bash
# Windows
scripts\test-prototype.bat

# Linux/Mac
bash scripts/test-prototype.sh
```

## Access URLs

| Service     | URL                   | Description                |
| ----------- | --------------------- | -------------------------- |
| Frontend    | http://localhost:5173 | React dashboard and UI     |
| Backend API | http://localhost:8080 | Spring Boot REST API       |
| RAG Service | http://localhost:8000 | Python FastAPI RAG service |
| Database    | localhost:5432        | PostgreSQL with pgvector   |

## Testing the Dashboard

1. Open http://localhost:5173 in your browser
2. You should see the LegalJava Dashboard with:
   - Statistics cards (Total Cases, Pending Tasks, Recent Uploads)
   - Quick action buttons (Upload AME Report, Calculate Benefits, Ask AI)
   - Recent activity feed
   - Navigation tabs (Dashboard, Legal Chat, Document Upload, Workers' Comp Cases)

## API Testing

### Backend Health Check

```bash
curl http://localhost:8080/actuator/health
```

### RAG Service Health Check

```bash
curl http://localhost:8000/health
```

### Test Document Upload

```bash
curl -X POST http://localhost:8080/api/documents \
  -H "Content-Type: application/json" \
  -d '{"title":"Test Document","content":"Test content"}'
```

## Development Workflow

### For Frontend Development

1. Start database: `docker-compose up -d db`
2. Start backend: `docker-compose up -d backend`
3. Start RAG service: `docker-compose up -d py-rag`
4. Run frontend in dev mode: `cd ui && npm run dev`
5. Make changes to React components with hot reload

### For Backend Development

1. Start database: `docker-compose up -d db`
2. Run backend with Gradle: `cd backend && ./gradlew bootRun`
3. Start other services as containers
4. Make changes to Java code with automatic restart

### For RAG Service Development

1. Start database: `docker-compose up -d db`
2. Run RAG service with uvicorn: `cd py-rag && uvicorn main:app --reload`
3. Start other services as containers
4. Make changes to Python code with automatic reload

## Troubleshooting

### Port Conflicts

- Database: 5432
- Backend: 8080
- RAG Service: 8000
- Frontend: 5173

Check for conflicts: `netstat -an | grep :<port>`

### Environment Variables

Make sure your `.env` file has:

```
OPENAI_API_KEY=your_key_here
DATABASE_URL=postgresql://postgres:legaljava@localhost:5432/legaljava
VITE_API_URL=http://localhost:8080
```

### Docker Issues

```bash
# Rebuild containers
docker-compose build --no-cache

# View logs
docker-compose logs -f <service_name>

# Reset everything
docker-compose down -v
docker-compose up -d
```

## Next Steps for UI Development

1. **Enhance Dashboard Components**

   - Add real API calls to fetch statistics
   - Implement click handlers for action buttons
   - Add more detailed activity tracking

2. **Develop Feature-Specific Pages**

   - AME Report upload and processing
   - Benefits calculation interface
   - Case management forms
   - AI chat interface improvements

3. **Add State Management**

   - Consider Redux or Zustand for global state
   - Implement proper error handling
   - Add loading states and user feedback

4. **Improve UI/UX**
   - Add more interactive elements
   - Implement responsive design
   - Add form validation
   - Enhance accessibility

## Useful Commands

```bash
# Check service health
curl -s http://localhost:8080/actuator/health | jq
curl -s http://localhost:8000/health | jq

# View container logs
docker-compose logs -f frontend
docker-compose logs -f backend
docker-compose logs -f py-rag
docker-compose logs -f db

# Restart specific service
docker-compose restart frontend
docker-compose restart backend

# Scale services (for testing)
docker-compose up -d --scale py-rag=2
```
