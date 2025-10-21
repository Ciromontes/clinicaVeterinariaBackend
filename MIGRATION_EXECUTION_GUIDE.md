# 🚀 GUÍA DE EJECUCIÓN DE MIGRACIONES - PASO A PASO

> **Proyecto:** Clínica Veterinaria - Backend Spring Boot  
> **Fecha:** 2025-01-20  
> **Estado:** LISTO PARA EJECUTAR  
> **Tiempo estimado:** 30 minutos  

---

## ✅ ESTADO ACTUAL

### **Archivos creados:**
- ✅ 6 scripts SQL de migración en `/sql-migrations/`
- ✅ 3 modelos Java actualizados (Cita, Usuario, HistoriaClinica)
- ✅ Sin errores de compilación

### **Cambios realizados:**

#### **Base de Datos:**
1. Cambio de tipo de dato: `historiaclinica.Fecha_Creacion` (DATETIME → DATE)
2. 5 índices nuevos para optimización
3. 6 constraints CHECK para validación
4. 4 campos nuevos (Motivo_Cancelacion, Fecha_Actualizacion, Intentos_Fallidos, Fecha_Bloqueo)

#### **Código Java:**
1. `HistoriaClinica.java` - `LocalDateTime` → `LocalDate`
2. `Cita.java` - Agregados `motivoCancelacion` y `fechaActualizacion`
3. `Usuario.java` - Agregados `intentosFallidos` y `fechaBloqueo`

---

## 📋 PASOS DE EJECUCIÓN

### **PASO 1: Crear backup de base de datos (OBLIGATORIO)**

```cmd
cd "C:\Program Files\MySQL\MySQL Server 9.0\bin"
mysqldump -u root -p clinicaveterinaria > D:\backup_clinica_20250120.sql
```

**Verificar que el archivo se creó correctamente:**
```cmd
dir D:\backup_clinica_20250120.sql
```

---

### **PASO 2: Verificar estado actual (opcional pero recomendado)**

```sql
-- Conectar a MySQL
mysql -u root -p

-- Ejecutar script de verificación
source D:/CopiaF/AnalisisYDesarrolloDeSoftware/2025sena/ProyectoFinalClinVet/gestion-citas/sql-migrations/00_backup_verification.sql
```

Deberías ver un resumen de registros actuales.

---

### **PASO 3: Ejecutar todas las migraciones**

#### **Opción A: Script maestro (RECOMENDADO)**

```sql
-- En MySQL:
source D:/CopiaF/AnalisisYDesarrolloDeSoftware/2025sena/ProyectoFinalClinVet/gestion-citas/sql-migrations/ejecutar_todas_migraciones.sql
```

Este script ejecuta todas las migraciones en una transacción. Si algo falla, hace ROLLBACK automático.

#### **Opción B: Ejecutar una por una**

Si prefieres más control, ejecuta en orden:

```sql
-- Migración 1
source D:/CopiaF/AnalisisYDesarrolloDeSoftware/2025sena/ProyectoFinalClinVet/gestion-citas/sql-migrations/01_ajustar_historia_clinica_fecha.sql

-- Migración 2
source D:/CopiaF/AnalisisYDesarrolloDeSoftware/2025sena/ProyectoFinalClinVet/gestion-citas/sql-migrations/02_agregar_indices_optimizacion.sql

-- Migración 3
source D:/CopiaF/AnalisisYDesarrolloDeSoftware/2025sena/ProyectoFinalClinVet/gestion-citas/sql-migrations/03_agregar_constraints_validacion.sql

-- Migración 4
source D:/CopiaF/AnalisisYDesarrolloDeSoftware/2025sena/ProyectoFinalClinVet/gestion-citas/sql-migrations/04_agregar_campos_nuevas_funcionalidades.sql
```

---

### **PASO 4: Verificar que las migraciones se ejecutaron correctamente**

```sql
-- Verificar estructura de historiaclinica
DESCRIBE historiaclinica;
-- Fecha_Creacion debe ser DATE (no DATETIME)

-- Verificar índices nuevos
SHOW INDEX FROM cita WHERE Key_name LIKE 'idx_%';
-- Debe mostrar 3 índices

-- Verificar constraints
SELECT CONSTRAINT_NAME, TABLE_NAME 
FROM information_schema.TABLE_CONSTRAINTS
WHERE TABLE_SCHEMA = 'clinicaveterinaria'
  AND CONSTRAINT_TYPE = 'CHECK';
-- Debe mostrar 6 constraints

-- Verificar campos nuevos en cita
SHOW COLUMNS FROM cita WHERE Field IN ('Motivo_Cancelacion', 'Fecha_Actualizacion');

-- Verificar campos nuevos en usuario
SHOW COLUMNS FROM usuario WHERE Field IN ('Intentos_Fallidos', 'Fecha_Bloqueo');
```

