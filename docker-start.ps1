Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "MySQL:   " -NoNewline -ForegroundColor White
Write-Host "http://localhost:3306" -ForegroundColor Yellow
Write-Host "Backend: " -NoNewline -ForegroundColor White
Write-Host "http://localhost:8080" -ForegroundColor Yellow
Write-Host ""
Write-Host "Ver logs en tiempo real:" -ForegroundColor White
Write-Host "  docker-compose logs -f" -ForegroundColor Gray
Write-Host ""
Write-Host "Probar health check:" -ForegroundColor White
Write-Host "  curl http://localhost:8080/actuator/health" -ForegroundColor Gray
Write-Host ""
Write-Host "Parar servicios:" -ForegroundColor White
Write-Host "  docker-compose down" -ForegroundColor Gray
Write-Host ""
# ====================================================================
# Script PowerShell - Inicio Rápido Docker
# ====================================================================
# Proyecto: Clínica Veterinaria Backend
# ====================================================================

Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  DOCKER - Clínica Veterinaria Backend" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Verificar .env
if (-not (Test-Path ".env")) {
    Write-Host "[ERROR] No se encontró archivo .env" -ForegroundColor Red
    Write-Host ""
    Write-Host "Crea el archivo .env copiando .env.example:" -ForegroundColor Yellow
    Write-Host "  Copy-Item .env.example .env" -ForegroundColor White
    Write-Host ""
    Write-Host "Luego edita .env con tus credenciales reales" -ForegroundColor Yellow
    exit 1
}

# Verificar Docker
Write-Host "[1/5] Verificando Docker..." -ForegroundColor White
try {
    docker --version | Out-Null
    Write-Host "[OK] Docker encontrado" -ForegroundColor Green
} catch {
    Write-Host "[ERROR] Docker no está instalado o no está en PATH" -ForegroundColor Red
    exit 1
}

# Construir imágenes
Write-Host ""
Write-Host "[2/5] Construyendo imágenes..." -ForegroundColor White
docker-compose build
if ($LASTEXITCODE -ne 0) {
    Write-Host "[ERROR] Falló la construcción" -ForegroundColor Red
    exit 1
}

# Levantar servicios
Write-Host ""
Write-Host "[3/5] Levantando contenedores..." -ForegroundColor White
docker-compose up -d
if ($LASTEXITCODE -ne 0) {
    Write-Host "[ERROR] Falló al levantar contenedores" -ForegroundColor Red
    exit 1
}

# Esperar
Write-Host ""
Write-Host "[4/5] Esperando que los servicios estén listos (30 segundos)..." -ForegroundColor White
Start-Sleep -Seconds 30

# Verificar estado
Write-Host ""
Write-Host "[5/5] Verificando estado de servicios..." -ForegroundColor White
docker-compose ps

Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  SERVICIOS INICIADOS" -ForegroundColor Green

