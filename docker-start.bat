@echo off
REM ====================================================================
REM Script de Inicio Rápido - Docker Backend
REM ====================================================================
REM Proyecto: Clínica Veterinaria
REM ====================================================================

echo.
echo ========================================
echo  DOCKER - Clínica Veterinaria Backend
echo ========================================
echo.

REM Verificar si existe .env
if not exist ".env" (
    echo [ERROR] No se encontro archivo .env
    echo.
    echo Crea el archivo .env copiando .env.example:
    echo   copy .env.example .env
    echo.
    echo Luego edita .env con tus credenciales reales
    pause
    exit /b 1
)

echo [1/5] Verificando Docker...
docker --version >nul 2>&1
if errorlevel 1 (
    echo [ERROR] Docker no esta instalado o no esta en PATH
    pause
    exit /b 1
)
echo [OK] Docker encontrado

echo.
echo [2/5] Construyendo imagenes...
docker-compose build

if errorlevel 1 (
    echo [ERROR] Fallo la construccion
    pause
    exit /b 1
)

echo.
echo [3/5] Levantando contenedores...
docker-compose up -d

if errorlevel 1 (
    echo [ERROR] Fallo al levantar contenedores
    pause
    exit /b 1
)

echo.
echo [4/5] Esperando que los servicios esten listos...
timeout /t 10 /nobreak >nul

echo.
echo [5/5] Verificando estado de servicios...
docker-compose ps

echo.
echo ========================================
echo  SERVICIOS INICIADOS
echo ========================================
echo.
echo MySQL:   http://localhost:3306
echo Backend: http://localhost:8080
echo.
echo Ver logs en tiempo real:
echo   docker-compose logs -f
echo.
echo Probar health check:
echo   curl http://localhost:8080/actuator/health
echo.
echo Parar servicios:
echo   docker-compose down
echo.
pause

