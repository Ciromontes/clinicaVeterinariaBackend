# 📮 GUÍA DE PRUEBAS CON POSTMAN

## 🎯 OBJETIVO
Probar todos los endpoints del backend de la Clínica Veterinaria usando Postman.

---

## 📊 ESTADO ACTUAL DE LA BASE DE DATOS

### 👤 Usuarios de Prueba
```
ADMIN:
- Email: admin@clinicaveterinaria.com
- Password: admin123

VETERINARIOS:
- Email: ana.vet@clinicaveterinaria.com | Password: vet123
- Email: juan.perez@veterinaria.com | Password: vet123
- Email: maria.rodriguez@veterinaria.com | Password: vet123

CLIENTES:
- Email: lucia.cliente@clinicaveterinaria.com | Password: cliente123
- Email: juan.perez@email.com | Password: 123456
- Email: pedro.ramirez@email.com | Password: 123456
```

---

## 🔐 PASO 1: AUTENTICACIÓN (LOGIN)

### Endpoint: `POST http://localhost:8080/api/auth/login`

**Body (JSON):**
```json
{
  "email": "admin@clinicaveterinaria.com",
  "password": "admin123"
}
```

**Respuesta Esperada:**
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

**⚠️ IMPORTANTE:** Copia el `token` de la respuesta para usarlo en las siguientes peticiones.

---

## 🔑 CONFIGURAR TOKEN EN POSTMAN

1. Selecciona la pestaña **Authorization**
2. Type: **Bearer Token**
3. Token: Pega el token obtenido en el login

---

## 📋 PRUEBAS POR MÓDULO

### 🐾 1. MASCOTAS

#### Listar todas las mascotas
```
GET http://localhost:8080/api/mascotas
Authorization: Bearer {TOKEN}
```

#### Obtener mascota por ID
```
GET http://localhost:8080/api/mascotas/1
Authorization: Bearer {TOKEN}
```

#### Crear nueva mascota
```
POST http://localhost:8080/api/mascotas
Authorization: Bearer {TOKEN}
Content-Type: application/json

{
  "nombre": "Rex",
  "especie": "Perro",
  "raza": "Pastor Alemán",
  "edad": 2,
  "peso": 30.5,
  "color": "Negro y Marrón",
  "sexo": "Macho",
  "idCliente": 1
}
```

**Nota:** Ahora usa `idCliente` directamente como número, es más simple.

---

### 📅 2. CITAS

#### Listar todas las citas (ADMIN/VETERINARIO)
```
GET http://localhost:8080/api/citas
Authorization: Bearer {TOKEN}
```

**Comportamiento por rol:**
- **ADMIN**: Ve TODAS las citas (supervisión)
- **VETERINARIO**: Ve TODAS las citas (coordinación)
- **CLIENTE**: Solo puede ver las citas de sus mascotas (mediante endpoint específico)

#### Citas de hoy (VETERINARIO/ADMIN)
```
GET http://localhost:8080/api/citas/hoy
Authorization: Bearer {TOKEN}
```

**Comportamiento por rol:**
- **VETERINARIO**: Ve solo SUS citas de hoy (agenda personal)
- **ADMIN**: Ve TODAS las citas de hoy (supervisión general)

**Login como VETERINARIO:**
```json
{
  "email": "ana.vet@clinicaveterinaria.com",
  "password": "vet123"
}
```

**Login como ADMIN:**
```json
{
  "email": "admin@clinicaveterinaria.com",
  "password": "admin123"
}
```

#### Crear nueva cita
```
POST http://localhost:8080/api/citas
Authorization: Bearer {TOKEN}
Content-Type: application/json

{
  "fechaCita": "2025-10-25",
  "horaCita": "14:30:00",
  "duracionMinutos": 30,
  "motivo": "Vacunación anual",
  "observaciones": "Traer cartilla de vacunación",
  "idMascota": 1,
  "idVeterinario": 3
}
```

#### Actualizar estado de cita (SOLO VETERINARIO)
```
PUT http://localhost:8080/api/citas/1/estado
Authorization: Bearer {TOKEN}
Content-Type: application/json

{
  "estado": "Completada"
}
```

**⚠️ IMPORTANTE:** 
- Solo el VETERINARIO asignado a la cita puede actualizar su estado
- ADMIN NO puede actualizar estados (es responsabilidad del veterinario)
- Para saber qué citas puede actualizar cada veterinario, ejecuta el script SQL: `IDENTIFICAR_CITAS_VETERINARIO.sql`

