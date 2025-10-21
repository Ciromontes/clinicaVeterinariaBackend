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

---

### 📅 2. CITAS

#### Listar todas las citas
```
GET http://localhost:8080/api/citas
Authorization: Bearer {TOKEN}
```

#### Citas de hoy (SOLO VETERINARIO/ADMIN)
```
GET http://localhost:8080/api/citas/hoy
Authorization: Bearer {TOKEN}
```

**Nota:** Debes iniciar sesión como veterinario:
```json
{
  "email": "ana.vet@clinicaveterinaria.com",
  "password": "vet123"
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

#### Actualizar estado de cita (SOLO VETERINARIO/ADMIN)
```
PUT http://localhost:8080/api/citas/1/estado
Authorization: Bearer {TOKEN}
Content-Type: application/json

{
  "estadoCita": "Completada",
  "observaciones": "Cita realizada exitosamente"
}
```

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

