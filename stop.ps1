# 🛑 SCRIPT DE DETENCIÓN - WINDOWS POWERSHELL
# Detiene todos los servicios de la Clínica Veterinaria

Write-Host "═══════════════════════════════════════════════════════" -ForegroundColor Cyan
Write-Host "🛑 CLÍNICA VETERINARIA - DETENCIÓN DE SERVICIOS" -ForegroundColor Yellow
Write-Host "═══════════════════════════════════════════════════════" -ForegroundColor Cyan
Write-Host ""

Write-Host "⏸️  Deteniendo contenedores..." -ForegroundColor Yellow
docker-compose -f docker-compose.full.yml down

Write-Host ""
Write-Host "✅ Servicios detenidos exitosamente" -ForegroundColor Green
Write-Host ""
Write-Host "💡 Para limpiar TODO (incluyendo datos de MySQL):" -ForegroundColor White
Write-Host "   docker-compose -f docker-compose.full.yml down -v" -ForegroundColor Gray
Write-Host ""
Write-Host "═══════════════════════════════════════════════════════" -ForegroundColor Cyan

