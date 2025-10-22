# 📮 GUÍA DE PRUEBAS CON POSTMAN

## 🎯 OBJETIVO
Probar todos los endpoints del backend de la Clínica Veterinaria usando Postman, validando permisos por rol.

---

## 📊 USUARIOS DE PRUEBA

### 🔐 Credenciales Validadas

**ADMIN:**
```json
{
  "email": "admin@clinicaveterinaria.com",
  "password": "admin123"
}
```

**VETERINARIOS:**
```json
{
  "email": "ana.vet@clinicaveterinaria.com",
  "password": "vet123"
}
```
```json
{
  "email": "juan.perez@veterinaria.com",
  "password": "vet123"
}
```
```json
{
  "email": "maria.rodriguez@veterinaria.com",
  "password": "vet123"
}
```

**CLIENTES:**
```json
{
  "email": "lucia.cliente@clinicaveterinaria.com",
  "password": "cliente123"
}
```
```json
{
  "email": "juan.perez@email.com",
  "password": "123456"
}
```

---

## 🔐 PASO 1: AUTENTICACIÓN (LOGIN)

### Endpoint
```
POST http://localhost:8080/api/auth/login
```

### Body (JSON)
```json
{
  "email": "admin@clinicaveterinaria.com",
  "password": "admin123"
}
```

