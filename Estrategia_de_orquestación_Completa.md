# 📋 ESTRATEGIA DE ORQUESTACIÓN - PROYECTO CLÍNICA VETERINARIA

## 🎯 OBJETIVO FINAL
Desplegar 3 aplicaciones dockerizadas en Azure, aprovechando el crédito estudiantil, con arquitectura de microservicios orquestada.

---

## 📊 ESTADO ACTUAL DEL PROYECTO

### ✅ COMPLETADO
- **Backend (Spring Boot):** Dockerizado + MySQL funcionando
- **API REST:** 26 pruebas validadas exitosamente (CLIENTE, VETERINARIO, ADMIN)
- **Base de datos:** MySQL con datos de prueba cargados
- **Autenticación:** JWT con roles implementados
- **Frontend:** Código existente + Dockerfile creado
- **Landing Page:** Código HTML estático existente

### ⚠️ PENDIENTE
- **Conexión Frontend ↔ Backend** en contenedores Docker
- **Landing Page** redirigiendo a Frontend dockerizado
- **Docker Compose maestro** orquestando los 3 servicios
- **Variables de entorno** configuradas para producción
- **Despliegue en Azure** con servicios estudiantiles

---

## 🚀 FASES DE IMPLEMENTACIÓN

---

## 📍 FASE 1: INTEGRACIÓN LOCAL CON DOCKER (HOY - 2-3 HORAS)

### 🎯 Objetivo
Lograr que Frontend dockerizado se comunique con Backend dockerizado.

### 📝 PROMPT PARA AGENTE FRONTEND

```markdown
# 🚀 TAREA: DOCKERIZAR Y CONECTAR FRONTEND CON BACKEND

## CONTEXTO ACTUAL
- Backend Spring Boot ya dockerizado: `http://localhost:8080/api`
- MySQL funcionando en Docker: puerto 3306
- 26 pruebas API exitosas validadas con Postman
- Frontend React con Vite existente en: `frontend-gestion-citas/`
- Dockerfile básico ya creado pero sin configuración de red

## OBJETIVO
Configurar el frontend para que funcione en Docker y se comunique con el backend dockerizado.

## TAREAS ESPECÍFICAS

### 1. CONFIGURAR VARIABLES DE ENTORNO
Crear/modificar `.env` files:

**Para desarrollo local (sin Docker):**
```env
VITE_API_URL=http://localhost:8080/api
```

**Para Docker (desarrollo):**
```env
VITE_API_URL=http://backend:8080/api
```

**Para Azure (producción):**
```env
VITE_API_URL=https://backend-vetclinic.azurewebsites.net/api
```

### 2. ACTUALIZAR DOCKERFILE
Asegurar que el Dockerfile:
- Use node:18-alpine como base
- Copie package.json y package-lock.json
- Instale dependencias con `npm ci`
- Copie código fuente
- Build la aplicación con `npm run build`
- Use nginx para servir archivos estáticos
- Exponga puerto 80

### 3. CREAR DOCKER-COMPOSE LOCAL
Crear `docker-compose.dev.yml` en `frontend-gestion-citas/`:

```yaml
version: '3.8'

services:
  frontend:
    build:
      context: .
      dockerfile: Dockerfile
    container_name: vetclinic-frontend-dev
    ports:
      - "3000:80"
    environment:
      - VITE_API_URL=http://backend:8080/api
    networks:
      - vetclinic-network
    depends_on:
      - backend

  backend:
    image: gestion-citas-backend:latest
    container_name: vetclinic-backend-dev
    ports:
      - "8080:8080"
    networks:
      - vetclinic-network

networks:
  vetclinic-network:
    driver: bridge
```

### 4. CONFIGURAR NGINX
Crear `nginx.conf` en `frontend-gestion-citas/`:

```nginx
server {
    listen 80;
    server_name localhost;
    root /usr/share/nginx/html;
    index index.html;

    location / {
        try_files $uri $uri/ /index.html;
    }

    location /api {
        proxy_pass http://backend:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }
}
```

### 5. COMANDOS DE PRUEBA
```bash
# Build y levantar solo frontend
docker-compose -f docker-compose.dev.yml build frontend
docker-compose -f docker-compose.dev.yml up frontend

# Verificar que responde
curl http://localhost:3000

# Verificar logs
docker logs vetclinic-frontend-dev
```

