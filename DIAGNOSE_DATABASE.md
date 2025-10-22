# 🔧 SCRIPT PARA VERIFICAR Y CORREGIR LA BASE DE DATOS

## 📋 Ejecuta estos comandos en MySQL para diagnosticar el problema

### 1. Verificar la estructura de la tabla mascota:
```sql
DESCRIBE mascota;
```

### 2. Ver todos los triggers en la tabla mascota:
```sql
SHOW TRIGGERS WHERE `Table` = 'mascota';
```

### 3. Ver la creación completa de la tabla:
```sql
SHOW CREATE TABLE mascota;
```

### 4. Verificar la estructura de historiaclinica:
```sql
DESCRIBE historiaclinica;
```

---

## 🔍 DIAGNÓSTICO DEL PROBLEMA

Basado en el error:
```
Field 'Fecha_Creacion' doesn't have a default value
INSERT INTO mascota (id_cliente,color,edad,especie,estado,fecha_registro,nombre,peso,raza,sexo)
```

**Posibles causas:**

1. **Trigger en la tabla mascota**: Existe un trigger BEFORE INSERT que intenta crear una historia clínica automáticamente
2. **Campo extra en la tabla**: La tabla mascota tiene un campo `Fecha_Creacion` que no está mapeado en Java
3. **Constraint de foreign key**: Una constraint está intentando insertar en historiaclinica

---

## ✅ SOLUCIÓN TEMPORAL: Deshabilitar creación automática

Si hay un trigger que crea la historia clínica, tenemos 2 opciones:

### Opción 1: Eliminar el trigger (RECOMENDADO)
```sql
-- Ver el trigger
SHOW TRIGGERS WHERE `Table` = 'mascota';

-- Eliminar el trigger (reemplaza NOMBRE_TRIGGER con el nombre real)
DROP TRIGGER IF EXISTS NOMBRE_TRIGGER;
```

### Opción 2: Modificar historiaclinica para que Fecha_Creacion tenga valor por defecto
```sql
ALTER TABLE historiaclinica 
MODIFY COLUMN Fecha_Creacion DATE DEFAULT (CURRENT_DATE);
```

---

## 📝 EJECUTA ESTOS COMANDOS Y DAME LOS RESULTADOS

Por favor ejecuta en MySQL:
```sql
DESCRIBE mascota;
SHOW TRIGGERS WHERE `Table` = 'mascota';
SHOW CREATE TABLE historiaclinica;
```

Y pégame los resultados para poder corregir el problema definitivamente.

