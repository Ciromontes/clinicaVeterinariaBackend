# Abrir en el navegador
start http://localhost:8080
```

**Verificar:**
- ✅ La landing page carga correctamente
- ✅ Los estilos CSS se aplican
- ✅ Las imágenes se muestran
- ✅ El botón "Acceder al Sistema" funciona
- ✅ Redirección al frontend (http://localhost:3000)

### Paso 4: Limpiar después de probar

```powershell
# Detener y eliminar contenedor
docker stop landing-test
docker rm landing-test

# (Opcional) Eliminar imagen
docker rmi vetclinic-landing:test
```

---

## 🚀 INTEGRACIÓN CON DOCKER-COMPOSE

Una vez validadas las pruebas locales, la landing se integrará en el `docker-compose.full.yml` del backend.

**El backend agregará este servicio:**

```yaml
landing:
  build:
    context: ./landing-page
    dockerfile: Dockerfile
  container_name: vetclinic-landing
  restart: unless-stopped
  ports:
    - "80:80"
  networks:
    - vetclinic-network
  healthcheck:
    test: ["CMD", "wget", "--no-verbose", "--tries=1", "--spider", "http://localhost/health"]
    interval: 30s
    timeout: 10s
    retries: 3
```

**URLs finales:**
- **Landing:** http://localhost (puerto 80)
- **Frontend:** http://localhost:3000
- **Backend:** http://localhost:8080

---

## ✅ CHECKLIST DE VALIDACIÓN

- [ ] `index.html` tiene botón con `id="btn-acceder"`
- [ ] Script de detección de ambiente agregado
- [ ] `Dockerfile` creado correctamente
- [ ] `nginx.conf` configurado
- [ ] `.dockerignore` creado
- [ ] Build local exitoso: `docker build -t vetclinic-landing:test .`
- [ ] Contenedor corre sin errores: `docker run -p 8080:80 vetclinic-landing:test`
- [ ] Landing carga en http://localhost:8080
- [ ] Botón redirecciona a http://localhost:3000
- [ ] No hay errores en la consola del navegador
- [ ] Health check responde: http://localhost:8080/health

---

## 🌐 PREPARACIÓN PARA AZURE

### Variables de entorno en Azure

**Azure Static Web Apps (Landing):**
```json
{
  "FRONTEND_URL": "https://frontend-vetclinic.azurewebsites.net"
}
```

### Actualizar redirección para producción

En Azure, el script automáticamente detectará el ambiente `azure` y usará:
```javascript
frontendUrl: 'https://frontend-vetclinic.azurewebsites.net'
```

### Configuración de DNS (opcional)

Si tienes un dominio personalizado:
```
www.clinicaveterinaria.com → Landing Page (Azure Static Web Apps)
app.clinicaveterinaria.com → Frontend (Azure Static Web Apps)
api.clinicaveterinaria.com → Backend (Azure App Service)
```

---

## 📊 ESTRUCTURA DE ARCHIVOS FINAL

```
landing-page/
├── index.html                 ← ✅ Actualizado con script
├── css/
│   └── styles.css
├── js/
│   └── main.js
├── images/
│   ├── logo.png
│   └── ...
├── config.js                  ← ✅ Nuevo
├── Dockerfile                 ← ✅ Nuevo
├── nginx.conf                 ← ✅ Nuevo
└── .dockerignore              ← ✅ Nuevo
```

---

## 🐛 TROUBLESHOOTING

### Error: "Cannot GET /"
**Causa:** Los archivos no se copiaron correctamente al contenedor.

**Solución:**
```powershell
# Verificar archivos dentro del contenedor
docker exec landing-test ls -la /usr/share/nginx/html/

# Deben estar: index.html, css/, js/, images/
```

---

### Error: Botón no redirige
**Causa:** El script no se ejecutó o el `id` del botón es incorrecto.

**Solución:**
```javascript
// Abrir consola del navegador (F12) y verificar:
console.log(document.getElementById('btn-acceder'));

// Debe mostrar el elemento <a>, no null
```

---

### Error: Estilos CSS no cargan
**Causa:** Rutas de archivos CSS incorrectas.

**Solución:**
```html
<!-- Verificar rutas relativas en index.html -->
<link rel="stylesheet" href="./css/styles.css">
<!-- o -->
<link rel="stylesheet" href="/css/styles.css">
```

---

### Error: Health check falla
**Causa:** Nginx no está configurado con endpoint `/health`.

**Solución:** Verificar que `nginx.conf` tenga:
```nginx
location /health {
    access_log off;
    return 200 "healthy\n";
    add_header Content-Type text/plain;
}
```

---

## 📝 COMMITS RECOMENDADOS

```powershell
cd landing-page