---

### 📋 3. HISTORIA CLÍNICA

#### Ver historial completo de una mascota
```
GET http://localhost:8080/api/historias/mascota/1/completo
Authorization: Bearer {TOKEN}
```

#### Agregar entrada a historia clínica (SOLO VETERINARIO/ADMIN)
```
POST http://localhost:8080/api/historias
Authorization: Bearer {TOKEN}
Content-Type: application/json

{
  "fechaEntrada": "2025-10-21",
  "descripcion": "Control rutinario. Mascota en buen estado de salud.",
  "observaciones": "Recomendar refuerzo de vacuna antirrábica en 6 meses",
  "pesoActual": 26.0,
  "temperatura": 38.5,
  "frecuenciaCardiaca": 115,
  "idHistoria": 1,
  "idVeterinario": 3
}
```

---

### 💊 4. TRATAMIENTOS

#### Listar tratamientos
```
GET http://localhost:8080/api/tratamientos
Authorization: Bearer {TOKEN}
```

#### Obtener tratamiento por ID
```
GET http://localhost:8080/api/tratamientos/7
Authorization: Bearer {TOKEN}
```

#### Crear tratamiento (SOLO VETERINARIO/ADMIN)
```
POST http://localhost:8080/api/tratamientos
Authorization: Bearer {TOKEN}
Content-Type: application/json

{
  "fechaTratamiento": "2025-10-21",
  "diagnostico": "Infección respiratoria leve",
  "tratamientoAplicado": "Antibiótico amoxicilina 250mg cada 8 horas por 7 días",
  "notas": "Control en 5 días para evaluar evolución",
  "estadoTratamiento": "En_Curso",
  "idCita": 4,
  "idVeterinario": 3
}
```

---

### 👨‍⚕️ 5. VETERINARIOS

#### Listar veterinarios
```
GET http://localhost:8080/api/veterinarios
Authorization: Bearer {TOKEN}
```

#### Obtener veterinario por ID
```
GET http://localhost:8080/api/veterinarios/1
Authorization: Bearer {TOKEN}
```

---

### 👥 6. CLIENTES

#### Listar clientes
```
GET http://localhost:8080/api/clientes
Authorization: Bearer {TOKEN}
```

#### Obtener cliente por ID
```
GET http://localhost:8080/api/clientes/1
Authorization: Bearer {TOKEN}
```

---

### 🔐 7. USUARIOS (SOLO ADMIN)

#### Listar todos los usuarios
```
GET http://localhost:8080/api/usuarios
Authorization: Bearer {TOKEN_ADMIN}
```

#### Activar/Desactivar usuario
```
PUT http://localhost:8080/api/usuarios/5/estado
Authorization: Bearer {TOKEN_ADMIN}
Content-Type: application/json

{
  "activo": false
}
```

---

## 🧪 SCRIPTS SQL DE PRUEBA

### Script para insertar datos adicionales

```sql
-- Insertar nuevo cliente
INSERT INTO cliente (Nombre, Apellidos, Identificacion, Direccion, Telefono, Correo, Estado)
VALUES ('Roberto', 'Gómez', '6070809010', 'Calle 70 #50-60', '3184567890', 'roberto.gomez@email.com', 'Activo');

-- Insertar nueva mascota para el cliente recién creado
INSERT INTO mascota (Nombre, Especie, Raza, Edad, Peso, Color, Sexo, Estado, ID_Cliente)
VALUES ('Manchas', 'Perro', 'Dálmata', 1, 18.5, 'Blanco con manchas negras', 'Macho', 'Activo', LAST_INSERT_ID());

-- Consultar citas programadas
SELECT c.ID_Cita, c.Fecha_Cita, c.Hora_Cita, c.Motivo, c.Estado_Cita,
       m.Nombre as Mascota, cl.Nombre as Cliente,
       v.Nombre as Veterinario
FROM cita c
JOIN mascota m ON c.ID_Mascota = m.ID_Mascota
JOIN cliente cl ON m.ID_Cliente = cl.ID_Cliente
JOIN veterinario v ON c.ID_Veterinario = v.ID_Veterinario
WHERE c.Estado_Cita = 'Programada'
ORDER BY c.Fecha_Cita, c.Hora_Cita;

-- Ver historias clínicas con sus entradas
SELECT hc.ID_Historia, m.Nombre as Mascota, 
       COUNT(eh.ID_Entrada) as Total_Entradas
FROM historiaclinica hc
JOIN mascota m ON hc.ID_Mascota = m.ID_Mascota
LEFT JOIN entradahistoria eh ON hc.ID_Historia = eh.ID_Historia
GROUP BY hc.ID_Historia, m.Nombre;
```

