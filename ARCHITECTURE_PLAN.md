# 🚀 ARQUITECTURA DEL PROYECTO - PLAN COMPLETO

> **Proyecto:** Clínica Veterinaria - Sistema Completo  
> **Fecha:** 2025-01-20  
> **Objetivo:** Dockerizar todo el stack de forma profesional  

---

## 📂 ESTRUCTURA ACTUAL VS ESTRUCTURA OBJETIVO

### **AHORA (desarrollo):**
```
ProyectoFinalClinVet/
├── gestion-citas/              ← Backend Spring Boot (Puerto 8080)
│   ├── .git/                   ← Repositorio Git del backend
│   ├── src/
│   ├── pom.xml
│   └── ...
│
├── frontend-gestion-citas/     ← Frontend React (Puerto 5173)
│   ├── .git/                   ← Repositorio Git del frontend
│   ├── src/
│   ├── package.json
│   ├── Dockerfile              ← Ya tiene Docker
│   └── nginx.conf
│
└── landing-page/               ← Landing HTML/CSS (Puerto 80)
    ├── .git/                   ← Repositorio Git del landing
    ├── index.html
    └── ...
```

### **OBJETIVO (producción con Docker):**
```
ProyectoFinalClinVet/
├── docker-compose.yml          ← Orquesta TODOS los contenedores
├── .env.example                ← Variables de entorno compartidas
├── README.md                   ← Documentación del monorepo
│
├── backend/                    ← Backend Spring Boot
│   ├── Dockerfile              ← Crear
│   ├── .dockerignore           ← Crear
│   ├── src/
│   └── pom.xml
│
├── frontend/                   ← Frontend React
│   ├── Dockerfile              ← Ya existe, adaptar
│   ├── .dockerignore           ← Crear
│   ├── nginx.conf              ← Ya existe
│   └── src/
│
├── landing/                    ← Landing Page
│   ├── Dockerfile              ← Crear
│   ├── nginx.conf              ← Crear
│   └── index.html
│
└── mysql/                      ← Base de datos
    ├── init.sql                ← Script de inicialización
    └── data/                   ← Volumen persistente (gitignored)
```

---

## 🎯 FLUJO DE TRABAJO RECOMENDADO

### **FASE 1: DESARROLLO (ACTUAL - AHORA)**
**Objetivo:** Desarrollar cada componente de forma independiente

```
Backend:  http://localhost:8080  ← Spring Boot ejecutándose con `mvn spring-boot:run`
Frontend: http://localhost:5173  ← Vite dev server con `npm run dev`
MySQL:    localhost:3306          ← MySQL nativo en Windows
```

**Repositorios Git:**
- ✅ Backend: `github.com/Ciromontes/clinicaVeterinariaBackend.git`
- ✅ Frontend: Repositorio separado (si existe)
- ✅ Landing: Repositorio separado (si existe)

**Ventajas:**
- Hot reload (cambios en vivo)
- Debug fácil en IDE
- Desarrollo rápido

---

### **FASE 2: INTEGRACIÓN (PRÓXIMO PASO)**
**Objetivo:** Preparar para Docker sin romper desarrollo actual

**Paso 2.1: Crear estructura de monorepo**
```bash
# Crear directorio raíz del proyecto completo
mkdir D:\ProyectoClinicaVeterinaria

# Mover proyectos existentes (renombrándolos)
Move-Item gestion-citas backend
Move-Item frontend-gestion-citas frontend
Move-Item landing-page landing

# Inicializar Git en la raíz
git init
```

**Paso 2.2: Crear Dockerfiles individuales**
- `backend/Dockerfile` ← Multi-stage build con Maven
- `frontend/Dockerfile` ← Ya existe, optimizar
- `landing/Dockerfile` ← Nginx simple

**Paso 2.3: Crear docker-compose.yml**
Orquesta los 4 servicios:
1. MySQL
2. Backend
3. Frontend
4. Landing

---

### **FASE 3: DOCKERIZACIÓN (DESPUÉS DE IMPLEMENTAR ENDPOINTS)**
**Objetivo:** Contenedores separados pero comunicados

```yaml
# docker-compose.yml
version: '3.8'

services:
  mysql:
    image: mysql:9.0
    ports: ["3306:3306"]
    volumes: ["./mysql/init.sql:/docker-entrypoint-initdb.d/init.sql"]
    
  backend:
    build: ./backend
    ports: ["8080:8080"]
    depends_on: [mysql]
    
  frontend:
    build: ./frontend
    ports: ["80:80"]
    depends_on: [backend]
    
  landing:
    build: ./landing
    ports: ["8081:80"]
```

**Comandos:**
```bash
docker-compose up -d          # Levantar todo
docker-compose logs -f        # Ver logs en tiempo real
docker-compose down           # Apagar todo
```

---

### **FASE 4: PRODUCCIÓN (FINAL)**
**Objetivo:** Deploy en servidor real

```
Servidor (VPS/AWS/Azure):
├── MySQL (contenedor o servicio administrado)
├── Backend (contenedor escalable)
├── Frontend (contenedor con Nginx)
└── Landing (contenedor con Nginx)

+ Nginx Reverse Proxy:
  - https://clinica.com/api → Backend
  - https://clinica.com → Frontend
  - https://clinica.com/landing → Landing
```

---

## ✅ SOLUCIÓN PARA TU SITUACIÓN ACTUAL

