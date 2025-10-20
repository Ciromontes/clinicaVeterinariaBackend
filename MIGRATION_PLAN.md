# 🗄️ PLAN DE MIGRACIONES SQL - BASE DE DATOS MYSQL

> **Proyecto:** GA7-220501096-AA4-EV03  
> **Fecha:** Enero 20, 2025  
> **Estado Actual:** Base de datos MySQL funcional con estructura básica  
> **Objetivo:** Ajustar tablas existentes para cumplir especificación del frontend  

---

## 📊 ESTADO ACTUAL DE LA BASE DE DATOS

### ✅ Tablas Existentes:
1. **usuario** - Estructura correcta
2. **cliente** - Estructura correcta
3. **mascota** - Necesita ajustes menores
4. **cita** - Necesita ajustes menores
5. **historiaclinica** - Necesita ajuste de tipo de dato
6. **entradahistoria** - Estructura correcta

---

## 🔧 MIGRACIONES NECESARIAS

### **MIGRACIÓN 1: Ajustar tabla `historiaclinica`**
**Problema:** El campo `Fecha_Creacion` es `DATETIME` pero frontend espera `DATE`  
**Prioridad:** MEDIA  
**Impacto:** Bajo (solo formato de respuesta)

```sql
-- Archivo: V1_ajustar_historia_clinica_fecha.sql
-- Descripción: Cambiar tipo de dato de Fecha_Creacion de DATETIME a DATE

ALTER TABLE historiaclinica 
MODIFY COLUMN Fecha_Creacion DATE NOT NULL;
```

**Archivos Java a modificar después:**
- `HistoriaClinica.java` - Cambiar `LocalDateTime` a `LocalDate`
- `HistoriaClinicaServiceImpl.java` - Ajustar lógica de creación

---

### **MIGRACIÓN 2: Agregar índices para optimización**
**Problema:** Consultas frecuentes sin índices  
**Prioridad:** ALTA  
**Impacto:** Mejora de rendimiento 50-70%

```sql
-- Archivo: V2_agregar_indices_optimizacion.sql
-- Descripción: Agregar índices a campos más consultados

-- Índice para búsqueda de citas por fecha y veterinario
CREATE INDEX idx_cita_fecha_veterinario 
ON cita(Fecha_Cita, ID_Veterinario);

-- Índice para búsqueda de citas por mascota
CREATE INDEX idx_cita_mascota 
ON cita(ID_Mascota);

-- Índice para búsqueda de citas por estado
CREATE INDEX idx_cita_estado 
ON cita(Estado_Cita);

-- Índice para búsqueda de mascotas por cliente
CREATE INDEX idx_mascota_cliente 
ON mascota(ID_Cliente);

-- Índice para búsqueda de usuarios por email (ya existe UNIQUE)
-- No necesario, ya optimizado

-- Índice para búsqueda de entradas de historia
CREATE INDEX idx_entrada_historia 
ON entradahistoria(ID_Historia, Fecha_Entrada DESC);
```

**Archivos Java:** No requiere cambios

---

### **MIGRACIÓN 3: Agregar restricciones de negocio**
**Problema:** Falta validación a nivel de base de datos  
**Prioridad:** ALTA  
**Impacto:** Previene datos inconsistentes

```sql
-- Archivo: V3_agregar_constraints_validacion.sql
-- Descripción: Agregar CHECK constraints para validaciones de negocio

-- Validar estados de cita permitidos
ALTER TABLE cita 
ADD CONSTRAINT chk_estado_cita 
CHECK (Estado_Cita IN ('Programada', 'En curso', 'Completada', 'Cancelada'));

-- Validar roles de usuario permitidos
ALTER TABLE usuario 
ADD CONSTRAINT chk_rol_usuario 
CHECK (rol IN ('ADMIN', 'VETERINARIO', 'RECEPCIONISTA', 'CLIENTE'));

-- Validar duración de cita (entre 15 y 120 minutos)
ALTER TABLE cita 
ADD CONSTRAINT chk_duracion_cita 
CHECK (Duracion_Minutos BETWEEN 15 AND 120);

-- Validar hora de cita (entre 08:00 y 18:00)
ALTER TABLE cita 
ADD CONSTRAINT chk_hora_cita 
CHECK (Hora_Cita BETWEEN '08:00:00' AND '18:00:00');

-- Validar que fecha de cita no sea pasada (se valida en aplicación)
-- No se puede hacer con CHECK dinámico en MySQL

-- Validar peso positivo
ALTER TABLE mascota 
ADD CONSTRAINT chk_peso_positivo 
CHECK (peso > 0);

-- Validar edad positiva
ALTER TABLE mascota 
ADD CONSTRAINT chk_edad_positiva 
CHECK (edad >= 0);
```

**Archivos Java a modificar después:**
- Remover validaciones duplicadas en servicios (opcional)

---

### **MIGRACIÓN 4: Agregar campos faltantes para nuevas funcionalidades**
**Problema:** Funcionalidades nuevas requieren campos adicionales  
**Prioridad:** MEDIA  
**Impacto:** Habilita nuevas features