---

## ✅ CHECKLIST DE PRUEBAS

### Autenticación
- [ ] Login como ADMIN
- [ ] Login como VETERINARIO
- [ ] Login como CLIENTE
- [ ] Login con credenciales incorrectas (debe fallar)

### Mascotas
- [ ] Listar todas las mascotas
- [ ] Obtener mascota específica
- [ ] Crear nueva mascota
- [ ] Actualizar mascota

### Citas
- [ ] Listar todas las citas
- [ ] Ver citas de hoy (como veterinario)
- [ ] Crear nueva cita
- [ ] Actualizar estado de cita (como veterinario)
- [ ] Intentar actualizar cita como cliente (debe fallar)

### Historia Clínica
- [ ] Ver historial completo de mascota (como cliente dueño)
- [ ] Ver historial completo de mascota (como veterinario)
- [ ] Agregar entrada a historia (como veterinario)
- [ ] Intentar agregar entrada como cliente (debe fallar)

### Tratamientos
- [ ] Listar tratamientos
- [ ] Ver tratamiento específico
- [ ] Crear nuevo tratamiento (como veterinario)

### Administración
- [ ] Listar usuarios (como admin)
- [ ] Desactivar usuario (como admin)
- [ ] Reactivar usuario (como admin)
- [ ] Intentar gestionar usuarios como cliente (debe fallar)

---

## 🐛 TROUBLESHOOTING

### Error: "Token inválido"
- Verifica que copiaste el token completo
- El token expira después de 24 horas, genera uno nuevo

### Error: "Acceso denegado"
- Verifica que estás usando el rol correcto para ese endpoint
- Los endpoints con restricción de rol requieren VETERINARIO o ADMIN

### Error: "404 Not Found"
- Verifica que el backend esté corriendo en el puerto 8080
- Verifica que el ID del recurso exista en la base de datos

### El navegador redirige a /login
- Esto es normal, el backend sirve el frontend React
- Usa POSTMAN o curl para probar los endpoints de API

---

## 📌 PRÓXIMOS PASOS

1. ✅ Probar todos los endpoints básicos
2. ✅ Verificar permisos por rol
3. 🔄 Dockerizar frontend React
4. 🔄 Dockerizar landing page
5. 🚀 Desplegar en Azure

**IMPORTANTE:** Una vez confirmes que todo funciona, haremos commit y push a Git.

---

## 🧪 GUÍA PASO A PASO DE PRUEBAS

### 📊 PREPARACIÓN: Identificar datos de prueba

Primero, ejecuta este script SQL en MySQL para ver qué datos tienes disponibles:

```sql
-- Ejecuta el archivo: IDENTIFICAR_CITAS_VETERINARIO.sql
-- O copia estos comandos en MySQL:

-- Ver todas las citas de HOY
SELECT c.ID_Cita, c.Fecha_Cita, c.Hora_Cita, c.Estado_Cita, c.Motivo,
       m.Nombre as Mascota, v.Nombre as Veterinario, v.Correo
FROM cita c
JOIN mascota m ON c.ID_Mascota = m.ID_Mascota
JOIN veterinario v ON c.ID_Veterinario = v.ID_Veterinario
WHERE c.Fecha_Cita = CURDATE();

-- Si NO hay citas para hoy, crea una:
INSERT INTO cita (Fecha_Cita, Hora_Cita, Duracion_Minutos, Motivo, Estado_Cita, ID_Mascota, ID_Veterinario)
VALUES (CURDATE(), '09:00:00', 30, 'Vacunación de prueba', 'Programada', 1, 3);
```

---

### 🔐 PRUEBAS COMO ADMIN

#### 1. Login como ADMIN
```
POST http://localhost:8080/api/auth/login
Body: {"email": "admin@clinicaveterinaria.com", "password": "admin123"}
```
✅ Copia el TOKEN recibido

#### 2. Ver TODAS las citas de hoy (Supervisión)
```
GET http://localhost:8080/api/citas/hoy
Authorization: Bearer {TOKEN_ADMIN}
```
✅ **Esperado:** Ver TODAS las citas de hoy (de todos los veterinarios)

