# 🚀 SCRIPT DE DESPLIEGUE A AZURE - CLÍNICA VETERINARIA
# Automatiza el despliegue completo en Azure App Service

param(
    [string]$Action = "deploy",  # deploy, setup, teardown
    [string]$Version = "latest"
)

# ========== CONFIGURACIÓN ==========
$RESOURCE_GROUP = "rg-vetclinic-prod"
$LOCATION = "eastus"
$APP_SERVICE_PLAN = "asp-vetclinic"
$APP_NAME = "vetclinic-app-2025"  # Cambiar por un nombre único
$REGISTRY_NAME = "acrvetclinic2025"  # Cambiar por un nombre único

# Colores
$ColorInfo = "Cyan"
$ColorSuccess = "Green"
$ColorWarning = "Yellow"
$ColorError = "Red"

# ========== FUNCIONES ==========

function Write-Step {
    param([string]$Message)
    Write-Host "`n================================================" -ForegroundColor $ColorInfo
    Write-Host $Message -ForegroundColor $ColorInfo
    Write-Host "================================================`n" -ForegroundColor $ColorInfo
}

function Write-Success {
    param([string]$Message)
    Write-Host "✅ $Message" -ForegroundColor $ColorSuccess
}

function Write-Warning {
    param([string]$Message)
    Write-Host "⚠️  $Message" -ForegroundColor $ColorWarning
}

function Write-Error {
    param([string]$Message)
    Write-Host "❌ $Message" -ForegroundColor $ColorError
}

function Check-Prerequisites {
    Write-Step "Verificando prerequisitos..."

    # Azure CLI
    if (-not (Get-Command az -ErrorAction SilentlyContinue)) {
        Write-Error "Azure CLI no está instalado"
        Write-Host "Descarga desde: https://aka.ms/installazurecliwindows" -ForegroundColor Yellow
        exit 1
    }
    Write-Success "Azure CLI instalado"

    # Docker
    if (-not (Get-Command docker -ErrorAction SilentlyContinue)) {
        Write-Error "Docker no está instalado"
        exit 1
    }
    Write-Success "Docker instalado"

    # Verificar login en Azure
    $account = az account show 2>$null
    if (-not $account) {
        Write-Warning "No estás autenticado en Azure"
        Write-Host "Ejecutando az login..." -ForegroundColor Yellow
        az login
    } else {
        Write-Success "Autenticado en Azure"
    }
}