## CRITERIOS DE ÉXITO
- [ ] Frontend responde en `http://localhost:3000`
- [ ] Página de login carga correctamente
- [ ] Al hacer login, el frontend puede autenticarse contra `http://backend:8080/api/auth/login`
- [ ] Dashboard carga datos desde el backend
- [ ] No hay errores de CORS
- [ ] Logs de Docker muestran comunicación exitosa

## ENTREGABLES
1. `.env.development` y `.env.production` configurados
2. `Dockerfile` optimizado para producción
3. `nginx.conf` con proxy reverso
4. `docker-compose.dev.yml` funcional
5. Documentación de comandos de prueba
6. Capturas de pantalla de frontend funcionando
```

---

### 📝 PROMPT PARA LANDING PAGE

```markdown
# 🚀 TAREA: CONFIGURAR LANDING PAGE CON REDIRECCIÓN

## CONTEXTO
- Landing page HTML/CSS/JS estática en: `landing-page/`
- Frontend dockerizado corriendo en: `http://localhost:3000`
- Necesitamos que el botón "Acceder al Sistema" redirija correctamente

## OBJETIVO
Dockerizar la landing page y configurar redirección al frontend.

## TAREAS ESPECÍFICAS

### 1. ACTUALIZAR REDIRECCIÓN EN HTML
Modificar `landing-page/index.html`:

```html
<!-- Botón de acceso -->
<a href="http://localhost:3000" 
   class="btn-primary" 
   id="btn-acceder">
  Acceder al Sistema
</a>

<script>
  // Configurar URL según entorno
  const FRONTEND_URL = {
    development: 'http://localhost:3000',
    docker: 'http://frontend:3000',
    production: 'https://frontend-vetclinic.azurewebsites.net'
  };

  document.getElementById('btn-acceder').href = 
    FRONTEND_URL[process.env.NODE_ENV || 'development'];
</script>
```

### 2. CREAR DOCKERFILE
Crear `landing-page/Dockerfile`:

```dockerfile
FROM nginx:alpine

# Copiar archivos estáticos
COPY . /usr/share/nginx/html

# Copiar configuración de nginx
COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
```

### 3. CONFIGURAR NGINX
Crear `landing-page/nginx.conf`:

```nginx
server {
    listen 80;
    server_name localhost;
    root /usr/share/nginx/html;
    index index.html;

    location / {
        try_files $uri $uri/ /index.html;
    }
}
```

### 4. COMANDOS DE PRUEBA
```bash
# Build imagen
docker build -t vetclinic-landing:latest .

# Ejecutar contenedor
docker run -d -p 80:80 --name landing vetclinic-landing:latest

# Verificar
curl http://localhost
```

## CRITERIOS DE ÉXITO
- [ ] Landing page carga en `http://localhost`
- [ ] Botón "Acceder" funciona correctamente
- [ ] Redirección apunta a `http://localhost:3000`
- [ ] Estilos CSS se cargan correctamente
- [ ] Imágenes se muestran correctamente

## ENTREGABLES
1. `Dockerfile` para landing page
2. `nginx.conf` configurado
3. HTML actualizado con redirección dinámica
4. Documentación de comandos
```

---

## 📍 FASE 2: ORQUESTACIÓN COMPLETA (MAÑANA - 2 HORAS)

### 🎯 Objetivo
Crear docker-compose maestro que orqueste los 3 servicios: Backend + Frontend + Landing.

### 📝 PROMPT PARA DOCKER-COMPOSE MAESTRO

```markdown
# 🚀 TAREA: ORQUESTAR LOS 3 SERVICIOS CON DOCKER-COMPOSE

## CONTEXTO
- Backend dockerizado: `gestion-citas/` (Spring Boot + MySQL)
- Frontend dockerizado: `frontend-gestion-citas/` (React + Nginx)
- Landing dockerizada: `landing-page/` (HTML estático + Nginx)

## OBJETIVO
Crear un `docker-compose.yml` maestro que levante los 3 servicios coordinados.

## ESTRUCTURA DE CARPETAS ESPERADA
```
proyecto-clinica-veterinaria/
├── docker-compose.yml          ← ARCHIVO MAESTRO A CREAR
├── .env.production             ← Variables de entorno
├── gestion-citas/              ← Backend
│   ├── Dockerfile
│   └── docker-compose.yml      ← Ya existe
├── frontend-gestion-citas/     ← Frontend
│   ├── Dockerfile
│   └── nginx.conf
└── landing-page/               ← Landing
├── Dockerfile
└── nginx.conf
```

## TAREAS ESPECÍFICAS

### 1. CREAR DOCKER-COMPOSE.YML MAESTRO
Crear en la raíz del proyecto:

```yaml
version: '3.8'

networks:
  vetclinic-network:
    driver: bridge

volumes:
  mysql-data:
    driver: local

services:
  # ========== BASE DE DATOS ==========
  mysql:
    image: mysql:8.0
    container_name: vetclinic-mysql
    restart: unless-stopped
    environment:
      MYSQL_ROOT_PASSWORD: ${MYSQL_ROOT_PASSWORD}
      MYSQL_DATABASE: ${MYSQL_DATABASE}
      MYSQL_USER: ${MYSQL_USER}
      MYSQL_PASSWORD: ${MYSQL_PASSWORD}
    ports:
      - "${MYSQL_PORT}:3306"
    volumes:
      - mysql-data:/var/lib/mysql
      - ./gestion-citas/init.sql:/docker-entrypoint-initdb.d/init.sql
    networks:
      - vetclinic-network
    healthcheck:
      test: ["CMD", "mysqladmin", "ping", "-h", "localhost"]
      interval: 10s
      timeout: 5s
      retries: 5

  # ========== BACKEND ==========
  backend:
    build:
      context: ./gestion-citas
      dockerfile: Dockerfile
    container_name: vetclinic-backend
    restart: unless-stopped
    environment:
      SPRING_DATASOURCE_URL: jdbc:mysql://mysql:3306/${MYSQL_DATABASE}
      SPRING_DATASOURCE_USERNAME: ${MYSQL_USER}
      SPRING_DATASOURCE_PASSWORD: ${MYSQL_PASSWORD}
      JWT_SECRET: ${JWT_SECRET}
    ports:
      - "${BACKEND_PORT}:8080"
    depends_on:
      mysql:
        condition: service_healthy
    networks:
      - vetclinic-network
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:8080/actuator/health"]
      interval: 30s
      timeout: 10s
      retries: 3

  # ========== FRONTEND ==========
  frontend:
    build:
      context: ./frontend-gestion-citas
      dockerfile: Dockerfile
      args:
        VITE_API_URL: http://backend:8080/api
    container_name: vetclinic-frontend
    restart: unless-stopped
    ports:
      - "${FRONTEND_PORT}:80"
    depends_on:
      backend:
        condition: service_healthy
    networks:
      - vetclinic-network
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:80"]
      interval: 30s
      timeout: 10s
      retries: 3

  # ========== LANDING PAGE ==========
  landing:
    build:
      context: ./landing-page
      dockerfile: Dockerfile
    container_name: vetclinic-landing
    restart: unless-stopped
    environment:
      FRONTEND_URL: http://frontend:80
    ports:
      - "${LANDING_PORT}:80"
    depends_on:
      - frontend
    networks:
      - vetclinic-network
```

### 2. CREAR .ENV.PRODUCTION
```env
# MySQL
MYSQL_ROOT_PASSWORD=rootSecurePass123
MYSQL_DATABASE=gestion_citas
MYSQL_USER=vetclinic_user
MYSQL_PASSWORD=VetClinic2025!
MYSQL_PORT=3306

# Backend
BACKEND_PORT=8080
JWT_SECRET=clave-super-secreta-jwt-2025-vetclinic

# Frontend
FRONTEND_PORT=3000
VITE_API_URL=http://localhost:8080/api

# Landing
LANDING_PORT=80
```

### 3. CREAR SCRIPTS DE ADMINISTRACIÓN

**start.sh** (Linux/Mac):
```bash
#!/bin/bash
echo "🚀 Iniciando Clínica Veterinaria..."
docker-compose --env-file .env.production up -d
echo "✅ Servicios iniciados:"
echo "   Landing: http://localhost"
echo "   Frontend: http://localhost:3000"
echo "   Backend: http://localhost:8080"
docker-compose ps
```

**start.ps1** (Windows PowerShell):
```powershell
Write-Host "🚀 Iniciando Clínica Veterinaria..." -ForegroundColor Green
docker-compose --env-file .env.production up -d
Write-Host "✅ Servicios iniciados:" -ForegroundColor Green
Write-Host "   Landing: http://localhost" -ForegroundColor Cyan
Write-Host "   Frontend: http://localhost:3000" -ForegroundColor Cyan
Write-Host "   Backend: http://localhost:8080" -ForegroundColor Cyan
docker-compose ps
```

**stop.sh** / **stop.ps1**:
```bash
#!/bin/bash
echo "🛑 Deteniendo Clínica Veterinaria..."
docker-compose down
echo "✅ Servicios detenidos"
```