git add .
git commit -m "feat: dockerizar landing page con integración dinámica

- Crear Dockerfile y nginx.conf
- Agregar detección automática de ambiente
- Configurar redirección dinámica al frontend
- Agregar health check para Docker
- Preparar para despliegue en Azure"

git push origin main
```

---

## 🎯 RESULTADO ESPERADO

Después de aplicar estos cambios:

1. **En desarrollo local:**
   ```
   http://localhost → Landing Page
   Click en "Acceder" → http://localhost:3000 (Frontend)
   ```

2. **En Docker completo:**
   ```
   docker-compose -f docker-compose.full.yml up
   
   ✅ Landing en puerto 80
   ✅ Frontend en puerto 3000
   ✅ Backend en puerto 8080
   ✅ MySQL en puerto 3306
   ```

3. **En Azure (producción):**
   ```
   https://vetclinic.azurewebsites.net → Landing
   Click en "Acceder" → https://frontend-vetclinic.azurewebsites.net
   ```

---

## 🚀 SIGUIENTES PASOS

1. ✅ Aplicar estos cambios en `landing-page/`
2. ✅ Hacer pruebas locales con Docker
3. ✅ Confirmar que la redirección funciona
4. ✅ Notificar al backend que está listo
5. 📦 El backend integrará landing en `docker-compose.full.yml`
6. 🎉 Sistema completo funcionando (Landing → Frontend → Backend → MySQL)

---

## 📞 COORDINACIÓN CON OTROS EQUIPOS

### Para el equipo Frontend:
- La landing redirigirá a `http://localhost:3000` en desarrollo
- En Azure redirigirá a la URL del frontend en producción

### Para el equipo Backend:
- Una vez lista, el backend agregará el servicio `landing` al `docker-compose.full.yml`
- La landing se levantará automáticamente con `.\start.ps1`

---

**Última actualización:** 2025-10-22  
**Versión:** 1.0 - Dockerización y preparación para Azure  
**Autor:** Equipo Backend - Sistema Clínica Veterinaria  
**Objetivo:** Despliegue completo en Azure con crédito estudiantil
# 🌐 INSTRUCCIONES PARA LA LANDING PAGE

## 🎯 OBJETIVO
Dockerizar la landing page y conectarla con el frontend React, preparando todo para despliegue en Azure.

---

## 📋 ARQUITECTURA FINAL

```
┌─────────────────────────────────────────┐
│  LANDING PAGE (Puerto 80)               │
│  http://localhost                       │
│  Página de presentación + Botón Acceder │
└─────────────┬───────────────────────────┘
              │
              │ Redirección al hacer click
              │
┌─────────────▼───────────────────────────┐
│  FRONTEND (Puerto 3000)                 │
│  http://localhost:3000                  │
│  Sistema de gestión (Login, Dashboard)  │
└─────────────┬───────────────────────────┘
              │
              │ API Calls (/api/*)
              │
┌─────────────▼───────────────────────────┐
│  BACKEND (Puerto 8080)                  │
│  http://localhost:8080/api              │
│  Spring Boot REST API                   │
└─────────────┬───────────────────────────┘
              │
              │ JDBC
              │
┌─────────────▼───────────────────────────┐
│  MYSQL (Puerto 3306)                    │
│  Base de datos                          │
└─────────────────────────────────────────┘
```

---

## 🔧 CAMBIOS REQUERIDOS

### ✅ ESTADO ACTUAL
- Landing page HTML/CSS/JS existe ✅
- Diseño responsive ✅
- Botón "Acceder al Sistema" existe ✅

### 🚀 CAMBIOS NECESARIOS

---

## 1️⃣ ACTUALIZAR REDIRECCIÓN EN HTML

**Archivo:** `landing-page/index.html`

Busca el botón de acceso y actualízalo:

**ANTES:**
```html
<a href="#" class="btn-primary">Acceder al Sistema</a>
```

**DESPUÉS:**
```html
<a href="http://localhost:3000" 
   id="btn-acceder" 
   class="btn-primary"
   target="_blank">
  Acceder al Sistema
</a>
```

---

## 2️⃣ AGREGAR JAVASCRIPT PARA AMBIENTE DINÁMICO

**Al final del `<body>` en `index.html`, agregar:**

