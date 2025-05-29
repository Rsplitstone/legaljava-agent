@echo off
REM LegalJava Project Startup Script for Windows

echo 🏛️  Starting LegalJava - AI-Powered Legal Assistant
echo =================================================

REM Navigate to project root
cd /d "%~dp0\.."

REM Check if .env file exists
if not exist ".env" (
    echo ⚠️  No .env file found. Creating one from .env.example...
    if exist ".env.example" (
        copy .env.example .env
    ) else (
        echo OPENAI_API_KEY=your_openai_api_key_here > .env
    )
    echo 📝 Please edit .env file and add your OPENAI_API_KEY
    echo    Then run this script again.
    pause
    exit /b 1
)

REM Check if OpenAI API key is set
findstr /C:"your_openai_api_key_here" .env >nul
if %errorlevel% equ 0 (
    echo ⚠️  Please set your OPENAI_API_KEY in the .env file
    pause
    exit /b 1
)

echo 🐳 Starting services with Docker Compose...
docker-compose up --build -d

echo ⏳ Waiting for services to start...
timeout /t 10 /nobreak >nul

echo 🔍 Checking service health...

echo 🎉 LegalJava is starting up!
echo.
echo 📱 Frontend: http://localhost:5173
echo 🔧 Backend API: http://localhost:8080/api
echo 🤖 RAG Service: http://localhost:8000
echo 🗄️  Database: localhost:5432
echo.
echo 📖 View logs: docker-compose logs -f
echo 🛑 Stop services: docker-compose down
echo.
pause
