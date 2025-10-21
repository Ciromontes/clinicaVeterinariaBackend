# 🐳 GUÍA DE DOCKERIZACIÓN - BACKEND SPRING BOOT

> **Proyecto:** Clínica Veterinaria - Backend  
> **Fecha:** 2025-01-20  
> **Stack:** Spring Boot 3.x + MySQL 9.0 + Docker  

---

## 📋 ARCHIVOS CREADOS

### **1. Dockerfile** (Multi-stage build)
- **Stage 1 (Builder):** Compila con Maven 3.9.6 + JDK 17
- **Stage 2 (Runtime):** Imagen ligera con JRE 17 Alpine
- **Seguridad:** Ejecuta como usuario no-root
- **Health check:** Endpoint `/actuator/health`

### **2. .dockerignore**
Excluye archivos innecesarios:
- `frontend-gestion-citas/` y `landing-page/`
- Archivos de configuración sensibles
- Logs, temporales, documentación
- Reduce tamaño de imagen en ~80%

### **3. docker-compose.yml**
Orquesta 2 servicios (4 en el futuro):
- ✅ **MySQL 9.0** con volumen persistente
- ✅ **Backend Spring Boot** dependiente de MySQL
- 🔲 Frontend (comentado - construir después)
- 🔲 Landing (comentado - construir después)

### **4. .env.example**
Plantilla de variables de entorno:
- Credenciales de MySQL
- JWT Secret
- Puertos configurables
- Zona horaria

### **5. docker/mysql/init.sql**
Script de inicialización de BD (vacío por ahora)

### **6. docker/mysql/my.cnf**
Configuración personalizada de MySQL

---

## 🚀 CÓMO USAR DOCKER

### **PASO 1: Crear archivo .env**

```bash
# Copiar plantilla
Copy-Item .env.example .env

# Editar .env con tus valores
notepad .env
```

**Valores recomendados para desarrollo:**
```env
MYSQL_ROOT_PASSWORD=root123
MYSQL_DATABASE=clinicaveterinaria
MYSQL_USER=clinica_user
MYSQL_PASSWORD=clinica123
JWT_SECRET=tu_jwt_secret_generado_con_40_caracteres
```

---

### **PASO 2: Exportar datos de MySQL local (IMPORTANTE)**

Si ya tienes datos en tu MySQL local, expórtalos:

```bash
# Opción 1: Solo estructura (tablas sin datos)
cd "C:\Program Files\MySQL\MySQL Server 9.0\bin"
.\mysqldump -u root -p --no-data clinicaveterinaria > D:\schema.sql

# Opción 2: Estructura + datos (recomendado)
.\mysqldump -u root -p clinicaveterinaria > D:\full_backup.sql
```

Luego copia el contenido a `docker/mysql/init.sql`

---

### **PASO 3: Construir y levantar contenedores**

```bash
# Desde el directorio gestion-citas/
cd D:\CopiaF\AnalisisYDesarrolloDeSoftware\2025sena\ProyectoFinalClinVet\gestion-citas

# Construir imágenes
docker-compose build

# Levantar servicios en segundo plano
docker-compose up -d

# Ver logs en tiempo real
docker-compose logs -f

# Ver solo logs del backend
docker-compose logs -f backend
```

---

### **PASO 4: Verificar que funciona**

```bash
# Ver estado de contenedores
docker-compose ps

# Debe mostrar:
# clinica-mysql    Up (healthy)
# clinica-backend  Up (healthy)

# Probar backend
curl http://localhost:8080/actuator/health

# Debe responder: {"status":"UP"}

# Probar login
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@clinicaveterinaria.com","password":"admin123"}'
```

---

### **PASO 5: Restaurar datos (si exportaste con mysqldump)**

```bash
# Si no agregaste init.sql, restaura manualmente:
docker exec -i clinica-mysql mysql -u root -p clinicaveterinaria < D:\full_backup.sql
# Ingresa la contraseña cuando te la pida
```

---

## 🛠️ COMANDOS ÚTILES

### **Gestión de contenedores:**
```bash
# Ver logs
docker-compose logs -f

# Ver solo errores
docker-compose logs --tail=50 backend | findstr "ERROR"

# Reiniciar servicios
docker-compose restart

# Parar servicios
docker-compose stop

# Parar y eliminar contenedores (mantiene volúmenes)
docker-compose down

# Eliminar TODO (contenedores + volúmenes)
docker-compose down -v
```

