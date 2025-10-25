
1. Revisa los logs: `.\deploy-azure.ps1 -Action logs`
2. Consulta `AZURE_DEPLOYMENT_GUIDE.md` (guía detallada)
3. Verifica el estado: `.\deploy-azure.ps1 -Action status`
# 🚀 GUÍA RÁPIDA - DESPLIEGUE EN AZURE

## ✅ YA TIENES TODO LISTO

Se han creado los siguientes archivos para Azure:

1. **`AZURE_DEPLOYMENT_GUIDE.md`** - Guía completa paso a paso
2. **`docker-compose.azure.yml`** - Orquestación optimizada para Azure
3. **`deploy-azure.ps1`** - Script automatizado de despliegue
4. **`.env.azure.example`** - Variables de entorno
5. **`nginx.azure.conf`** - Configuración Nginx para Azure
6. **`Dockerfile.azure`** - Build optimizado con Landing + Frontend

---

## 🎯 PRÓXIMOS PASOS (15 minutos)

### PASO 1: Probar localmente (IMPORTANTE)

Antes de subir a Azure, asegúrate de que todo funciona localmente:

```powershell
# Ir a la raíz del proyecto
cd D:\CopiaF\AnalisisYDesarrolloDeSoftware\2025sena\ProyectoFinalClinVet\gestion-citas

# Probar el docker-compose completo
docker-compose -f docker-compose.full.yml up -d

# Esperar 2 minutos y verificar
Start-Sleep -Seconds 120

# Ver logs
docker-compose -f docker-compose.full.yml logs

# Abrir en el navegador
start http://localhost:3000
```

**Verificar que funciona:**
- ✅ Página carga sin errores
- ✅ Login funciona con `admin@clinicaveterinaria.com / admin123`
- ✅ No hay errores en la consola del navegador (F12)

---

### PASO 2: Ajustar nombres únicos

Edita el archivo `deploy-azure.ps1` (líneas 7-11):

```powershell
$APP_NAME = "vetclinic-tuapellido-2025"  # ⬅️ CAMBIAR (debe ser único)
$REGISTRY_NAME = "acrvetclinictuapellido"  # ⬅️ CAMBIAR (solo letras y números)
```

**Ejemplo:**
```powershell
$APP_NAME = "vetclinic-garcia-2025"
$REGISTRY_NAME = "acrvetclinicgarcia"
```

---

### PASO 3: Instalar Azure CLI (si no lo tienes)

```powershell
# Descargar e instalar desde:
# https://aka.ms/installazurecliwindows

# Verificar instalación
az --version

# Login
az login
```

---

### PASO 4: Desplegar a Azure (AUTOMÁTICO)

```powershell
# Despliegue completo desde cero
.\deploy-azure.ps1 -Action full -Version v1.0.0
```

Este comando hará **TODO AUTOMÁTICAMENTE**:
1. ✅ Crear Resource Group
2. ✅ Crear Container Registry
3. ✅ Crear App Service Plan (B1)
4. ✅ Crear Web App
5. ✅ Build Backend + Frontend
6. ✅ Push a Azure Container Registry
7. ✅ Desplegar aplicación
8. ✅ Abrir en el navegador

**Tiempo estimado:** 10-15 minutos

---

## 💰 COSTOS ESTIMADOS

| Servicio | Plan | Costo/mes | Costo/día |
|----------|------|-----------|-----------|
| App Service | B1 Basic | $13.14 | $0.44 |
| Container Registry | Basic | $5.00 | $0.17 |
| MySQL | En contenedor | $0.00 | $0.00 |
| **TOTAL** | | **$18.14** | **$0.61** |

**Con $100 USD de créditos = 5.5 meses de hosting**

### 💡 OPCIÓN GRATIS para pruebas:

Si quieres **GASTAR CERO** mientras pruebas:

```powershell
# Edita deploy-azure.ps1 línea 70, cambia:
--sku B1
# Por:
--sku F1  # ¡GRATIS!
```

**Limitaciones del plan F1:**
- ⚠️ Solo 60 minutos/día de CPU
- ⚠️ 1GB RAM
- ⚠️ Sin SSL personalizado
- ✅ Perfecto para demos y desarrollo

---

## 🔄 ACTUALIZACIONES FUTURAS

Cuando hagas cambios en el código:

```powershell
# Build + Deploy nueva versión
.\deploy-azure.ps1 -Action deploy -Version v1.0.1
```

---

## 📊 MONITOREO

### Ver logs en tiempo real:
```powershell
.\deploy-azure.ps1 -Action logs
```

### Ver estado y costos:
```powershell
.\deploy-azure.ps1 -Action status
```

### Abrir portal de Azure:
```powershell
az webapp browse --name vetclinic-tuapellido-2025 --resource-group rg-vetclinic-prod
```

---

## 🆘 TROUBLESHOOTING

### Problema: "El nombre de la app ya existe"
**Solución:** Cambia `$APP_NAME` en `deploy-azure.ps1` por algo único

### Problema: "Error 502 Bad Gateway"
**Solución:** Espera 2-3 minutos. El backend tarda en iniciar.

### Problema: "No se puede conectar a MySQL"
**Solución:** Verifica los logs:
```powershell
.\deploy-azure.ps1 -Action logs
```

### Problema: "Se acabaron los créditos"
**Solución:** Elimina recursos cuando no los uses:
```powershell
.\deploy-azure.ps1 -Action teardown
```

---

## 🎓 RECURSOS ADICIONALES

- 📚 [Documentación oficial de Azure](https://docs.microsoft.com/azure)
- 💳 [Portal de créditos para estudiantes](https://portal.azure.com)
- 🎥 [Tutoriales de Azure App Service](https://learn.microsoft.com/azure/app-service/)

---

## ✅ CHECKLIST ANTES DE DESPLEGAR

- [ ] Probé localmente con `docker-compose.full.yml`
- [ ] Todo funciona (login, backend, frontend)
- [ ] Cambié `$APP_NAME` y `$REGISTRY_NAME` por nombres únicos
- [ ] Instalé Azure CLI
- [ ] Hice `az login` exitosamente
- [ ] Tengo créditos disponibles en mi cuenta de estudiante
- [ ] Leí la guía completa en `AZURE_DEPLOYMENT_GUIDE.md`

---

## 🎯 COMANDOS IMPORTANTES

```powershell
# Ver ayuda del script
.\deploy-azure.ps1

# Despliegue completo
.\deploy-azure.ps1 -Action full

# Solo actualizar código
.\deploy-azure.ps1 -Action deploy -Version v1.0.1

# Ver logs
.\deploy-azure.ps1 -Action logs

# Ver estado
.\deploy-azure.ps1 -Action status

# Eliminar todo (liberar recursos)
.\deploy-azure.ps1 -Action teardown
```

---

## 🎉 DESPUÉS DEL DESPLIEGUE

Tu aplicación estará disponible en:

🌐 **URL:** `https://vetclinic-tuapellido-2025.azurewebsites.net`

**Rutas:**
- `/` → Landing page
- `/app/` → Aplicación de gestión
- `/api/` → Backend (API REST)

**Usuarios de prueba:**
- Admin: `admin@clinicaveterinaria.com / admin123`
- Veterinario: `veterinario@clinicaveterinaria.com / vet123`
- Cliente: `cliente@clinicaveterinaria.com / cliente123`

---

## 📞 ¿NECESITAS AYUDA?