### Respuesta Esperada
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "tipo": "Bearer",
  "usuario": {
    "id": 2,
    "nombre": "Administrador General",
    "email": "admin@clinicaveterinaria.com",
    "rol": "ADMIN"
  }
}
```

### 🔑 Configurar Token en Postman
1. Copia el `token` de la respuesta
2. En Postman, selecciona la pestaña **Authorization**
3. Type: **Bearer Token**
4. Token: Pega el token copiado

---

## 🧪 PRUEBAS COMPLETAS POR ROL

---

## 👤 PRUEBAS COMO CLIENTE

### 1️⃣ Login
```
POST http://localhost:8080/api/auth/login
```
```json
{
  "email": "lucia.cliente@clinicaveterinaria.com",
  "password": "cliente123"
}
```

### 2️⃣ Ver MIS mascotas ✅
```
GET http://localhost:8080/api/mascotas/mias
```
**Esperado:** Solo mascotas que pertenecen a Lucía

### 3️⃣ Ver MIS citas ✅
```
GET http://localhost:8080/api/citas/mis-citas
```
**Esperado:** Solo citas de las mascotas de Lucía (NO las 44 del sistema)

### 4️⃣ Ver historial de MI mascota ✅
```
GET http://localhost:8080/api/historias/mascota/1/completo
```
**Esperado:** Historial completo si la mascota le pertenece

**Respuesta esperada:**
```json
{
  "historia": {
    "idHistoria": 1,
    "fechaCreacion": "2025-08-24",
    "idMascota": 1
  },
  "entradas": [
    {
      "idEntrada": 1,
      "descripcion": "Control de rutina...",
      "pesoActual": 25.50,
      "temperatura": 38.30
    }
  ],
  "mascotaId": 1,
  "totalEntradas": 1
}
```

### 5️⃣ Agendar nueva cita ✅
```
POST http://localhost:8080/api/citas/agendar
```
```json
{
  "fechaCita": "2025-10-28",
  "horaCita": "09:30:00",
  "motivo": "Vacunación antirrábica",
  "idMascota": 1,
  "idVeterinario": 3
}
```

### 6️⃣ Agendar otra cita ✅
```
POST http://localhost:8080/api/citas/agendar
```
```json
{
  "fechaCita": "2025-10-29",
  "horaCita": "14:00:00",
  "motivo": "Control de peso",
  "idMascota": 1,
  "idVeterinario": 3
}
```

### 7️⃣ Intentar agregar entrada médica ❌
```
POST http://localhost:8080/api/historias/1/entrada
```
```json
{
  "descripcion": "Intento ilegal",
  "pesoActual": 25.0,
  "temperatura": 38.0,
  "frecuenciaCardiaca": 120
}
```
**Esperado:** `403 Forbidden` (Solo VETERINARIO puede)

### 8️⃣ Intentar ver TODAS las citas ❌
```
GET http://localhost:8080/api/citas
```
**Esperado:** `403 Forbidden` (Debe usar `/mis-citas`)

### 9️⃣ Intentar ver citas de hoy ❌
```
GET http://localhost:8080/api/citas/hoy
```
**Esperado:** `403 Forbidden` (Solo VETERINARIO/ADMIN)

---

## 👨‍⚕️ PRUEBAS COMO VETERINARIO

### 1️⃣ Login
```
POST http://localhost:8080/api/auth/login
```
```json
{
  "email": "ana.vet@clinicaveterinaria.com",
  "password": "vet123"
}
```

### 2️⃣ Ver TODAS las citas (Coordinación) ✅
```
GET http://localhost:8080/api/citas
```
**Esperado:** Todas las citas del sistema (coordinación entre veterinarios)

### 3️⃣ Ver solo MIS citas de hoy ✅
```
GET http://localhost:8080/api/citas/hoy
```
**Esperado:** Solo citas de la Dra. Ana (ID_Veterinario = 3) para HOY

### 4️⃣ Crear nueva cita ✅
```
POST http://localhost:8080/api/citas
```
```json
{
  "fechaCita": "2025-10-30",
  "horaCita": "11:00:00",
  "duracionMinutos": 30,
  "motivo": "Revisión post-operatoria",
  "idMascota": 2,
  "idVeterinario": 3
}
```

### 5️⃣ Ver historial de cualquier mascota ✅
```
GET http://localhost:8080/api/historias/mascota/1/completo
```
**Esperado:** Historial completo (VETERINARIO puede ver todas para atención médica)

### 6️⃣ Agregar entrada médica ✅
```
POST http://localhost:8080/api/historias/1/entrada
```
```json
{
  "descripcion": "Control rutinario. Mascota presenta buen estado general.",
  "observaciones": "Recomendar refuerzo de vacuna en 6 meses",
  "pesoActual": 26.5,
  "temperatura": 38.3,
  "frecuenciaCardiaca": 118
}
```

### 7️⃣ Actualizar estado de MI cita ✅
**Primero, obtén una cita:**
```
GET http://localhost:8080/api/citas/hoy
```

**Luego actualiza (usa el ID obtenido):**
```
PUT http://localhost:8080/api/citas/{ID_CITA}/estado
```
```json
{
  "estado": "Completada"
}
```

### 8️⃣ Intentar actualizar cita de OTRO veterinario ❌
```
PUT http://localhost:8080/api/citas/1/estado
```
```json
{
  "estado": "Completada"
}
```
**Esperado:** Error "No puede modificar citas de otro veterinario"

---

## 🔐 PRUEBAS COMO ADMIN

### 1️⃣ Login
```
POST http://localhost:8080/api/auth/login
```
```json
{
  "email": "admin@clinicaveterinaria.com",
  "password": "admin123"
}
```

### 2️⃣ Ver TODAS las citas (Supervisión) ✅
```
GET http://localhost:8080/api/citas
```
**Esperado:** Lista completa de todas las citas del sistema

### 3️⃣ Ver TODAS las citas de hoy ✅
```
GET http://localhost:8080/api/citas/hoy
```
**Esperado:** Todas las citas de hoy (de todos los veterinarios)

### 4️⃣ Crear nueva cita ✅
```
POST http://localhost:8080/api/citas
```
```json
{
  "fechaCita": "2025-10-31",
  "horaCita": "16:00:00",
  "duracionMinutos": 30,
  "motivo": "Revisión administrativa",
  "idMascota": 2,
  "idVeterinario": 3
}
```

### 5️⃣ Ver historial de cualquier mascota ✅
```
GET http://localhost:8080/api/historias/mascota/1/completo
```
**Esperado:** Historial completo (ADMIN supervisa todas las historias)

### 6️⃣ Ver historial de otra mascota ✅
```
GET http://localhost:8080/api/historias/mascota/2/completo
```
**Esperado:** Historial completo (sin restricción)

### 7️⃣ Listar todas las mascotas ✅
```
GET http://localhost:8080/api/mascotas
```
**Esperado:** Lista completa de todas las mascotas del sistema

### 8️⃣ Intentar actualizar estado de cita ❌
```
PUT http://localhost:8080/api/citas/1/estado
```
```json
{
  "estado": "Completada"
}
```
**Esperado:** `403 Forbidden` (Solo VETERINARIO asignado puede actualizar)

### 9️⃣ Listar todos los usuarios ✅
```
GET http://localhost:8080/api/usuarios
```
**Esperado:** Lista de todos los usuarios (ADMIN, VETERINARIOS, CLIENTES)

### 🔟 Activar/Desactivar usuario ✅
**Primero, desactiva un usuario en MySQL:**
```sql
UPDATE usuario SET activo = 0 WHERE id = 9;
```

**Luego actívalo con la API:**
```
PUT http://localhost:8080/api/usuarios/9/estado
```
```json
{
  "activo": true
}
```

**Desactívalo nuevamente:**
```
PUT http://localhost:8080/api/usuarios/9/estado
```
```json
{
  "activo": false
}
```

---

## 📊 RESUMEN DE PERMISOS VALIDADOS

| Endpoint | CLIENTE | VETERINARIO | ADMIN |
|----------|---------|-------------|-------|
| `GET /api/citas` | ❌ 403 | ✅ Todas | ✅ Todas |
| `GET /api/citas/mis-citas` | ✅ Propias | ✅ Propias | ✅ Propias |
| `GET /api/citas/hoy` | ❌ 403 | ✅ Suyas hoy | ✅ Todas hoy |
| `POST /api/citas/agendar` | ✅ Sí | ✅ Sí | ✅ Sí |
| `PUT /api/citas/{id}/estado` | ❌ 403 | ✅ Solo suyas | ❌ 403 |
| `GET /api/mascotas` | ❌ 403 | ✅ Todas | ✅ Todas |
| `GET /api/mascotas/mias` | ✅ Propias | ✅ Propias | ✅ Propias |
| `GET /api/historias/mascota/{id}/completo` | ✅ Propias | ✅ Todas | ✅ Todas |
| `POST /api/historias/{id}/entrada` | ❌ 403 | ✅ Sí | ✅ Sí |
| `GET /api/usuarios` | ❌ 403 | ❌ 403 | ✅ Todos |
| `PUT /api/usuarios/{id}/estado` | ❌ 403 | ❌ 403 | ✅ Sí |

---

## 💊 ENDPOINTS ADICIONALES

### Tratamientos

#### Listar tratamientos
```
GET http://localhost:8080/api/tratamientos
```

#### Crear tratamiento (VETERINARIO/ADMIN)
```
POST http://localhost:8080/api/tratamientos
```
```json
{
  "fechaTratamiento": "2025-10-22",
  "diagnostico": "Infección respiratoria leve",
  "tratamientoAplicado": "Antibiótico amoxicilina 250mg cada 8 horas por 7 días",
  "notas": "Control en 5 días",
  "estadoTratamiento": "En_Curso",
  "idCita": 4,
  "idVeterinario": 3
}
```

### Veterinarios

#### Listar veterinarios
```
GET http://localhost:8080/api/veterinarios
```

### Clientes

#### Listar clientes (ADMIN/VETERINARIO)
```
GET http://localhost:8080/api/clientes
```

---

## 📅 HORARIOS DISPONIBLES PARA NUEVAS CITAS

**Para evitar conflictos de citas duplicadas:**

### Veterinario Ana (ID: 3)
- 2025-10-28 09:30:00
- 2025-10-28 15:00:00
- 2025-10-29 10:00:00
- 2025-10-29 14:00:00
- 2025-10-30 11:00:00

### Veterinario Juan (ID: 1)
- 2025-10-28 08:00:00
- 2025-10-29 16:00:00
- 2025-10-30 09:00:00

### Veterinario María (ID: 2)
- 2025-10-28 13:00:00
- 2025-10-29 11:30:00
- 2025-10-30 14:30:00

---

## 🔍 SCRIPTS SQL ÚTILES

### Verificar citas existentes
```sql
SELECT ID_Cita, Fecha_Cita, Hora_Cita, ID_Veterinario, Motivo, Estado_Cita
FROM cita
WHERE Fecha_Cita >= CURDATE()
ORDER BY Fecha_Cita, Hora_Cita;
```

### Ver usuarios activos
```sql
SELECT id, nombre, email, rol, activo
FROM usuario
ORDER BY rol, nombre;
```

### Ver mascotas por cliente
```sql
SELECT m.ID_Mascota, m.Nombre as Mascota, m.Especie, m.Raza,
       c.Nombre as Cliente, c.Correo