```sql
-- Archivo: V4_agregar_campos_nuevas_funcionalidades.sql
-- Descripción: Agregar campos para notificaciones y cancelaciones

-- Campo para motivo de cancelación en citas
ALTER TABLE cita 
ADD COLUMN Motivo_Cancelacion TEXT NULL AFTER Observaciones;

-- Timestamp de última modificación
ALTER TABLE cita 
ADD COLUMN Fecha_Actualizacion TIMESTAMP 
DEFAULT CURRENT_TIMESTAMP 
ON UPDATE CURRENT_TIMESTAMP;

-- Campo para controlar intentos fallidos de login
ALTER TABLE usuario 
ADD COLUMN Intentos_Fallidos INT DEFAULT 0 AFTER activo;

ALTER TABLE usuario 
ADD COLUMN Fecha_Bloqueo DATETIME NULL AFTER Intentos_Fallidos;
```

**Archivos Java a modificar después:**
- `Cita.java` - Agregar campos `motivoCancelacion` y `fechaActualizacion`
- `Usuario.java` - Agregar campos `intentosFallidos` y `fechaBloqueo`

---

### **MIGRACIÓN 5: Crear tabla de notificaciones (OPCIONAL - PRIORIDAD BAJA)**
**Problema:** Sistema de notificaciones requiere almacenamiento  
**Prioridad:** BAJA  
**Impacto:** Feature nueva

```sql
-- Archivo: V5_crear_tabla_notificaciones.sql
-- Descripción: Tabla para sistema de notificaciones en tiempo real

CREATE TABLE notificacion (
    ID_Notificacion INT PRIMARY KEY AUTO_INCREMENT,
    ID_Usuario INT NOT NULL,
    Tipo_Notificacion ENUM('CITA_PROXIMA', 'CITA_CANCELADA', 'RECORDATORIO', 'CAMBIO_ESTADO') NOT NULL,
    Mensaje TEXT NOT NULL,
    Leida BOOLEAN DEFAULT FALSE,
    Fecha_Creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    Fecha_Lectura DATETIME NULL,
    FOREIGN KEY (ID_Usuario) REFERENCES usuario(id),
    INDEX idx_usuario_leida (ID_Usuario, Leida),
    INDEX idx_fecha_creacion (Fecha_Creacion DESC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

**Archivos Java a crear después:**
- `Notificacion.java` (modelo)
- `NotificacionRepository.java`
- `NotificacionService.java`
- `NotificacionController.java`

---

### **MIGRACIÓN 6: Crear tabla de disponibilidad de veterinarios (OPCIONAL)**
**Problema:** Necesario para mostrar horarios disponibles  
**Prioridad:** MEDIA  
**Impacto:** Mejora UX al agendar citas

```sql
-- Archivo: V6_crear_tabla_disponibilidad.sql
-- Descripción: Tabla para gestionar horarios de veterinarios