### 4. COMANDOS DE PRUEBA

```bash
# Build todas las imágenes
docker-compose --env-file .env.production build

# Levantar todos los servicios
docker-compose --env-file .env.production up -d

# Ver logs de todos los servicios
docker-compose logs -f

# Ver logs de un servicio específico
docker-compose logs -f backend

# Ver estado de los servicios
docker-compose ps

# Verificar health checks
docker inspect vetclinic-backend | grep -A 10 Health

# Detener todos los servicios
docker-compose down

# Limpiar todo (incluyendo volúmenes)
docker-compose down -v
```

### 5. PRUEBAS END-TO-END

```bash
# 1. Verificar MySQL
docker exec -it vetclinic-mysql mysql -uroot -prootSecurePass123 -e "SHOW DATABASES;"

# 2. Verificar Backend
curl http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@clinicaveterinaria.com","password":"admin123"}'

# 3. Verificar Frontend
curl http://localhost:3000

# 4. Verificar Landing
curl http://localhost
```

## CRITERIOS DE ÉXITO
- [ ] `docker-compose up -d` ejecuta sin errores
- [ ] Todos los contenedores aparecen como "healthy"
- [ ] MySQL acepta conexiones del backend
- [ ] Backend responde en el puerto 8080
- [ ] Frontend carga en el puerto 3000
- [ ] Landing carga en el puerto 80
- [ ] Landing → Frontend → Backend flujo completo funciona
- [ ] Login desde frontend autentica contra backend
- [ ] No hay errores de red entre contenedores

## ENTREGABLES
1. `docker-compose.yml` maestro funcional
2. `.env.production` con todas las variables
3. Scripts `start.sh/ps1` y `stop.sh/ps1`
4. README.md con instrucciones de uso
5. Documentación de troubleshooting
6. Capturas de pantalla de los 3 servicios funcionando
```

---

## 📍 FASE 3: PREPARACIÓN PARA AZURE (1 DÍA)

### 🎯 Objetivo
Adaptar la configuración para despliegue en Azure con servicios estudiantiles.

### 📝 PLAN DE DESPLIEGUE EN AZURE

```markdown
# 🚀 ESTRATEGIA DE DESPLIEGUE EN AZURE

## SERVICIOS DE AZURE A UTILIZAR

### 1. AZURE DATABASE FOR MYSQL
**Servicio:** Azure Database for MySQL - Flexible Server  
**Tier:** B1ms (1 vCore, 2GB RAM) - Incluido en crédito estudiantil  
**Configuración:**
- Backup automático habilitado
- SSL requerido
- Firewall: permitir IPs de App Services

### 2. BACKEND - AZURE APP SERVICE
**Servicio:** Azure App Service (Linux)  
**Plan:** B1 (1 Core, 1.75GB RAM)  
**Runtime:** Java 17  
**Configuración:**
- Deployment: Docker Container
- Registry: Azure Container Registry
- Health check: `/actuator/health`
- Always On: Habilitado

### 3. FRONTEND - AZURE STATIC WEB APPS
**Servicio:** Azure Static Web Apps  
**Plan:** Free (incluido)  
**Configuración:**
- Build: React (Vite)
- Output: `dist/`
- API: Vinculada al App Service del backend

### 4. LANDING PAGE - AZURE STATIC WEB APPS
**Servicio:** Azure Static Web Apps (misma instancia que frontend)  
**Plan:** Free  
**Configuración:**
- Ruta raíz: Landing page
- Subruta `/app`: Frontend

## ARQUITECTURA FINAL EN AZURE

```
┌─────────────────────────────────────────┐
│    Azure Static Web Apps (Free)         │
│  ┌───────────────────────────────────┐  │
│  │  Landing Page (/)                 │  │
│  │  http://vetclinic.azurewebsites... │  │
│  └───────────┬───────────────────────┘  │
│              │ Redirección              │
│  ┌───────────▼───────────────────────┐  │
│  │  Frontend (/app)                  │  │
│  │  React + Vite                     │  │
│  └───────────┬───────────────────────┘  │
└──────────────┼──────────────────────────┘
│ API Calls
│
┌──────────────▼──────────────────────────┐
│   Azure App Service (B1)                │
│   Backend Spring Boot                   │
│   http://backend-vetclinic.azure...     │
└──────────────┬──────────────────────────┘
│ JDBC
┌──────────────▼──────────────────────────┐
│   Azure Database for MySQL (B1ms)      │
│   Flexible Server                       │
└─────────────────────────────────────────┘
```