FROM mascota m
JOIN cliente c ON m.ID_Cliente = c.ID_Cliente
WHERE c.ID_Cliente = 1;
```

---

## 🐛 TROUBLESHOOTING

### Error: "Token inválido"
- Verifica que copiaste el token completo
- El token expira después de 24 horas, genera uno nuevo con login

### Error: "403 Forbidden"
- ✅ CORRECTO si estás probando restricciones de permisos
- ❌ INCORRECTO si deberías tener acceso → Verifica el rol del usuario

### Error: "Duplicate entry '1-2025-10-25-10:00:00'"
- Ya existe una cita para ese veterinario en esa fecha/hora
- Usa los horarios disponibles listados arriba

### Error: "No puede modificar citas de otro veterinario"
- Solo el veterinario asignado puede actualizar el estado de sus citas
- Verifica que el ID_Veterinario de la cita coincida con el usuario logueado

---

## ✅ CHECKLIST DE VALIDACIÓN

### CLIENTE ✅
- [x] Login exitoso
- [x] Ve solo SUS mascotas
- [x] Ve solo SUS citas
- [x] Ve historial de SU mascota
- [x] Agenda nueva cita
- [x] NO puede agregar entrada médica (403)
- [x] NO puede ver todas las citas (403)
- [x] NO puede ver citas de hoy (403)

### VETERINARIO ✅
- [x] Login exitoso
- [x] Ve TODAS las citas (coordinación)
- [x] Ve solo SUS citas de hoy (agenda)
- [x] Crea nueva cita
- [x] Ve historial de cualquier mascota
- [x] Agrega entrada médica
- [x] Actualiza estado de SU cita
- [x] NO puede actualizar cita de otro veterinario

### ADMIN ✅
- [x] Login exitoso
- [x] Ve TODAS las citas (supervisión)
- [x] Ve TODAS las citas de hoy
- [x] Crea nueva cita
- [x] Ve historial de cualquier mascota
- [x] Ve todas las mascotas
- [x] NO puede actualizar estados de citas (403)
- [x] Lista todos los usuarios
- [x] Activa/desactiva usuarios

---

## 🎉 ESTADO FINAL

**✅ TODAS LAS PRUEBAS HAN SIDO VALIDADAS EXITOSAMENTE**

El sistema implementa correctamente:
- Autenticación por roles (JWT)
- Permisos diferenciados CLIENTE/VETERINARIO/ADMIN
- Restricciones de acceso a datos propios (CLIENTE)
- Coordinación entre veterinarios
- Supervisión administrativa
- Gestión de usuarios

---

**📌 Última actualización:** 2025-10-22  
**🔒 Versión:** 1.0 - Sistema de permisos validado

