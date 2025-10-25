# 🚀 GUÍA DE DESPLIEGUE EN AZURE - CLÍNICA VETERINARIA

## 📋 PREREQUISITOS

### 1. Cuenta de Azure para Estudiantes
- ✅ $100 USD de crédito gratuito
- ✅ Sin necesidad de tarjeta de crédito
- 🔗 [Portal Azure](https://portal.azure.com)

### 2. Herramientas necesarias
- ✅ Azure CLI instalado
- ✅ Docker Desktop corriendo
- ✅ Git configurado

---

## 🎯 ARQUITECTURA DE DESPLIEGUE

```
┌─────────────────────────────────────────────────┐
│  Azure App Service (Multi-Container)           │
│  Plan: B1 Basic ($13/mes)                       │
│                                                 │
│  ┌──────────────┐  ┌──────────────┐            │
│  │   NGINX      │  │   Backend    │            │
│  │  (Landing +  │→ │  Spring Boot │            │
│  │   Frontend)  │  │  (Port 8080) │            │
│  │  (Port 80)   │  └──────────────┘            │
│  └──────────────┘          ↓                   │
│                    ┌──────────────┐            │
│                    │    MySQL     │            │
│                    │  (Port 3306) │            │
│                    └──────────────┘            │
│                                                 │
└─────────────────────────────────────────────────┘
         ↑
    Internet
```

**Ventajas:**
- ✅ Todo en un solo servicio = **menor costo**
- ✅ Comunicación interna rápida (sin latencia de red)
- ✅ Fácil rollback y gestión
- ✅ Un solo dominio (ejemplo: `vetclinic.azurewebsites.net`)

---

## 📦 PASO 1: PREPARAR DOCKER-COMPOSE PARA AZURE

### 1.1 Crear `docker-compose.azure.yml`

Este archivo está optimizado para Azure (sin puertos externos innecesarios):

```yaml
version: '3.8'

services:
  # Base de datos
  mysql:
    image: mysql:8.0
    restart: always
    environment:
      MYSQL_ROOT_PASSWORD: ${MYSQL_ROOT_PASSWORD}
      MYSQL_DATABASE: gestion_citas
      MYSQL_USER: vetclinic
      MYSQL_PASSWORD: ${MYSQL_PASSWORD}
    volumes:
      - mysql-data:/var/lib/mysql
    networks:
      - vetclinic-network
    healthcheck:
      test: ["CMD", "mysqladmin", "ping", "-h", "localhost"]
      interval: 10s
      timeout: 5s
      retries: 5

  # Backend
  backend:
    build: .
    restart: always
    environment:
      SPRING_DATASOURCE_URL: jdbc:mysql://mysql:3306/gestion_citas?useSSL=false&serverTimezone=UTC
      SPRING_DATASOURCE_USERNAME: vetclinic
      SPRING_DATASOURCE_PASSWORD: ${MYSQL_PASSWORD}
      JWT_SECRET: ${JWT_SECRET}
      CORS_ALLOWED_ORIGINS: https://${WEBSITE_HOSTNAME}
    depends_on:
      mysql:
        condition: service_healthy
    networks:
      - vetclinic-network

  # Frontend + Landing (Nginx)
  frontend:
    build: 
      context: ./frontend-gestion-citas
      args:
        VITE_API_URL: /api
    restart: always
    ports:
      - "80:80"
    depends_on:
      - backend
    networks:
      - vetclinic-network

volumes:
  mysql-data:

networks:
  vetclinic-network:
    driver: bridge
```

---

## 🔧 PASO 2: AJUSTAR CONFIGURACIÓN DE NGINX

### 2.1 Actualizar `nginx.conf` para producción

El frontend debe servir también la landing page y hacer proxy al backend:

```nginx
server {
    listen 80;
    server_name _;

    # Landing page (ruta raíz)
    location = / {
        alias /usr/share/nginx/html/landing/;
        try_files /index.html =404;
    }

    location /landing/ {
        alias /usr/share/nginx/html/landing/;
        try_files $uri $uri/ /landing/index.html;
    }

    # Frontend (app de gestión)
    location /app/ {
        alias /usr/share/nginx/html/app/;
        try_files $uri $uri/ /app/index.html;
    }

    # API (proxy al backend)
    location /api/ {
        proxy_pass http://backend:8080/api/;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }

    # Health check
    location /health {
        access_log off;
        return 200 "OK";
        add_header Content-Type text/plain;
    }
}
```

---

## 🚀 PASO 3: DESPLEGAR EN AZURE

### 3.1 Instalar Azure CLI (si no lo tienes)

```powershell
# Descargar e instalar desde:
# https://aka.ms/installazurecliwindows

# Verificar instalación
az --version
```

### 3.2 Iniciar sesión en Azure

```powershell
az login
```

### 3.3 Crear recursos en Azure

```powershell
# Variables de configuración
$RESOURCE_GROUP="rg-vetclinic-prod"
$LOCATION="eastus"
$APP_SERVICE_PLAN="asp-vetclinic"
$APP_NAME="vetclinic-app"  # Debe ser único globalmente
$REGISTRY_NAME="acrvetclinic"  # Debe ser único

# 1. Crear Resource Group
az group create --name $RESOURCE_GROUP --location $LOCATION

# 2. Crear Azure Container Registry (ACR)
az acr create `
  --resource-group $RESOURCE_GROUP `
  --name $REGISTRY_NAME `
  --sku Basic `
  --admin-enabled true

# 3. Obtener credenciales del registry
$ACR_USERNAME=$(az acr credential show --name $REGISTRY_NAME --query username -o tsv)
$ACR_PASSWORD=$(az acr credential show --name $REGISTRY_NAME --query "passwords[0].value" -o tsv)

# 4. Crear App Service Plan (B1 Basic)
az appservice plan create `
  --name $APP_SERVICE_PLAN `
  --resource-group $RESOURCE_GROUP `
  --is-linux `
  --sku B1

# 5. Crear Web App con contenedor multi-contenedor
az webapp create `
  --resource-group $RESOURCE_GROUP `
  --plan $APP_SERVICE_PLAN `
  --name $APP_NAME `
  --multicontainer-config-type compose `
  --multicontainer-config-file docker-compose.azure.yml

# 6. Configurar variables de entorno
az webapp config appsettings set `
  --resource-group $RESOURCE_GROUP `
  --name $APP_NAME `
  --settings `
    MYSQL_ROOT_PASSWORD="RootPass2025!" `
    MYSQL_PASSWORD="VetClinic2025!" `
    JWT_SECRET="jwt-secret-production-2025-azure-vetclinic-secure-key" `
    WEBSITES_PORT=80 `
    DOCKER_REGISTRY_SERVER_URL="https://$REGISTRY_NAME.azurecr.io" `
    DOCKER_REGISTRY_SERVER_USERNAME=$ACR_USERNAME `
    DOCKER_REGISTRY_SERVER_PASSWORD=$ACR_PASSWORD

echo "✅ App creada: https://$APP_NAME.azurewebsites.net"
```

---

## 📤 PASO 4: BUILD Y PUSH DE IMÁGENES

### 4.1 Login en Azure Container Registry

```powershell
az acr login --name $REGISTRY_NAME
```

### 4.2 Build y push de imágenes

```powershell
# Backend
docker build -t $REGISTRY_NAME.azurecr.io/backend:latest .
docker push $REGISTRY_NAME.azurecr.io/backend:latest

# Frontend
docker build -t $REGISTRY_NAME.azurecr.io/frontend:latest ./frontend-gestion-citas
docker push $REGISTRY_NAME.azurecr.io/frontend:latest
```

### 4.3 Actualizar docker-compose.azure.yml con rutas del registry

Reemplaza los `build:` por `image:`:

```yaml
services:
  backend:
    image: acrvetclinic.azurecr.io/backend:latest
    # ... resto de config
  
  frontend:
    image: acrvetclinic.azurecr.io/frontend:latest
    # ... resto de config
```

### 4.4 Redesplegar

```powershell
az webapp config container set `
  --resource-group $RESOURCE_GROUP `
  --name $APP_NAME `
  --multicontainer-config-type compose `
  --multicontainer-config-file docker-compose.azure.yml

az webapp restart --name $APP_NAME --resource-group $RESOURCE_GROUP
```

---

## ✅ PASO 5: VERIFICACIÓN

### 5.1 Verificar logs

```powershell
# Ver logs en tiempo real
az webapp log tail --name $APP_NAME --resource-group $RESOURCE_GROUP

# Descargar logs
az webapp log download --name $APP_NAME --resource-group $RESOURCE_GROUP
```

### 5.2 Probar la aplicación

```powershell
# Abrir en el navegador
start https://$APP_NAME.azurewebsites.net
```

**Verificar:**
- ✅ Landing page carga en `/`
- ✅ App de gestión en `/app/`
- ✅ Login funciona con: `admin@clinicaveterinaria.com / admin123`
- ✅ Backend responde en `/api/actuator/health`

---

## 💰 PASO 6: MONITOREO DE COSTOS

### 6.1 Ver consumo de créditos

```powershell
# Ver uso de recursos
az consumption usage list --query "[].{Date:usageStart, Cost:pretaxCost, Service:meterName}" -o table
```

### 6.2 Configurar alertas de costo

```powershell
# Crear alerta cuando llegues a $50 USD
az monitor action-group create `
  --name "AlertaCostosEmail" `
  --resource-group $RESOURCE_GROUP `
  --short-name "CostAlert" `
  --email-receiver "email" "tu-email@ejemplo.com"
```

---

## 🔄 PASO 7: ACTUALIZACIONES (CI/CD MANUAL)

### Script de despliegue rápido: `deploy-azure.ps1`

```powershell
# deploy-azure.ps1
param(
    [string]$Version = "latest"
)

$REGISTRY_NAME="acrvetclinic"
$APP_NAME="vetclinic-app"
$RESOURCE_GROUP="rg-vetclinic-prod"

Write-Host "🚀 Desplegando versión: $Version" -ForegroundColor Cyan

# 1. Build
Write-Host "📦 Building images..." -ForegroundColor Yellow
docker build -t $REGISTRY_NAME.azurecr.io/backend:$Version .
docker build -t $REGISTRY_NAME.azurecr.io/frontend:$Version ./frontend-gestion-citas

# 2. Push
Write-Host "📤 Pushing to ACR..." -ForegroundColor Yellow
az acr login --name $REGISTRY_NAME
docker push $REGISTRY_NAME.azurecr.io/backend:$Version
docker push $REGISTRY_NAME.azurecr.io/frontend:$Version

# 3. Restart
Write-Host "🔄 Restarting app..." -ForegroundColor Yellow
az webapp restart --name $APP_NAME --resource-group $RESOURCE_GROUP

Write-Host "✅ Despliegue completado!" -ForegroundColor Green
Write-Host "🌐 URL: https://$APP_NAME.azurewebsites.net" -ForegroundColor Cyan
```

**Uso:**
```powershell
.\deploy-azure.ps1 -Version "v1.0.1"
```

---

## 🆘 TROUBLESHOOTING

### Problema: "No se puede conectar a MySQL"
```powershell
# Verificar que el contenedor MySQL está corriendo
az webapp log tail --name $APP_NAME --resource-group $RESOURCE_GROUP | Select-String "mysql"
```

### Problema: "Frontend no carga estilos"
- Verificar que el build de Vite generó los archivos en `/dist`
- Revisar nginx.conf tiene las rutas correctas

### Problema: "Error 502 Bad Gateway"
- El backend no está listo. Esperar 2-3 minutos después del despliegue
- Verificar logs del backend

---

## 🎓 ALTERNATIVA: AZURE STUDENT STARTER

Si quieres **GASTAR CERO** de tus créditos por ahora:

### Opción 1: Azure App Service Free Tier (F1)
```powershell
az appservice plan create `
  --name $APP_SERVICE_PLAN `
  --resource-group $RESOURCE_GROUP `
  --is-linux `
  --sku F1  # ¡GRATIS!
```

**Limitaciones:**
- Solo 60 minutos/día de CPU
- 1GB RAM
- No custom domain SSL
- **Perfecto para demos y desarrollo**

### Opción 2: Azure Container Instances (ACI)
Pagas solo por segundos de uso. Ideal para demos:

```powershell
az container create `
  --resource-group $RESOURCE_GROUP `
  --name vetclinic-aci `
  --image $REGISTRY_NAME.azurecr.io/frontend:latest `
  --cpu 1 --memory 1 `
  --ports 80 `
  --dns-name-label vetclinic-demo
```

**Costo:** ~$0.0000125 USD/segundo = ~$1.08/día cuando está corriendo

---

## 📊 RESUMEN DE COSTOS

| Servicio | Plan | Costo Mensual | Alternativa Gratis |
|----------|------|---------------|-------------------|
| App Service | B1 Basic | $13.14 | F1 Free (limitado) |
| Container Registry | Basic | $5 | Primeros 500 builds gratis |
| MySQL | En contenedor | $0 | ✅ Incluido |
| **TOTAL** | | **~$18/mes** | **~$0 con F1** |

**Con $100 USD de créditos = 5+ meses de hosting**

---

## ✅ CHECKLIST FINAL

- [ ] Azure CLI instalado y configurado
- [ ] Cuenta de Azure para estudiantes activa
- [ ] Resource Group creado
- [ ] Container Registry creado
- [ ] Imágenes subidas al ACR
- [ ] App Service configurado
- [ ] Variables de entorno establecidas
- [ ] Landing page accesible en `/`
- [ ] App de gestión en `/app/`
- [ ] API respondiendo en `/api/`
- [ ] Login funcional
- [ ] Monitoreo de costos configurado

---

## 🎯 PRÓXIMOS PASOS

1. **Probar localmente**: `docker-compose -f docker-compose.full.yml up`
2. **Si funciona local**, seguir esta guía paso a paso
3. **Configurar dominio custom** (opcional): `vetclinic.tudominio.com`
4. **Configurar SSL/HTTPS** (gratuito con Let's Encrypt)
5. **Configurar backups** de la base de datos

---

**¿Necesitas ayuda?** Revisa los logs con:
```powershell
az webapp log tail --name $APP_NAME --resource-group $RESOURCE_GROUP
```