## TAREAS DE PREPARACIÓN

### 1. CREAR AZURE CONTAINER REGISTRY
```bash
# Login en Azure
az login --use-device-code

# Crear Resource Group
az group create \
  --name rg-vetclinic \
  --location eastus

# Crear Container Registry
az acr create \
  --resource-group rg-vetclinic \
  --name vetclinicregistry \
  --sku Basic
```

### 2. CONFIGURAR MYSQL EN AZURE
```bash
# Crear MySQL Flexible Server
az mysql flexible-server create \
  --resource-group rg-vetclinic \
  --name mysql-vetclinic \
  --location eastus \
  --admin-user adminvetclinic \
  --admin-password 'VetClinic2025!' \
  --sku-name B_Standard_B1ms \
  --tier Burstable \
  --storage-size 20 \
  --version 8.0

# Crear base de datos
az mysql flexible-server db create \
  --resource-group rg-vetclinic \
  --server-name mysql-vetclinic \
  --database-name gestion_citas
```

### 3. PUSH BACKEND A ACR
```bash
# Tag imagen
docker tag gestion-citas-backend:latest \
  vetclinicregistry.azurecr.io/backend:latest

# Login en ACR
az acr login --name vetclinicregistry

# Push imagen
docker push vetclinicregistry.azurecr.io/backend:latest
```

### 4. CREAR APP SERVICE PARA BACKEND
```bash
# Crear App Service Plan
az appservice plan create \
  --name plan-vetclinic \
  --resource-group rg-vetclinic \
  --sku B1 \
  --is-linux

# Crear Web App con container
az webapp create \
  --resource-group rg-vetclinic \
  --plan plan-vetclinic \
  --name backend-vetclinic \
  --deployment-container-image-name vetclinicregistry.azurecr.io/backend:latest

# Configurar variables de entorno
az webapp config appsettings set \
  --resource-group rg-vetclinic \
  --name backend-vetclinic \
  --settings \
    SPRING_DATASOURCE_URL="jdbc:mysql://mysql-vetclinic.mysql.database.azure.com:3306/gestion_citas?sslMode=REQUIRED" \
    SPRING_DATASOURCE_USERNAME="adminvetclinic" \
    SPRING_DATASOURCE_PASSWORD="VetClinic2025!" \
    JWT_SECRET="azure-jwt-secret-2025"
```

### 5. DESPLEGAR FRONTEND EN STATIC WEB APPS
```bash
# Instalar extensión
npm install -g @azure/static-web-apps-cli

# Deploy frontend
cd frontend-gestion-citas
swa deploy \
  --app-location . \
  --output-location dist \
  --api-location "" \
  --env production
```

### 6. DESPLEGAR LANDING EN STATIC WEB APPS
```bash
cd landing-page
swa deploy \
  --app-location . \
  --output-location . \
  --env production
```

## VARIABLES DE ENTORNO PARA AZURE

**.env.azure**:
```env
# MySQL Azure
AZURE_MYSQL_HOST=mysql-vetclinic.mysql.database.azure.com
AZURE_MYSQL_PORT=3306
AZURE_MYSQL_DATABASE=gestion_citas
AZURE_MYSQL_USER=adminvetclinic
AZURE_MYSQL_PASSWORD=VetClinic2025!

# Backend Azure
AZURE_BACKEND_URL=https://backend-vetclinic.azurewebsites.net

# Frontend Azure
AZURE_FRONTEND_URL=https://vetclinic.azurewebsites.net

# JWT
JWT_SECRET=azure-jwt-secret-ultra-secure-2025
```

## CHECKLIST AZURE
- [ ] Cuenta Azure Educación activada
- [ ] Resource Group creado
- [ ] Azure Container Registry configurado
- [ ] MySQL Flexible Server creado y accesible
- [ ] Backend desplegado en App Service
- [ ] Frontend desplegado en Static Web Apps
- [ ] Landing desplegada en Static Web Apps
- [ ] Firewall MySQL configurado para App Service
- [ ] SSL/TLS habilitado en MySQL
- [ ] Health checks configurados
- [ ] Logs centralizados en Log Analytics
- [ ] Pruebas E2E exitosas en producción
```

---

## 📍 FASE 4: VALIDACIÓN FINAL (DESPUÉS DEL DESPLIEGUE)

### 🧪 PRUEBAS EN AZURE

```markdown
# CHECKLIST DE VALIDACIÓN EN PRODUCCIÓN