#### 3. Crear una nueva cita
```
POST http://localhost:8080/api/citas
Authorization: Bearer {TOKEN_ADMIN}
Body:
{
  "fechaCita": "2025-10-25",
  "horaCita": "14:30:00",
  "duracionMinutos": 30,
  "motivo": "Control general",
  "idMascota": 1,
  "idVeterinario": 3
}
```
✅ **Esperado:** Cita creada exitosamente

#### 4. Ver historial completo de cualquier mascota
```
GET http://localhost:8080/api/historias/mascota/1/completo
Authorization: Bearer {TOKEN_ADMIN}
```
✅ **Esperado:** Historial completo de la mascota (ADMIN puede ver todas las historias para supervisión)

#### 5. Listar todas las mascotas
```
GET http://localhost:8080/api/mascotas
Authorization: Bearer {TOKEN_ADMIN}
```
✅ **Esperado:** Lista completa de mascotas

#### 6. Intentar actualizar estado de cita (Debe fallar)
```
PUT http://localhost:8080/api/citas/1/estado
Authorization: Bearer {TOKEN_ADMIN}
Body: {"estado": "Completada"}
```
❌ **Esperado:** 403 Forbidden (Solo VETERINARIO puede actualizar estados)

---

### 👨‍⚕️ PRUEBAS COMO VETERINARIO (Dra. Ana)

#### 1. Login como VETERINARIO
```
POST http://localhost:8080/api/auth/login
Body: {"email": "ana.vet@clinicaveterinaria.com", "password": "vet123"}
```
✅ Copia el TOKEN recibido

#### 2. Ver TODAS las citas (Coordinación)
```
GET http://localhost:8080/api/citas
Authorization: Bearer {TOKEN_VETERINARIO}
```
✅ **Esperado:** Ver TODAS las citas del sistema (para coordinación entre veterinarios)

#### 3. Ver solo MIS citas de hoy (Agenda personal)
```
GET http://localhost:8080/api/citas/hoy
Authorization: Bearer {TOKEN_VETERINARIO}
```
✅ **Esperado:** Solo citas asignadas a la Dra. Ana para HOY (ID_Veterinario = 3)

**📝 Nota:** Si no aparecen citas, crea una en MySQL:
```sql
INSERT INTO cita (Fecha_Cita, Hora_Cita, Duracion_Minutos, Motivo, Estado_Cita, ID_Mascota, ID_Veterinario)
VALUES (CURDATE(), '10:00:00', 30, 'Consulta general', 'Programada', 1, 3);
```

#### 4. Crear una nueva cita
```
POST http://localhost:8080/api/citas
Authorization: Bearer {TOKEN_VETERINARIO}
Body:
{
  "fechaCita": "2025-10-26",
  "horaCita": "11:00:00",
  "duracionMinutos": 30,
  "motivo": "Vacunación antirrábica",
  "idMascota": 2,
  "idVeterinario": 3
}
```
✅ **Esperado:** Cita creada exitosamente

#### 5. Actualizar estado de MI cita
Primero, identifica una cita que pertenezca a la Dra. Ana (ID_Veterinario = 3):
```sql
SELECT ID_Cita FROM cita WHERE ID_Veterinario = 3 AND Estado_Cita = 'Programada' LIMIT 1;
```

Luego actualiza:
```
PUT http://localhost:8080/api/citas/{ID_CITA}/estado
Authorization: Bearer {TOKEN_VETERINARIO}
Body: {"estado": "Completada"}
```
✅ **Esperado:** Cita actualizada exitosamente

#### 6. Intentar actualizar cita de OTRO veterinario (Debe fallar)
```
PUT http://localhost:8080/api/citas/15/estado
Authorization: Bearer {TOKEN_VETERINARIO}
Body: {"estado": "Completada"}
```
❌ **Esperado:** "No puede modificar citas de otro veterinario"

#### 7. Ver historial completo de cualquier mascota
```
GET http://localhost:8080/api/historias/mascota/1/completo
Authorization: Bearer {TOKEN_VETERINARIO}
```
✅ **Esperado:** Historial completo (VETERINARIO puede ver todas las historias para atención médica)

