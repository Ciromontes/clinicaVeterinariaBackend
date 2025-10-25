# ===================================================================
# SCRIPT DE INICIO - WINDOWS POWERSHELL
# Inicia todos los servicios de la Clinica Veterinaria
# ===================================================================

Write-Host "===================================================================" -ForegroundColor Cyan
Write-Host " CLINICA VETERINARIA - INICIO DE SERVICIOS" -ForegroundColor Green
Write-Host "===================================================================" -ForegroundColor Cyan
Write-Host ""

# Detener servicios anteriores si existen
Write-Host "[INFO] Deteniendo servicios anteriores..." -ForegroundColor Yellow
docker-compose down 2>$null
docker stop frontend-test 2>$null
docker rm frontend-test 2>$null

# Limpiar contenedores huerfanos
Write-Host "[INFO] Limpiando contenedores huerfanos..." -ForegroundColor Yellow
docker-compose -f docker-compose.full.yml down --remove-orphans 2>$null

Write-Host ""
Write-Host "[BUILD] Construyendo imagenes Docker..." -ForegroundColor Cyan
docker-compose -f docker-compose.full.yml --env-file .env.local build

Write-Host ""
Write-Host "[START] Iniciando servicios..." -ForegroundColor Cyan
docker-compose -f docker-compose.full.yml --env-file .env.local up -d

Write-Host ""
Write-Host "[WAIT] Esperando que los servicios esten listos..." -ForegroundColor Yellow
Start-Sleep -Seconds 15

Write-Host ""
Write-Host "===================================================================" -ForegroundColor Cyan
Write-Host " SERVICIOS INICIADOS EXITOSAMENTE" -ForegroundColor Green
Write-Host "===================================================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "[STATUS] Estado de los servicios:" -ForegroundColor White
docker-compose -f docker-compose.full.yml ps

Write-Host ""
Write-Host "[URLS] URLs de acceso:" -ForegroundColor White
Write-Host "   Frontend:    " -NoNewline; Write-Host "http://localhost:3000" -ForegroundColor Cyan
Write-Host "   Backend API: " -NoNewline; Write-Host "http://localhost:8080/api" -ForegroundColor Cyan
Write-Host "   MySQL:       " -NoNewline; Write-Host "localhost:3306" -ForegroundColor Cyan

Write-Host ""
Write-Host "[CREDENCIALES] Usuarios de prueba:" -ForegroundColor White
Write-Host "   ADMIN:       " -NoNewline; Write-Host "admin@clinicaveterinaria.com / admin123" -ForegroundColor Yellow
Write-Host "   VETERINARIO: " -NoNewline; Write-Host "ana.vet@clinicaveterinaria.com / vet123" -ForegroundColor Yellow
Write-Host "   CLIENTE:     " -NoNewline; Write-Host "lucia.cliente@clinicaveterinaria.com / cliente123" -ForegroundColor Yellow

Write-Host ""
Write-Host "[COMANDOS] Comandos utiles:" -ForegroundColor White
Write-Host "   Ver logs:  " -NoNewline; Write-Host "docker-compose -f docker-compose.full.yml logs -f" -ForegroundColor Gray
Write-Host "   Detener:   " -NoNewline; Write-Host ".\stop.ps1" -ForegroundColor Gray
Write-Host "   Reiniciar: " -NoNewline; Write-Host ".\start.ps1" -ForegroundColor Gray

Write-Host ""
Write-Host "[NAVEGADOR] Abriendo frontend en el navegador..." -ForegroundColor Green
Start-Sleep -Seconds 3
Start-Process "http://localhost:3000"

Write-Host ""
Write-Host "===================================================================" -ForegroundColor Cyan