CREATE TABLE disponibilidad_veterinario (
    ID_Disponibilidad INT PRIMARY KEY AUTO_INCREMENT,
    ID_Veterinario INT NOT NULL,
    Dia_Semana ENUM('Lunes', 'Martes', 'Miercoles', 'Jueves', 'Viernes', 'Sabado', 'Domingo') NOT NULL,
    Hora_Inicio TIME NOT NULL,
    Hora_Fin TIME NOT NULL,
    Activo BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (ID_Veterinario) REFERENCES usuario(id_veterinario),
    INDEX idx_veterinario_dia (ID_Veterinario, Dia_Semana)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Datos de ejemplo (veterinarios trabajan L-V 8:00-18:00)
INSERT INTO disponibilidad_veterinario (ID_Veterinario, Dia_Semana, Hora_Inicio, Hora_Fin) 
SELECT id_veterinario, 'Lunes', '08:00:00', '18:00:00' 
FROM usuario WHERE rol = 'VETERINARIO';

INSERT INTO disponibilidad_veterinario (ID_Veterinario, Dia_Semana, Hora_Inicio, Hora_Fin) 
SELECT id_veterinario, 'Martes', '08:00:00', '18:00:00' 
FROM usuario WHERE rol = 'VETERINARIO';

-- Repetir para Miercoles, Jueves, Viernes
```

---

## 📝 SCRIPT COMPLETO DE MIGRACIÓN

### **Archivo: ejecutar_todas_migraciones.sql**

```sql
-- ============================================
-- SCRIPT MAESTRO DE MIGRACIONES
-- Ejecutar en orden secuencial
-- ============================================

USE clinicaveterinaria;

-- BACKUP RECOMENDADO ANTES DE EJECUTAR
-- mysqldump -u root -p clinicaveterinaria > backup_antes_migracion.sql

START TRANSACTION;

-- MIGRACIÓN 1: Ajustar fecha de historia clínica
ALTER TABLE historiaclinica 
MODIFY COLUMN Fecha_Creacion DATE NOT NULL;

-- MIGRACIÓN 2: Índices de optimización
CREATE INDEX idx_cita_fecha_veterinario ON cita(Fecha_Cita, ID_Veterinario);
CREATE INDEX idx_cita_mascota ON cita(ID_Mascota);
CREATE INDEX idx_cita_estado ON cita(Estado_Cita);
CREATE INDEX idx_mascota_cliente ON mascota(ID_Cliente);
CREATE INDEX idx_entrada_historia ON entradahistoria(ID_Historia, Fecha_Entrada DESC);

-- MIGRACIÓN 3: Constraints de validación
ALTER TABLE cita 
ADD CONSTRAINT chk_estado_cita 
CHECK (Estado_Cita IN ('Programada', 'En curso', 'Completada', 'Cancelada'));

ALTER TABLE usuario 
ADD CONSTRAINT chk_rol_usuario 
CHECK (rol IN ('ADMIN', 'VETERINARIO', 'RECEPCIONISTA', 'CLIENTE'));

ALTER TABLE cita 
ADD CONSTRAINT chk_duracion_cita 
CHECK (Duracion_Minutos BETWEEN 15 AND 120);

ALTER TABLE cita 
ADD CONSTRAINT chk_hora_cita 
CHECK (Hora_Cita BETWEEN '08:00:00' AND '18:00:00');

ALTER TABLE mascota 
ADD CONSTRAINT chk_peso_positivo CHECK (peso > 0);

ALTER TABLE mascota 
ADD CONSTRAINT chk_edad_positiva CHECK (edad >= 0);

-- MIGRACIÓN 4: Campos nuevas funcionalidades
ALTER TABLE cita 
ADD COLUMN Motivo_Cancelacion TEXT NULL AFTER Observaciones;

ALTER TABLE cita 
ADD COLUMN Fecha_Actualizacion TIMESTAMP 
DEFAULT CURRENT_TIMESTAMP 
ON UPDATE CURRENT_TIMESTAMP;

ALTER TABLE usuario 
ADD COLUMN Intentos_Fallidos INT DEFAULT 0 AFTER activo;

ALTER TABLE usuario 
ADD COLUMN Fecha_Bloqueo DATETIME NULL AFTER Intentos_Fallidos;

COMMIT;

-- Verificar migraciones
SELECT 'Migraciones completadas exitosamente' AS resultado;
```

---

## ⚠️ PRECAUCIONES Y ROLLBACK

### **Backup antes de migrar:**
```bash
# Windows CMD
cd C:\Program Files\MySQL\MySQL Server 8.0\bin
mysqldump -u root -p clinicaveterinaria > D:\backup_clinica_%date:~-4,4%%date:~-7,2%%date:~-10,2%.sql
```

### **Rollback en caso de error:**
```sql
-- Si algo falla durante la migración
ROLLBACK;

-- O restaurar desde backup
-- mysql -u root -p clinicaveterinaria < backup_clinica_20250120.sql
```

---

## 📂 ORDEN DE MODIFICACIÓN DE ARCHIVOS JAVA

### **Después de ejecutar migraciones SQL:**

1. **Modelos (models/):**
   - `HistoriaClinica.java` - Cambiar LocalDateTime a LocalDate
   - `Cita.java` - Agregar motivoCancelacion, fechaActualizacion
   - `Usuario.java` - Agregar intentosFallidos, fechaBloqueo
   - `Notificacion.java` - Crear nuevo (opcional)

2. **Repositorios (repository/):**
   - `CitaRepository.java` - Agregar métodos de filtrado
   - `MascotaRepository.java` - Agregar métodos de búsqueda
   - `UsuarioRepository.java` - Agregar búsqueda y conteo
   - `NotificacionRepository.java` - Crear nuevo (opcional)

3. **Servicios (service/ e impl/):**
   - Actualizar lógica según nuevos campos
   - Implementar nuevos métodos de negocio

4. **Controladores (controller/):**
   - Agregar nuevos endpoints
   - Validar request/response

---

## ✅ CHECKLIST DE EJECUCIÓN

- [ ] Crear backup de base de datos
- [ ] Revisar conexiones activas (cerrar aplicación)
- [ ] Ejecutar `ejecutar_todas_migraciones.sql`
- [ ] Verificar que no haya errores
- [ ] Probar consultas básicas (SELECT)
- [ ] Modificar modelos Java
- [ ] Compilar proyecto (verificar errores)
- [ ] Ejecutar tests (si existen)
- [ ] Probar endpoints existentes
- [ ] Documentar cambios en changelog

---

## 🎯 RESUMEN DE IMPACTO

| Migración | Tiempo Estimado | Riesgo | Impacto en Código Java |
|-----------|----------------|--------|------------------------|
| 1. Ajustar fecha | 1 min | Bajo | 2 archivos |
| 2. Índices | 2 min | Muy Bajo | 0 archivos |
| 3. Constraints | 3 min | Bajo | 0 archivos |
| 4. Campos nuevos | 2 min | Medio | 2 archivos |
| 5. Notificaciones | 5 min | Bajo | 4 archivos nuevos |
| 6. Disponibilidad | 5 min | Bajo | 4 archivos nuevos |

**Total:** 18 minutos + 30 minutos de modificación de código Java  
**Ventana de mantenimiento recomendada:** 1 hora

---

**Última actualización:** Enero 20, 2025  
**Estado:** PLANIFICACIÓN - NO EJECUTAR AÚN