```html
<!-- Script para redirección según ambiente -->
<script>
  // Configuración de URLs por ambiente
  const FRONTEND_URLS = {
    local: 'http://localhost:3000',
    docker: 'http://localhost:3000',
    azure: 'https://frontend-vetclinic.azurewebsites.net'
  };

  // Detectar ambiente actual
  const getEnvironment = () => {
    const hostname = window.location.hostname;
    if (hostname === 'localhost' || hostname === '127.0.0.1') {
      return 'local';
    } else if (hostname.includes('azurewebsites.net')) {
      return 'azure';
    }
    return 'docker';
  };

  // Configurar redirección
  const btnAcceder = document.getElementById('btn-acceder');
  if (btnAcceder) {
    const env = getEnvironment();
    const frontendUrl = FRONTEND_URLS[env];
    btnAcceder.href = frontendUrl;
    
    console.log(`🌐 Ambiente detectado: ${env}`);
    console.log(`🔗 URL del frontend: ${frontendUrl}`);
  }
</script>
```

---

## 3️⃣ CREAR DOCKERFILE

**Archivo:** `landing-page/Dockerfile`

```dockerfile
# ========== LANDING PAGE - NGINX ==========
FROM nginx:alpine

# Información del mantenedor
LABEL maintainer="Clínica Veterinaria <dev@clinicaveterinaria.com>"
LABEL description="Landing Page - Clínica Veterinaria"

# Copiar archivos de la landing page
COPY . /usr/share/nginx/html

# Copiar configuración de nginx
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Exponer puerto 80
EXPOSE 80

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=5s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost/health || exit 1

# Comando de inicio
CMD ["nginx", "-g", "daemon off;"]
```

---

## 4️⃣ CREAR NGINX.CONF

**Archivo:** `landing-page/nginx.conf`

```nginx
server {
    listen 80;
    server_name localhost;
    root /usr/share/nginx/html;
    index index.html;

    # Configuración de cache para archivos estáticos
    location ~* \.(jpg|jpeg|png|gif|ico|css|js|svg|woff|woff2|ttf|eot)$ {
        expires 1y;
        add_header Cache-Control "public, immutable";
    }

    # Página principal
    location / {
        try_files $uri $uri/ /index.html;
    }

    # Health check para Docker
    location /health {
        access_log off;
        return 200 "healthy\n";
        add_header Content-Type text/plain;
    }

    # Redirección al frontend (opcional)
    location /app {
        return 301 http://localhost:3000;
    }

    # Deshabilitar logs de favicon
    location = /favicon.ico {
        log_not_found off;
        access_log off;
    }
}
```

---

## 5️⃣ CREAR .DOCKERIGNORE

**Archivo:** `landing-page/.dockerignore`

```
# Node modules (si los hay)
node_modules
npm-debug.log

# Git
.git
.gitignore

# IDE
.vscode
.idea
*.swp
*.swo

# OS
.DS_Store
Thumbs.db

# Logs
*.log

# Docker
Dockerfile
docker-compose*.yml
.dockerignore

# README
README.md
```

---

## 6️⃣ CREAR ARCHIVO DE CONFIGURACIÓN (OPCIONAL)

**Archivo:** `landing-page/config.js`

```javascript
// Configuración de URLs para diferentes ambientes
window.LANDING_CONFIG = {
  environments: {
    development: {
      frontendUrl: 'http://localhost:3000',
      backendUrl: 'http://localhost:8080',
    },
    docker: {
      frontendUrl: 'http://localhost:3000',
      backendUrl: 'http://localhost:8080',
    },
    production: {
      frontendUrl: 'https://frontend-vetclinic.azurewebsites.net',
      backendUrl: 'https://backend-vetclinic.azurewebsites.net',
    }
  },
  
  // Detectar ambiente actual
  getCurrentEnvironment() {
    const hostname = window.location.hostname;
    if (hostname === 'localhost' || hostname === '127.0.0.1') {
      return this.environments.development;
    } else if (hostname.includes('azurewebsites.net')) {
      return this.environments.production;
    }
    return this.environments.docker;
  }
};
```

Luego, en el `<head>` de tu `index.html`:
```html
<script src="config.js"></script>
```

---

## 🧪 PRUEBAS LOCALES

### Paso 1: Build de la imagen

```powershell
# Navegar a la carpeta landing-page
cd landing-page

# Build de la imagen
docker build -t vetclinic-landing:test .

# Verificar que se creó
docker images | Select-String "vetclinic-landing"
```

### Paso 2: Ejecutar contenedor de prueba

```powershell
# Ejecutar contenedor
docker run -d `
  --name landing-test `
  -p 8080:80 `
  vetclinic-landing:test

# Verificar que está corriendo
docker ps | Select-String "landing-test"

# Ver logs
docker logs landing-test
```

### Paso 3: Probar en el navegador

```powershell