function Setup-Azure {
    Write-Step "Configurando recursos en Azure..."

    # 1. Crear Resource Group
    Write-Host "Creando Resource Group: $RESOURCE_GROUP..." -ForegroundColor Yellow
    az group create --name $RESOURCE_GROUP --location $LOCATION --output table
    Write-Success "Resource Group creado"

    # 2. Crear Azure Container Registry
    Write-Host "Creando Azure Container Registry: $REGISTRY_NAME..." -ForegroundColor Yellow
    az acr create `
        --resource-group $RESOURCE_GROUP `
        --name $REGISTRY_NAME `
        --sku Basic `
        --admin-enabled true `
        --output table
    Write-Success "Container Registry creado"

    # 3. Obtener credenciales
    Write-Host "Obteniendo credenciales del registry..." -ForegroundColor Yellow
    $global:ACR_USERNAME = az acr credential show --name $REGISTRY_NAME --query username -o tsv
    $global:ACR_PASSWORD = az acr credential show --name $REGISTRY_NAME --query "passwords[0].value" -o tsv
    Write-Success "Credenciales obtenidas"

    # 4. Crear App Service Plan
    Write-Host "Creando App Service Plan: $APP_SERVICE_PLAN (B1 Basic)..." -ForegroundColor Yellow
    az appservice plan create `
        --name $APP_SERVICE_PLAN `
        --resource-group $RESOURCE_GROUP `
        --is-linux `
        --sku B1 `
        --output table
    Write-Success "App Service Plan creado"

    # 5. Crear Web App
    Write-Host "Creando Web App: $APP_NAME..." -ForegroundColor Yellow
    az webapp create `
        --resource-group $RESOURCE_GROUP `
        --plan $APP_SERVICE_PLAN `
        --name $APP_NAME `
        --multicontainer-config-type compose `
        --multicontainer-config-file docker-compose.azure.yml `
        --output table
    Write-Success "Web App creada"

    # 6. Configurar variables de entorno
    Write-Host "Configurando variables de entorno..." -ForegroundColor Yellow
    az webapp config appsettings set `
        --resource-group $RESOURCE_GROUP `
        --name $APP_NAME `
        --settings `
            MYSQL_ROOT_PASSWORD="RootPass2025!Secure" `
            MYSQL_PASSWORD="VetClinic2025!Secure" `
            JWT_SECRET="jwt-secret-production-2025-azure-vetclinic-secure-key-$(Get-Random)" `
            WEBSITES_PORT=80 `
            DOCKER_REGISTRY="$REGISTRY_NAME.azurecr.io" `
            IMAGE_TAG=$Version `
            DOCKER_REGISTRY_SERVER_URL="https://$REGISTRY_NAME.azurecr.io" `
            DOCKER_REGISTRY_SERVER_USERNAME=$ACR_USERNAME `
            DOCKER_REGISTRY_SERVER_PASSWORD=$ACR_PASSWORD `
        --output table
    Write-Success "Variables configuradas"

    Write-Success "Setup completado!"
    Write-Host "`n🌐 URL de la aplicación: https://$APP_NAME.azurewebsites.net" -ForegroundColor Cyan
    Write-Host "📦 Container Registry: $REGISTRY_NAME.azurecr.io" -ForegroundColor Cyan
}

function Build-And-Push {
    Write-Step "Building y pushing imágenes a Azure Container Registry..."

    # Login en ACR
    Write-Host "Login en Azure Container Registry..." -ForegroundColor Yellow
    az acr login --name $REGISTRY_NAME

    # Build Backend
    Write-Host "`nBuilding imagen del Backend..." -ForegroundColor Yellow
    docker build -t "$REGISTRY_NAME.azurecr.io/backend:$Version" .
    if ($LASTEXITCODE -ne 0) {
        Write-Error "Fallo el build del backend"
        exit 1
    }
    Write-Success "Backend build exitoso"

    # Push Backend
    Write-Host "Pushing Backend..." -ForegroundColor Yellow
    docker push "$REGISTRY_NAME.azurecr.io/backend:$Version"
    Write-Success "Backend pushed"

    # Build Frontend
    Write-Host "`nBuilding imagen del Frontend..." -ForegroundColor Yellow
    docker build -t "$REGISTRY_NAME.azurecr.io/frontend:$Version" ./frontend-gestion-citas
    if ($LASTEXITCODE -ne 0) {
        Write-Error "Fallo el build del frontend"
        exit 1
    }
    Write-Success "Frontend build exitoso"

    # Push Frontend
    Write-Host "Pushing Frontend..." -ForegroundColor Yellow
    docker push "$REGISTRY_NAME.azurecr.io/frontend:$Version"
    Write-Success "Frontend pushed"
}

function Deploy-To-Azure {
    Write-Step "Desplegando a Azure..."

    # Actualizar configuración del contenedor
    Write-Host "Actualizando configuración del contenedor..." -ForegroundColor Yellow
    az webapp config container set `
        --resource-group $RESOURCE_GROUP `
        --name $APP_NAME `
        --multicontainer-config-type compose `
        --multicontainer-config-file docker-compose.azure.yml

    # Actualizar variable de versión
    az webapp config appsettings set `
        --resource-group $RESOURCE_GROUP `
        --name $APP_NAME `
        --settings IMAGE_TAG=$Version `
        --output none

    # Restart
    Write-Host "Reiniciando aplicación..." -ForegroundColor Yellow
    az webapp restart --name $APP_NAME --resource-group $RESOURCE_GROUP

    Write-Success "Despliegue completado!"
    Write-Host "`n🌐 Abriendo aplicación en el navegador..." -ForegroundColor Cyan
    Start-Process "https://$APP_NAME.azurewebsites.net"
}

