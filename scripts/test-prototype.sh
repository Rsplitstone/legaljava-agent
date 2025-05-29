#!/bin/bash

# LegalJava Prototype Testing Script
# This script helps you start services individually for development testing

echo "🚀 LegalJava Prototype Testing Helper"
echo "====================================="

# Function to check if a port is in use
check_port() {
    local port=$1
    if netstat -an | grep ":$port " > /dev/null 2>&1; then
        echo "⚠️  Port $port is already in use"
        return 1
    else
        echo "✅ Port $port is available"
        return 0
    fi
}

# Function to start database
start_database() {
    echo ""
    echo "📊 Starting PostgreSQL Database..."
    echo "-----------------------------------"
    
    check_port 5432
    if [ $? -eq 0 ]; then
        echo "Starting database container..."
        docker-compose up -d db
        echo "✅ Database started on port 5432"
        echo "   - Database: legaljava"
        echo "   - User: postgres"
        echo "   - Password: legaljava"
    fi
}

# Function to start backend
start_backend() {
    echo ""
    echo "☕ Starting Spring Boot Backend..."
    echo "-----------------------------------"
    
    check_port 8080
    if [ $? -eq 0 ]; then
        echo "Choose backend startup method:"
        echo "1) Docker container (recommended for testing)"
        echo "2) Native Gradle run (for development)"
        read -p "Enter choice (1-2): " choice
        
        case $choice in
            1)
                echo "Starting backend container..."
                docker-compose up -d backend
                ;;
            2)
                echo "Starting backend with Gradle..."
                cd backend
                ./gradlew bootRun &
                cd ..
                ;;
            *)
                echo "Invalid choice. Skipping backend."
                return
                ;;
        esac
        echo "✅ Backend will be available on port 8080"
    fi
}

# Function to start Python RAG service
start_rag_service() {
    echo ""
    echo "🐍 Starting Python RAG Service..."
    echo "-----------------------------------"
    
    check_port 8000
    if [ $? -eq 0 ]; then
        echo "Choose RAG service startup method:"
        echo "1) Docker container (recommended for testing)"
        echo "2) Native Python run (for development)"
        read -p "Enter choice (1-2): " choice
        
        case $choice in
            1)
                echo "Starting RAG service container..."
                docker-compose up -d py-rag
                ;;
            2)
                echo "Starting RAG service with Python..."
                cd py-rag
                if [ -f "requirements.txt" ]; then
                    pip install -r requirements.txt
                fi
                uvicorn main:app --reload --host 0.0.0.0 --port 8000 &
                cd ..
                ;;
            *)
                echo "Invalid choice. Skipping RAG service."
                return
                ;;
        esac
        echo "✅ RAG service will be available on port 8000"
    fi
}

# Function to start frontend
start_frontend() {
    echo ""
    echo "⚛️  Starting React Frontend..."
    echo "-----------------------------------"
    
    check_port 5173
    if [ $? -eq 0 ]; then
        echo "Choose frontend startup method:"
        echo "1) Docker container"
        echo "2) Native npm run (recommended for development)"
        read -p "Enter choice (1-2): " choice
        
        case $choice in
            1)
                echo "Starting frontend container..."
                docker-compose up -d frontend
                ;;
            2)
                echo "Starting frontend with npm..."
                cd ui
                if [ ! -d "node_modules" ]; then
                    echo "Installing dependencies..."
                    npm install
                fi
                npm run dev &
                cd ..
                ;;
            *)
                echo "Invalid choice. Skipping frontend."
                return
                ;;
        esac
        echo "✅ Frontend will be available on port 5173"
    fi
}

# Function to show status
show_status() {
    echo ""
    echo "🔍 Service Status"
    echo "=================="
    echo "Database (5432):  $(curl -s http://localhost:5432 > /dev/null 2>&1 && echo "✅ Running" || echo "❌ Not running")"
    echo "Backend (8080):   $(curl -s http://localhost:8080/actuator/health > /dev/null 2>&1 && echo "✅ Running" || echo "❌ Not running")"
    echo "RAG Service (8000): $(curl -s http://localhost:8000/health > /dev/null 2>&1 && echo "✅ Running" || echo "❌ Not running")"
    echo "Frontend (5173):  $(curl -s http://localhost:5173 > /dev/null 2>&1 && echo "✅ Running" || echo "❌ Not running")"
}

# Function to stop all services
stop_all() {
    echo ""
    echo "🛑 Stopping all services..."
    echo "============================"
    docker-compose down
    pkill -f "gradle"
    pkill -f "uvicorn"
    pkill -f "npm run dev"
    echo "✅ All services stopped"
}

# Main menu
while true; do
    echo ""
    echo "Choose an option:"
    echo "1) Start Database only"
    echo "2) Start Backend only"
    echo "3) Start RAG Service only"
    echo "4) Start Frontend only"
    echo "5) Start All Services (step by step)"
    echo "6) Start All Services (Docker Compose)"
    echo "7) Show Service Status"
    echo "8) Stop All Services"
    echo "9) Exit"
    echo ""
    read -p "Enter your choice (1-9): " choice

    case $choice in
        1) start_database ;;
        2) start_backend ;;
        3) start_rag_service ;;
        4) start_frontend ;;
        5) 
            start_database
            sleep 3
            start_backend
            sleep 3
            start_rag_service
            sleep 3
            start_frontend
            ;;
        6)
            echo "Starting all services with Docker Compose..."
            docker-compose up -d
            echo "✅ All services started"
            ;;
        7) show_status ;;
        8) stop_all ;;
        9) 
            echo "Goodbye! 👋"
            exit 0
            ;;
        *)
            echo "Invalid choice. Please try again."
            ;;
    esac
done
