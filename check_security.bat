@echo off
REM ================================================================
REM Script de Verificación de Seguridad - Clínica Veterinaria
REM ================================================================
REM Este script verifica si hay archivos sensibles en Git
REM Ejecutar desde: D:\CopiaF\...\gestion-citas\
REM ================================================================

echo.
echo ========================================
echo  VERIFICACION DE SEGURIDAD - GIT
echo ========================================
echo.

cd /d "%~dp0"

REM Verificar si existe .git
if not exist ".git" (
    echo [OK] No hay repositorio Git inicializado todavia
    echo.
    echo Cuando inicialices Git, asegurate de:
    echo 1. Agregar .gitignore PRIMERO
    echo 2. NUNCA agregar application.properties
    echo.
    pause
    exit /b 0
)

echo [INFO] Repositorio Git encontrado. Analizando...
echo.

REM Verificar .gitignore
echo [1/6] Verificando .gitignore...
if exist ".gitignore" (
    echo [OK] .gitignore existe
) else (
    echo [ERROR] .gitignore NO existe - CREAR INMEDIATAMENTE
)
echo.

REM Verificar application.properties en Git
echo [2/6] Verificando si application.properties esta en Git...
git ls-files | findstr "application.properties" >nul 2>&1
if %errorlevel% equ 0 (
    echo [CRITICO] application.properties ESTA en Git - ACCION REQUERIDA
    echo.
    echo Archivos .properties encontrados:
    git ls-files | findstr ".properties"
    echo.
    echo SOLUCION: Ejecutar fix_security.bat
) else (
    echo [OK] application.properties NO esta en Git
)
echo.

REM Buscar credenciales en el código
echo [3/6] Buscando posibles credenciales expuestas...
git grep -i "password=" -- "*.properties" "*.java" "*.yml" >nul 2>&1
if %errorlevel% equ 0 (
    echo [ADVERTENCIA] Se encontraron referencias a passwords:
    git grep -i "password=" -- "*.properties" "*.java" "*.yml"
) else (
    echo [OK] No se encontraron passwords en archivos trackeados
)
echo.

REM Verificar archivos sensibles
echo [4/6] Verificando otros archivos sensibles...
set SENSIBLE_ENCONTRADO=0

for %%f in (*.sql *.dump *.key *.pem .env) do (
    git ls-files | findstr "%%f" >nul 2>&1
    if not errorlevel 1 (
        echo [ERROR] Archivo sensible encontrado: %%f
        set SENSIBLE_ENCONTRADO=1
    )
)

if %SENSIBLE_ENCONTRADO% equ 0 (
    echo [OK] No se encontraron archivos sensibles en Git
)
echo.

REM Verificar historial
echo [5/6] Verificando historial de commits con archivos sensibles...
git log --all --full-history --oneline -- "**/application.properties" 2>nul | findstr "." >nul
if %errorlevel% equ 0 (
    echo [CRITICO] application.properties ESTUVO en Git anteriormente
    echo.
    echo Historial encontrado:
    git log --all --full-history --oneline -- "**/application.properties"
    echo.
    echo DEBE LIMPIAR EL HISTORIAL - Ver SECURITY_GUIDE.md
) else (
    echo [OK] No hay historial de application.properties
)
echo.

REM Verificar staging area
echo [6/6] Verificando archivos en staging...
git diff --cached --name-only | findstr "application.properties" >nul 2>&1
if %errorlevel% equ 0 (
    echo [ADVERTENCIA] application.properties esta en staging
    echo Ejecutar: git reset HEAD src/main/resources/application.properties
) else (
    echo [OK] No hay archivos sensibles en staging
)
echo.

echo ========================================
echo  VERIFICACION COMPLETADA
echo ========================================
echo.
echo Revisa los resultados arriba.
echo.
echo Si encontraste errores CRITICOS:
echo 1. Lee SECURITY_GUIDE.md
echo 2. Ejecuta fix_security.bat
echo 3. Cambia tus passwords de BD y JWT secret
echo.

pause