function Show-Logs {
    Write-Step "Mostrando logs de la aplicación..."
    az webapp log tail --name $APP_NAME --resource-group $RESOURCE_GROUP
}

function Teardown {
    Write-Step "Eliminando recursos de Azure..."
    Write-Warning "Esto eliminará TODOS los recursos del Resource Group: $RESOURCE_GROUP"
    $confirm = Read-Host "¿Estás seguro? (escribe 'SI' para confirmar)"

    if ($confirm -eq "SI") {
        Write-Host "Eliminando Resource Group..." -ForegroundColor Yellow
        az group delete --name $RESOURCE_GROUP --yes --no-wait
        Write-Success "Eliminación iniciada (se completará en unos minutos)"
    } else {
        Write-Warning "Operación cancelada"
    }
}

function Show-Status {
    Write-Step "Estado de la aplicación..."

    # Estado del Resource Group
    Write-Host "`n📦 Resource Group:" -ForegroundColor Cyan
    az group show --name $RESOURCE_GROUP --query "{Name:name, Location:location, State:properties.provisioningState}" -o table 2>$null

    # Estado del App Service
    Write-Host "`n🌐 App Service:" -ForegroundColor Cyan
    az webapp show --name $APP_NAME --resource-group $RESOURCE_GROUP --query "{Name:name, State:state, URL:defaultHostName}" -o table 2>$null

    # Uso de costos
    Write-Host "`n💰 Estimación de costos:" -ForegroundColor Cyan
    Write-Host "  - App Service Plan (B1): ~$13/mes" -ForegroundColor Yellow
    Write-Host "  - Container Registry (Basic): ~$5/mes" -ForegroundColor Yellow
    Write-Host "  - MySQL (en contenedor): $0" -ForegroundColor Green
    Write-Host "  - TOTAL ESTIMADO: ~$18/mes" -ForegroundColor Yellow
}

# ========== EJECUCIÓN PRINCIPAL ==========

Write-Host @"
╔════════════════════════════════════════════════╗
║   🚀 AZURE DEPLOYMENT - CLÍNICA VETERINARIA   ║
║              Versión: $Version                 ║
╚════════════════════════════════════════════════╝
"@ -ForegroundColor Cyan

switch ($Action.ToLower()) {
    "setup" {
        Check-Prerequisites
        Setup-Azure
    }
    "build" {
        Check-Prerequisites
        Build-And-Push
    }
    "deploy" {
        Check-Prerequisites
        Build-And-Push
        Deploy-To-Azure
    }
    "logs" {
        Show-Logs
    }
    "status" {
        Show-Status
    }
    "teardown" {
        Teardown
    }
    "full" {
        Check-Prerequisites
        Setup-Azure
        Build-And-Push
        Deploy-To-Azure
        Show-Status
    }
    default {
        Write-Host @"
Uso: .\deploy-azure.ps1 -Action <acción> [-Version <version>]

Acciones disponibles:
  setup      - Crear todos los recursos en Azure (primera vez)
  build      - Build y push de imágenes Docker
  deploy     - Build + Push + Deploy (recomendado para actualizaciones)
  full       - Setup + Build + Deploy (completo desde cero)
  logs       - Ver logs en tiempo real
  status     - Ver estado y costos estimados
  teardown   - Eliminar todos los recursos de Azure

Ejemplos:
  .\deploy-azure.ps1 -Action full              # Primera vez
  .\deploy-azure.ps1 -Action deploy -Version v1.0.1  # Actualización
  .\deploy-azure.ps1 -Action logs              # Ver logs
  .\deploy-azure.ps1 -Action status            # Ver estado

"@ -ForegroundColor Yellow
    }
}

