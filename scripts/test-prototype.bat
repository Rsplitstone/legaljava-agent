@echo off
setlocal enabledelayedexpansion

echo.
echo 🚀 LegalJava Prototype Testing Helper
echo =====================================

:menu
echo.
echo Choose an option:
echo 1) Start Database only
echo 2) Start Backend only  
echo 3) Start RAG Service only
echo 4) Start Frontend only
echo 5) Start All Services (Docker Compose)
echo 6) Show Service Status
echo 7) Stop All Services
echo 8) Exit
echo.
set /p choice="Enter your choice (1-8): "

if "%choice%"=="1" goto start_database
if "%choice%"=="2" goto start_backend
if "%choice%"=="3" goto start_rag
if "%choice%"=="4" goto start_frontend
if "%choice%"=="5" goto start_all
if "%choice%"=="6" goto show_status
if "%choice%"=="7" goto stop_all
if "%choice%"=="8" goto exit
echo Invalid choice. Please try again.
goto menu

:start_database
echo.
echo 📊 Starting PostgreSQL Database...
echo -----------------------------------
docker-compose up -d db
echo ✅ Database started on port 5432
goto menu

:start_backend
echo.
echo ☕ Starting Spring Boot Backend...
echo -----------------------------------
echo Choose startup method:
echo 1) Docker container
echo 2) Native Gradle run
set /p method="Enter choice (1-2): "
if "%method%"=="1" (
    docker-compose up -d backend
) else if "%method%"=="2" (
    cd backend
    start "" gradlew bootRun
    cd ..
)
echo ✅ Backend will be available on port 8080
goto menu

:start_rag
echo.
echo 🐍 Starting Python RAG Service...
echo -----------------------------------
echo Choose startup method:
echo 1) Docker container
echo 2) Native Python run
set /p method="Enter choice (1-2): "
if "%method%"=="1" (
    docker-compose up -d py-rag
) else if "%method%"=="2" (
    cd py-rag
    start "" uvicorn main:app --reload --host 0.0.0.0 --port 8000
    cd ..
)
echo ✅ RAG service will be available on port 8000
goto menu

:start_frontend
echo.
echo ⚛️ Starting React Frontend...
echo -----------------------------------
echo Choose startup method:
echo 1) Docker container
echo 2) Native npm run
set /p method="Enter choice (1-2): "
if "%method%"=="1" (
    docker-compose up -d frontend
) else if "%method%"=="2" (
    cd ui
    if not exist "node_modules" npm install
    start "" npm run dev
    cd ..
)
echo ✅ Frontend will be available on port 5173
goto menu

:start_all
echo.
echo 🚀 Starting all services with Docker Compose...
echo ===============================================
docker-compose up -d
echo ✅ All services started
echo.
echo 🌐 Access URLs:
echo   - Frontend: http://localhost:5173
echo   - Backend API: http://localhost:8080
echo   - RAG Service: http://localhost:8000
echo   - Database: localhost:5432
goto menu

:show_status
echo.
echo 🔍 Service Status
echo ==================
docker-compose ps
goto menu

:stop_all
echo.
echo 🛑 Stopping all services...
echo ============================
docker-compose down
taskkill /f /im "gradle.exe" 2>nul
taskkill /f /im "java.exe" 2>nul
taskkill /f /im "python.exe" 2>nul
taskkill /f /im "node.exe" 2>nul
echo ✅ All services stopped
goto menu

:exit
echo Goodbye! 👋
exit /b 0