### **Acceso a contenedores:**
```bash
# Acceder a MySQL
docker exec -it clinica-mysql mysql -u root -p

# Acceder al backend (shell)
docker exec -it clinica-backend sh

# Ver archivos del backend
docker exec clinica-backend ls -la /app
```

### **Debugging:**
```bash
# Ver variables de entorno del backend
docker exec clinica-backend env | findstr SPRING

# Verificar conectividad MySQL desde backend
docker exec clinica-backend wget -qO- mysql:3306
```

---

## 📊 ARQUITECTURA DE CONTENEDORES

```
┌─────────────────────────────────────────┐
│   Host (Windows)                        │
│                                         │
│  ┌───────────────────────────────────┐  │
│  │  Docker Network: clinica-network  │  │
│  │                                   │  │
│  │  ┌──────────┐    ┌─────────────┐ │  │
│  │  │  MySQL   │◄───│  Backend    │ │  │
│  │  │  :3306   │    │  :8080      │ │  │
│  │  └──────────┘    └─────────────┘ │  │
│  │       │                 │         │  │
│  └───────┼─────────────────┼─────────┘  │
│          │                 │            │
│     [Volumen]         [Volumen]         │
│   mysql-data      backend-logs          │
└─────────────────────────────────────────┘
         │                   │
    localhost:3306      localhost:8080
```

---

## ⚠️ SOLUCIÓN DE PROBLEMAS

### **Problema: Backend no se conecta a MySQL**
```bash
# Verificar que MySQL está healthy
docker-compose ps

# Ver logs de MySQL
docker-compose logs mysql

# Verificar credenciales en .env
cat .env
```

**Solución:**
- Asegúrate que `MYSQL_PASSWORD` en `.env` coincida con `SPRING_DATASOURCE_PASSWORD`
- Espera 30-60 segundos para que MySQL esté completamente listo

---

### **Problema: No puedo acceder a localhost:8080**
```bash
# Verificar que el puerto no esté ocupado
netstat -ano | findstr :8080

# Cambiar puerto en .env
BACKEND_PORT=8081
docker-compose up -d
```

---

### **Problema: El contenedor de backend se reinicia constantemente**
```bash
# Ver últimos logs
docker-compose logs --tail=100 backend

# Errores comunes:
# - "Cannot connect to MySQL" → MySQL aún no está listo
# - "Invalid JWT secret" → Revisa .env
# - "Out of memory" → Aumenta JAVA_OPTS en docker-compose.yml
```

---

## 🔐 SEGURIDAD

### **Producción:**
1. **Cambiar secretos:**
   - Generar JWT_SECRET fuerte (40+ caracteres)
   - Usar contraseñas seguras de MySQL
   - NO usar valores por defecto

2. **Usar gestor de secretos:**
   - Azure Key Vault
   - AWS Secrets Manager
   - HashiCorp Vault

3. **Configurar firewall:**
   - Exponer solo puertos necesarios
   - Usar HTTPS con certificado SSL

4. **Actualizar imágenes:**
   ```bash
   docker-compose pull
   docker-compose up -d
   ```

---

## 📈 MONITOREO

### **Verificar salud de servicios:**
```bash
# Health check manual
curl http://localhost:8080/actuator/health

# Metrics (si está habilitado)
curl http://localhost:8080/actuator/metrics
```

### **Consumo de recursos:**
```bash
# Ver uso de CPU/RAM
docker stats

# Ver tamaño de imágenes
docker images | findstr clinica

# Ver tamaño de volúmenes
docker volume ls
docker volume inspect clinica-mysql-data
```

---

## ✅ CHECKLIST ANTES DE COMMIT

- [ ] `.env` creado localmente (NO commitear)
- [ ] Contenedores funcionando: `docker-compose ps`
- [ ] Backend responde: `curl localhost:8080/actuator/health`
- [ ] Login funciona: Probar con Postman
- [ ] Datos de MySQL restaurados
- [ ] Logs sin errores: `docker-compose logs backend`
- [ ] `.dockerignore` excluye frontend/landing

---

## 🎯 PRÓXIMOS PASOS

1. **Probar todos los endpoints** con Postman desde Docker
2. **Dockerizar frontend** (día siguiente)
3. **Dockerizar landing** (día siguiente)
4. **Nginx reverse proxy** (producción)
5. **Deploy a servidor** (producción)

---

**Última actualización:** 2025-01-20  
**Estado:** LISTO PARA PROBAR  
**Siguiente:** Verificar funcionamiento antes de commit

