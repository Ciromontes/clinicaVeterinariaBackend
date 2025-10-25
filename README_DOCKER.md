# 🐳 GUÍA DE USO - DOCKER LOCAL

## 🎯 INICIO RÁPIDO

### Opción 1: Script Automatizado (Recomendado)
```powershell
.\start.ps1
```

### Opción 2: Comandos Manuales
```powershell
# Construir imágenes
docker-compose -f docker-compose.full.yml --env-file .env.local build

# Iniciar servicios
docker-compose -f docker-compose.full.yml --env-file .env.local up -d

# Ver estado
docker-compose -f docker-compose.full.yml ps

# Ver logs
docker-compose -f docker-compose.full.yml logs -f
```

---

## 🌐 URLs DE ACCESO

- **Frontend:** http://localhost:3000
- **Backend API:** http://localhost:8080/api
- **MySQL:** localhost:3306

---

## 👤 CREDENCIALES DE PRUEBA

```
ADMIN:
- Email: admin@clinicaveterinaria.com
- Password: admin123

VETERINARIO:
- Email: ana.vet@clinicaveterinaria.com
- Password: vet123

CLIENTE:
- Email: lucia.cliente@clinicaveterinaria.com
- Password: cliente123
```

---

## 🛑 DETENER SERVICIOS

```powershell
# Con script
.\stop.ps1

# Manual
docker-compose -f docker-compose.full.yml down

# Limpiar TODO (incluyendo base de datos)
docker-compose -f docker-compose.full.yml down -v
```

---

## 🔍 COMANDOS ÚTILES

### Ver logs de un servicio específico
```powershell
docker-compose -f docker-compose.full.yml logs -f backend
docker-compose -f docker-compose.full.yml logs -f frontend
docker-compose -f docker-compose.full.yml logs -f mysql
```

### Reiniciar un servicio
```powershell
docker-compose -f docker-compose.full.yml restart backend
```

### Rebuild sin caché
```powershell
docker-compose -f docker-compose.full.yml build --no-cache
```

### Ejecutar comando en contenedor
```powershell
# Acceder a MySQL
docker exec -it vetclinic-mysql mysql -uvetclinic -pvetclinic123 gestion_citas

# Ver logs del backend
docker exec -it vetclinic-backend cat /app/logs/application.log
```

---

## 🐛 TROUBLESHOOTING

### Error: "port already in use"
```powershell
# Ver qué está usando el puerto
netstat -ano | findstr :3000
netstat -ano | findstr :8080

# Cambiar puerto en .env.local
FRONTEND_PORT=3001
BACKEND_PORT=8081
```

### Error: "Cannot connect to MySQL"
```powershell
# Ver logs de MySQL
docker logs vetclinic-mysql

# Recrear volumen
docker-compose -f docker-compose.full.yml down -v
docker-compose -f docker-compose.full.yml up -d
```

### Frontend no conecta con Backend
```powershell
# Verificar red
docker network inspect vetclinic-network

# Probar conectividad
docker exec vetclinic-frontend wget -O- http://backend:8080/actuator/health
```

---

## 📊 ARQUITECTURA LOCAL

```
┌─────────────────────────────────────┐
│  http://localhost:3000              │
│  Frontend (React + Nginx)           │
│  Container: vetclinic-frontend      │
└─────────────┬───────────────────────┘
              │ HTTP Requests
              │ /api/*
┌─────────────▼───────────────────────┐
│  http://localhost:8080              │
│  Backend (Spring Boot)              │
│  Container: vetclinic-backend       │
└─────────────┬───────────────────────┘
              │ JDBC
              │ mysql://mysql:3306
┌─────────────▼───────────────────────┐
│  localhost:3306                     │
│  MySQL 8.0                          │
│  Container: vetclinic-mysql         │
└─────────────────────────────────────┘

Red: vetclinic-network (bridge)
```

---

## 🔧 CONFIGURACIÓN

### Variables de Entorno (.env.local)
- `MYSQL_*`: Configuración de base de datos
- `BACKEND_PORT`: Puerto del backend (default: 8080)
- `FRONTEND_PORT`: Puerto del frontend (default: 3000)
- `VITE_API_URL`: URL del backend para el frontend

### Modificar Configuración
1. Edita `.env.local`
2. Reinicia servicios: `.\stop.ps1` y `.\start.ps1`

---

## 📝 PRÓXIMOS PASOS

1. ✅ Iniciar servicios con `.\start.ps1`
2. ✅ Abrir http://localhost:3000
3. ✅ Hacer login con las credenciales de prueba
4. ✅ Ejecutar pruebas del `FRONTEND_TEST_GUIDE.md`
5. 📝 Documentar cualquier problema encontrado

---

**Última actualización:** 2025-10-22