### **OPCIÓN RECOMENDADA: Mantener todo junto pero con .gitignore mejorado**

No muevas `frontend-gestion-citas` ni `landing-page` por ahora. En su lugar:

**1. Actualizar `.gitignore` del backend para ignorar proyectos hermanos:**

```gitignore
# Proyectos hermanos (tienen sus propios repos Git)
frontend-gestion-citas/
landing-page/
```

**2. Verificar que cada proyecto tiene su propio `.git/`:**
```bash
ls frontend-gestion-citas/.git  # Debe existir
ls landing-page/.git            # Debe existir
```

**3. Trabajar así durante desarrollo:**
```
gestion-citas/          → git push → github.com/.../backend
frontend-gestion-citas/ → git push → github.com/.../frontend
landing-page/           → git push → github.com/.../landing
```

**4. Cuando estés listo para Docker (después de implementar endpoints):**

Crear **proyecto padre** que los englobe:
```bash
# Crear monorepo (fuera de gestion-citas)
cd D:\ProyectoClinicaVeterinaria
git init

# Agregar como submódulos
git submodule add https://github.com/Ciromontes/backend.git backend
git submodule add https://github.com/.../frontend.git frontend
git submodule add https://github.com/.../landing.git landing

# Crear docker-compose.yml en la raíz
```

---

## 📋 PLAN DE ACCIÓN INMEDIATO (PARA HOY)

### **1. NO mover directorios ahora** ✅
Déjalos donde están. Git los ignorará correctamente.

### **2. Actualizar .gitignore del backend:**

Agregar al inicio del archivo:
```gitignore
# ================================================================
# PROYECTOS HERMANOS (repositorios Git separados)
# ================================================================
frontend-gestion-citas/
landing-page/
```

### **3. Verificar que Git los ignora:**
```bash
cd gestion-citas
git status
# NO debe aparecer frontend ni landing
```

### **4. Continuar con desarrollo normal:**
- ✅ Backend: Implementar endpoints faltantes
- ✅ Frontend: Conectar con endpoints nuevos
- ✅ Testing: Probar integración

### **5. Docker viene después:**
Cuando termines de implementar los 12 endpoints (en 2-3 días).

---

## 🐳 PREVIEW: DOCKER-COMPOSE FINAL

```yaml
# docker-compose.yml (FUTURO)
version: '3.8'

services:
  # ===== BASE DE DATOS =====
  mysql:
    image: mysql:9.0
    container_name: clinica-mysql
    environment:
      MYSQL_ROOT_PASSWORD: ${DB_PASSWORD}
      MYSQL_DATABASE: clinicaveterinaria
    ports:
      - "3306:3306"
    volumes:
      - mysql-data:/var/lib/mysql
      - ./mysql/init.sql:/docker-entrypoint-initdb.d/init.sql
    networks:
      - clinica-network

  # ===== BACKEND SPRING BOOT =====
  backend:
    build:
      context: ./backend
      dockerfile: Dockerfile
    container_name: clinica-backend
    environment:
      SPRING_DATASOURCE_URL: jdbc:mysql://mysql:3306/clinicaveterinaria
      SPRING_DATASOURCE_PASSWORD: ${DB_PASSWORD}
      JWT_SECRET: ${JWT_SECRET}
    ports:
      - "8080:8080"
    depends_on:
      - mysql
    networks:
      - clinica-network

  # ===== FRONTEND REACT =====
  frontend:
    build:
      context: ./frontend
      dockerfile: Dockerfile
    container_name: clinica-frontend
    ports:
      - "80:80"
    depends_on:
      - backend
    networks:
      - clinica-network

  # ===== LANDING PAGE =====
  landing:
    build:
      context: ./landing
      dockerfile: Dockerfile
    container_name: clinica-landing
    ports:
      - "8081:80"
    networks:
      - clinica-network

volumes:
  mysql-data:

networks:
  clinica-network:
    driver: bridge
```

---

## 🎯 PRÓXIMOS PASOS (EN ORDEN)

### **HOY (Día 1):**
- [x] Push exitoso del backend ✅
- [x] Actualizar .gitignore para ignorar frontend/landing
- [ ] Ejecutar script de datos de prueba SQL
- [ ] Probar endpoints en Postman

### **Mañana (Día 2):**
- [ ] Implementar 5 endpoints prioridad ALTA
- [ ] Probar integración con frontend
- [ ] Ajustar respuestas según frontend

### **Día 3:**
- [ ] Implementar 3 endpoints prioridad MEDIA
- [ ] Testing completo (24 tests)
- [ ] Documentar endpoints en README

### **Día 4:**
- [ ] Implementar endpoints opcionales
- [ ] Preparar para Docker

### **Día 5:**
- [ ] Crear Dockerfiles
- [ ] Crear docker-compose.yml
- [ ] Testing con Docker
- [ ] Deploy

---

## 📝 RESUMEN

**AHORA:** Mantén `frontend-gestion-citas` y `landing-page` donde están.  
**GITIGNORE:** Los ignora correctamente.  
**DESARROLLO:** Continúa normal.  
**DOCKER:** Lo harás cuando termines endpoints (3-4 días).

**¿Seguimos con este plan?** Es limpio, profesional y te permite avanzar sin bloqueos.

---

**Última actualización:** 2025-01-20  
**Estado:** PLAN APROBADO - LISTO PARA EJECUTAR

