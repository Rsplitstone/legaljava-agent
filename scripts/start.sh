#!/bin/bash

# LegalJava Project Startup Script

echo "🏛️  Starting LegalJava - AI-Powered Legal Assistant"
echo "================================================="

# Check if .env file exists
if [ ! -f ".env" ]; then
    echo "⚠️  No .env file found. Creating one from .env.example..."
    cp .env.example .env
    echo "📝 Please edit .env file and add your OPENAI_API_KEY"
    echo "   Then run this script again."
    exit 1
fi

# Check if OpenAI API key is set
if grep -q "your_openai_api_key_here" .env; then
    echo "⚠️  Please set your OPENAI_API_KEY in the .env file"
    exit 1
fi

echo "🐳 Starting services with Docker Compose..."
docker-compose up --build -d

echo "⏳ Waiting for services to start..."
sleep 10

echo "🔍 Checking service health..."

# Check database
echo -n "Database: "
if docker-compose exec -T db pg_isready -U postgres > /dev/null 2>&1; then
    echo "✅ Ready"
else
    echo "❌ Not ready"
fi

# Check backend
echo -n "Backend: "
if curl -s http://localhost:8080/api/chat/health > /dev/null 2>&1; then
    echo "✅ Ready"
else
    echo "❌ Not ready"
fi

# Check Python RAG service
echo -n "RAG Service: "
if curl -s http://localhost:8000/health > /dev/null 2>&1; then
    echo "✅ Ready"
else
    echo "❌ Not ready"
fi

# Check frontend
echo -n "Frontend: "
if curl -s http://localhost:5173 > /dev/null 2>&1; then
    echo "✅ Ready"
else
    echo "❌ Not ready"
fi

echo ""
echo "🎉 LegalJava is starting up!"
echo ""
echo "📱 Frontend: http://localhost:5173"
echo "🔧 Backend API: http://localhost:8080/api"
echo "🤖 RAG Service: http://localhost:8000"
echo "🗄️  Database: localhost:5432"
echo ""
echo "📖 View logs: docker-compose logs -f"
echo "🛑 Stop services: docker-compose down"