---

### **PASO 5: Compilar el proyecto Java**

```cmd
cd D:\CopiaF\AnalisisYDesarrolloDeSoftware\2025sena\ProyectoFinalClinVet\gestion-citas

# Si usas Maven
mvn clean compile

# Verificar que no haya errores
```

---

### **PASO 6: Ejecutar la aplicación y probar**

```cmd
# Iniciar aplicación
mvn spring-boot:run

# O ejecutar el JAR
java -jar target/gestion-citas-0.0.1-SNAPSHOT.jar
```

**Verificar en los logs:**
- No debe haber errores de mapeo JPA
- Debe conectarse correctamente a la base de datos
- Endpoints deben responder

---

### **PASO 7: Probar endpoints existentes**

```powershell
# Probar login (debe funcionar igual)
curl -X POST http://localhost:8080/api/auth/login `
  -H "Content-Type: application/json" `
  -d '{\"email\":\"admin@clinica.com\",\"password\":\"admin123\"}'

# Probar listar citas (debe funcionar + incluir nuevos campos)
curl -X GET http://localhost:8080/api/citas `
  -H "Authorization: Bearer TU_TOKEN_AQUI"
```

---

## ⚠️ EN CASO DE ERROR

### **Si algo falla durante las migraciones SQL:**

```sql
-- Ejecutar ROLLBACK (si estás en transacción)
ROLLBACK;

-- Restaurar desde backup
exit

# En CMD:
cd "C:\Program Files\MySQL\MySQL Server 9.0\bin"
mysql -u root -p clinicaveterinaria < D:\backup_clinica_20250120.sql
```

### **Si la aplicación no inicia:**

1. **Verificar logs:**
```
tail -f logs/clinicaveterinaria.log
```

2. **Errores comunes:**

**Error: "Table 'cita' doesn't have a column named 'Motivo_Cancelacion'"**
- Solución: Ejecutaste la app sin ejecutar migraciones SQL primero
- Ejecuta las migraciones SQL y reinicia

**Error: "java.sql.SQLSyntaxErrorException"**
- Solución: Verifica que las migraciones se completaron
- Revisa los logs de MySQL

---

## ✅ CHECKLIST DE VERIFICACIÓN

- [ ] Backup creado (archivo .sql existe)
- [ ] Migraciones SQL ejecutadas sin errores
- [ ] Campo `Fecha_Creacion` es DATE en BD
- [ ] 5 índices creados
- [ ] 6 constraints creados
- [ ] 4 campos nuevos creados
- [ ] Proyecto compila sin errores
- [ ] Aplicación inicia correctamente
- [ ] Login funciona
- [ ] Endpoints responden correctamente

---

## 📊 RESUMEN DE CAMBIOS

### **Base de Datos:**
```
✅ historiaclinica.Fecha_Creacion: DATETIME → DATE
✅ 5 índices: idx_cita_fecha_veterinario, idx_cita_mascota, idx_cita_estado, 
              idx_mascota_cliente, idx_entrada_historia
✅ 6 constraints: chk_estado_cita, chk_rol_usuario, chk_duracion_cita, 
                  chk_hora_cita, chk_peso_positivo, chk_edad_positiva
✅ cita.Motivo_Cancelacion: TEXT NULL
✅ cita.Fecha_Actualizacion: TIMESTAMP
✅ usuario.Intentos_Fallidos: INT DEFAULT 0
✅ usuario.Fecha_Bloqueo: DATETIME NULL
```

### **Código Java:**
```
✅ HistoriaClinica.fechaCreacion: LocalDateTime → LocalDate
✅ Cita.motivoCancelacion: String (nuevo)
✅ Cita.fechaActualizacion: LocalDateTime (nuevo)
✅ Usuario.intentosFallidos: Integer (nuevo)
✅ Usuario.fechaBloqueo: LocalDateTime (nuevo)
```

---

## 🎯 PRÓXIMOS PASOS

Una vez completada esta migración, continuar con:

1. **Implementar endpoints nuevos** (ver ENDPOINTS_TO_IMPLEMENT.md)
2. **Agregar validaciones de seguridad** (ver RISK_ASSESSMENT.md)
3. **Crear GlobalExceptionHandler**
4. **Implementar tests unitarios**

---

## 📞 SOPORTE

**Si encuentras problemas:**
1. Revisa los logs de MySQL: `mysql> SHOW ERRORS;`
2. Revisa los logs de Spring Boot: `logs/clinicaveterinaria.log`
3. Verifica que el backup está disponible para restaurar
4. Consulta RISK_ASSESSMENT.md para problemas conocidos

---

**Última actualización:** 2025-01-20  
**Estado:** LISTO PARA EJECUTAR  
**Estimación de tiempo:** 30 minutos  
**Nivel de riesgo:** BAJO (con backup)

