# ================================================================
# Script para generar JWT Secret seguro
# ================================================================
# Genera un secret aleatorio de 40 caracteres
# Incluye: letras mayúsculas, minúsculas y números
# ================================================================

Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  GENERADOR DE JWT SECRET SEGURO" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Generar JWT secret aleatorio (40 caracteres)
$jwtSecret = -join ((48..57) + (65..90) + (97..122) | Get-Random -Count 40 | ForEach-Object {[char]$_})

Write-Host "Tu nuevo JWT Secret (40 caracteres):" -ForegroundColor Green
Write-Host ""
Write-Host $jwtSecret -ForegroundColor Yellow
Write-Host ""
Write-Host "Copia este valor y úsalo en application.properties" -ForegroundColor Green
Write-Host ""

# Copiar al portapapeles automáticamente
$jwtSecret | Set-Clipboard
Write-Host "[OK] JWT Secret copiado al portapapeles" -ForegroundColor Green
Write-Host ""

# Generar también contraseña de BD sugerida
$dbPassword = -join ((48..57) + (65..90) + (97..122) + (33,35,36,37,38,42,43,45,61,63,64) | Get-Random -Count 20 | ForEach-Object {[char]$_})

Write-Host "Contraseña de BD sugerida (20 caracteres):" -ForegroundColor Green
Write-Host ""
Write-Host $dbPassword -ForegroundColor Yellow
Write-Host ""
Write-Host "(Usa esta si aún no cambiaste la de MySQL)" -ForegroundColor Gray
Write-Host ""

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "PROXIMOS PASOS:" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "1. Actualiza src/main/resources/application.properties:" -ForegroundColor White
Write-Host "   spring.datasource.password=TU_NUEVA_PASSWORD" -ForegroundColor Gray
Write-Host "   jwt.secret=$jwtSecret" -ForegroundColor Gray
Write-Host ""
Write-Host "2. Verifica que application.properties NO está en Git:" -ForegroundColor White
Write-Host "   git status" -ForegroundColor Gray
Write-Host ""
Write-Host "3. Si aparece, eliminalo:" -ForegroundColor White
Write-Host "   git rm --cached src/main/resources/application.properties" -ForegroundColor Gray
Write-Host ""

Write-Host "IMPORTANTE: Guarda estos valores en un lugar seguro" -ForegroundColor Red
Write-Host ""

