@echo off
REM ================================================================
REM Script de Corrección de Seguridad - Clínica Veterinaria
REM ================================================================
REM Este script limpia archivos sensibles del repositorio Git
REM ¡¡¡ADVERTENCIA!!! Este script modifica el historial de Git
REM ================================================================

echo.
echo ========================================
echo  CORRECCION DE SEGURIDAD - GIT
echo ========================================
echo.
echo ADVERTENCIA: Este script va a:
echo 1. Eliminar application.properties de Git
echo 2. Eliminar archivos sensibles del historial
echo 3. Requerir force push si ya subiste cambios
echo.
echo Asegurate de:
echo - Tener BACKUP de tu base de datos
echo - Tener BACKUP de tu application.properties local
echo - Avisar al equipo antes de hacer force push
echo.
set /p CONTINUAR="Deseas continuar? (SI/no): "

if /i not "%CONTINUAR%"=="SI" (
    echo.
    echo Operacion cancelada.
    pause
    exit /b 1
)

cd /d "%~dp0"

REM Verificar si existe .git
if not exist ".git" (
    echo.
    echo [ERROR] No se encontro repositorio Git
    pause
    exit /b 1
)

echo.
echo [1/5] Haciendo backup de application.properties...
if exist "src\main\resources\application.properties" (
    copy "src\main\resources\application.properties" "application.properties.backup.%date:~-4,4%%date:~-7,2%%date:~-10,2%.txt"
    echo [OK] Backup creado: application.properties.backup.*.txt
) else (
    echo [INFO] No se encontro application.properties para backup
)

echo.
echo [2/5] Eliminando application.properties del staging y working tree...
git rm --cached src/main/resources/application.properties 2>nul
if %errorlevel% equ 0 (
    echo [OK] Archivo removido del staging
) else (
    echo [INFO] El archivo no estaba en staging
)

echo.
echo [3/5] Verificando si .gitignore existe...
if exist ".gitignore" (
    echo [OK] .gitignore existe
) else (
    echo [ERROR] .gitignore no existe - debe crearse primero
    pause
    exit /b 1
)

echo.
echo [4/5] Limpiando historial de Git (esto puede tardar)...
echo.
echo Eliminando application.properties del historial completo...

REM Usar filter-branch para eliminar del historial
git filter-branch --force --index-filter "git rm --cached --ignore-unmatch src/main/resources/application.properties" --prune-empty --tag-name-filter cat -- --all

if %errorlevel% equ 0 (
    echo [OK] Historial limpiado exitosamente
) else (
    echo [ERROR] Hubo un problema al limpiar el historial
    pause
    exit /b 1
)

echo.
echo [5/5] Limpiando referencias y optimizando repositorio...
git for-each-ref --format="delete %%(refname)" refs/original | git update-ref --stdin
git reflog expire --expire=now --all
git gc --prune=now --aggressive

echo.
echo ========================================
echo  CORRECCION COMPLETADA
echo ========================================
echo.
echo PROXIMOS PASOS CRITICOS:
echo.
echo 1. CAMBIAR PASSWORD DE MYSQL:
echo    mysql -u root -p
echo    ALTER USER 'root'@'localhost' IDENTIFIED BY 'NuevaPassword';
echo.
echo 2. GENERAR NUEVO JWT SECRET (32+ caracteres aleatorios)
echo.
echo 3. Actualizar application.properties local con nuevas credenciales
echo.
echo 4. Agregar y commitear .gitignore:
echo    git add .gitignore
echo    git add src/main/resources/application.properties.example
echo    git commit -m "feat: Agregar .gitignore y proteger credenciales"
echo.
echo 5. Si ya hiciste push al remoto, necesitas FORCE PUSH:
echo    git push origin --force --all
echo    git push origin --force --tags
echo.
echo    IMPORTANTE: Avisa al equipo que deben hacer:
echo    git fetch origin
echo    git reset --hard origin/main
echo.
echo 6. Verificar que funciono:
echo    git log --all --full-history -- "**/application.properties"
echo    (No debe mostrar nada)
echo.

pause