## 1. LANDING PAGE
- [ ] Accesible en URL de Azure
- [ ] Estilos CSS cargando correctamente
- [ ] Imágenes mostrándose
- [ ] Botón "Acceder" funcional
- [ ] Redirección al frontend correcta

## 2. FRONTEND
- [ ] Accesible en URL de Azure
- [ ] Página de login carga
- [ ] Login con credenciales de prueba funciona
- [ ] Dashboard carga datos
- [ ] Navegación entre secciones funciona
- [ ] Imágenes y assets cargan

## 3. BACKEND
- [ ] Health check responde: `/actuator/health`
- [ ] Login API funciona
- [ ] CRUD de citas funciona
- [ ] CRUD de mascotas funciona
- [ ] Permisos por rol validados

## 4. BASE DE DATOS
- [ ] Conexión desde backend exitosa
- [ ] Datos de prueba cargados
- [ ] Queries funcionando correctamente

## 5. INTEGRACIÓN E2E
- [ ] Landing → Frontend → Backend → DB flujo completo
- [ ] Login desde frontend autentica en backend
- [ ] CRUD completo funciona end-to-end
- [ ] No hay errores de CORS
- [ ] Performance aceptable (< 3s carga inicial)
```

---

## 📊 CRONOGRAMA RESUMIDO

| Fase | Duración | Entregable Principal |
|------|----------|---------------------|
| **FASE 1** | 2-3 horas | Frontend + Landing dockerizados y funcionando localmente |
| **FASE 2** | 2 horas | docker-compose.yml maestro orquestando los 3 servicios |
| **FASE 3** | 1 día | Configuración Azure completa + despliegue inicial |
| **FASE 4** | 2 horas | Validación E2E en producción |

---

## 🎯 HITOS CRÍTICOS

### ✅ HITO 1: Docker Local Completo
**Criterio:** `docker-compose up -d` levanta los 3 servicios y funcionan juntos

### ✅ HITO 2: Primera Conexión Frontend-Backend
**Criterio:** Login desde frontend dockerizado autentica contra backend dockerizado

### ✅ HITO 3: Azure Configurado
**Criterio:** Todos los servicios Azure creados y configurados

### ✅ HITO 4: Despliegue Exitoso
**Criterio:** Aplicación funcionando end-to-end en Azure

---

## 📝 DOCUMENTACIÓN A GENERAR

1. **README.md principal** con arquitectura del sistema
2. **DOCKER_SETUP.md** con instrucciones de Docker local
3. **AZURE_DEPLOYMENT.md** con pasos de despliegue en Azure
4. **TROUBLESHOOTING.md** con errores comunes y soluciones
5. **API_DOCUMENTATION.md** con endpoints documentados
6. **USER_MANUAL.md** con guía de usuario final

---

## 🚨 PUNTOS DE CONTROL

### Antes de avanzar a FASE 2:
- [ ] Frontend se comunica con Backend en Docker
- [ ] Landing redirige correctamente al Frontend

### Antes de avanzar a FASE 3:
- [ ] `docker-compose up -d` funciona sin errores
- [ ] Flujo E2E completo validado localmente

### Antes de considerar FASE 4:
- [ ] Todos los servicios Azure creados
- [ ] Imágenes Docker subidas a ACR

---

## 💡 RECOMENDACIONES FINALES

1. **NO pasar a la siguiente fase** hasta completar la actual
2. **Documentar cada error** encontrado y su solución
3. **Hacer commits frecuentes** al final de cada tarea exitosa
4. **Probar en local** antes de subir a Azure
5. **Mantener copias** de las variables de entorno
6. **Usar Azure CLI** para automatizar despliegues

---

## 🎉 RESULTADO ESPERADO FINAL

```
🌐 Landing Page (Azure Static Web Apps - FREE)
   └─► http://vetclinic.azurewebsites.net

📱 Frontend React (Azure Static Web Apps - FREE)
   └─► http://vetclinic.azurewebsites.net/app

⚙️ Backend API (Azure App Service - B1)
   └─► https://backend-vetclinic.azurewebsites.net/api

🗄️ Base de Datos (Azure MySQL - B1ms)
   └─► mysql-vetclinic.mysql.database.azure.com
```

**Todo funcionando de forma integrada, aprovechando crédito estudiantil de Azure.**

---

**📌 Última actualización:** 2025-10-22  
**🔒 Versión:** 1.0 - Estrategia de orquestación completa  
**✅ Estado:** Listo para ejecutar FASE 1