#### 8. Agregar entrada a historia clínica
```
POST http://localhost:8080/api/historias/1/entrada
Authorization: Bearer {TOKEN_VETERINARIO}
Body:
{
  "descripcion": "Control rutinario. Mascota en excelente estado.",
  "observaciones": "Continuar con dieta actual",
  "pesoActual": 26.5,
  "temperatura": 38.2,
  "frecuenciaCardiaca": 120
}
```
✅ **Esperado:** Entrada agregada exitosamente

---

### 👤 PRUEBAS COMO CLIENTE

#### 1. Login como CLIENTE
```
POST http://localhost:8080/api/auth/login
Body: {"email": "lucia.cliente@clinicaveterinaria.com", "password": "cliente123"}
```
✅ Copia el TOKEN recibido

#### 2. Ver solo MIS mascotas
```
GET http://localhost:8080/api/mascotas/mias
Authorization: Bearer {TOKEN_CLIENTE}
```
✅ **Esperado:** Solo mascotas que pertenecen a Lucía

#### 3. Ver historial de MI mascota
```
GET http://localhost:8080/api/historias/mascota/1/completo
Authorization: Bearer {TOKEN_CLIENTE}
```
✅ **Esperado:** Historial completo (si la mascota le pertenece)
❌ **Esperado:** "No tiene permiso" (si la mascota NO le pertenece)

#### 4. Agendar una cita
```
POST http://localhost:8080/api/citas/agendar
Authorization: Bearer {TOKEN_CLIENTE}
Body:
{
  "fechaCita": "2025-10-27",
  "horaCita": "15:00:00",
  "motivo": "Consulta general",
  "idMascota": 1,
  "idVeterinario": 3
}
```
✅ **Esperado:** Cita agendada exitosamente

---

## 📊 RESUMEN DE PERMISOS

| Acción | ADMIN | VETERINARIO | CLIENTE |
|--------|-------|-------------|---------|
| Ver TODAS las citas | ✅ Supervisión | ✅ Coordinación | ❌ Solo las suyas |
| Ver citas de hoy | ✅ Todas | ✅ Solo las suyas | ❌ |
| Crear citas | ✅ | ✅ | ✅ (agendar) |
| Actualizar estado de cita | ❌ | ✅ Solo las suyas | ❌ |
| Ver TODAS las historias clínicas | ✅ Supervisión | ✅ Atención médica | ❌ Solo las suyas |
| Agregar entradas médicas | ✅ | ✅ | ❌ |
| Ver todas las mascotas | ✅ | ✅ | ❌ Solo las suyas |
| Crear mascotas | ✅ | ✅ | ✅ |
| Gestionar usuarios | ✅ | ❌ | ❌ |

---

## 🔍 COMANDOS ÚTILES PARA LOGS

Ver logs del backend en tiempo real:
```powershell
docker logs clinica-backend -f
```

Ver últimos 50 logs:
```powershell
docker logs clinica-backend --tail 50
```

Ver solo errores:
```powershell
docker logs clinica-backend 2>&1 | Select-String -Pattern "ERROR"
```

Ver logs de un endpoint específico:
```powershell
docker logs clinica-backend 2>&1 | Select-String -Pattern "/api/citas/hoy"
```

---

## ✅ CHECKLIST FINAL DE PRUEBAS

### Como ADMIN
- [ ] Login exitoso
- [ ] Ver TODAS las citas (supervisión) ✅
- [ ] Ver todas las citas de hoy (supervisión) ✅
- [ ] Crear nueva cita ✅
- [ ] Ver historial de cualquier mascota ✅
- [ ] Listar todas las mascotas ✅
- [ ] NO puede actualizar estado de citas ❌ (correcto, es responsabilidad del veterinario)

### Como VETERINARIO
- [ ] Login exitoso
- [ ] Ver TODAS las citas (coordinación) ✅
- [ ] Ver solo SUS citas de hoy (agenda personal) ✅
- [ ] Crear nueva cita ✅
- [ ] Actualizar estado de SU cita ✅
- [ ] NO puede actualizar citas de otro vet ❌ (correcto)
- [ ] Ver historial de cualquier mascota (atención médica) ✅
- [ ] Agregar entrada a historia clínica ✅

### Como CLIENTE
- [ ] Login exitoso
- [ ] Ver solo SUS mascotas ✅
- [ ] Ver historial de SU mascota ✅
- [ ] NO puede ver mascotas de otros ❌ (correcto)
- [ ] Agendar cita para su mascota ✅
- [ ] NO puede ver todas las citas ❌ (correcto)


