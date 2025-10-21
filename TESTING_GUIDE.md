
**Request:** `GET /api/historias/mascota/10/completo`  
(Mascota ID 10 no pertenece al cliente logueado)

**Response esperado:**
- Status: `403 Forbidden`
- Mensaje: "No tiene permiso para ver esta mascota"

---

### **TEST E4: Agendar cita con fecha pasada** ❌

**Request:**
```json
{
  "fechaCita": "2024-01-01",
  "horaCita": "10:00:00",
  "motivo": "Test",
  "idMascota": 1,
  "idVeterinario": 1
}
```

**Response esperado:**
- Status: `400 Bad Request`
- Mensaje: "No se pueden agendar citas en fechas pasadas"

---

## 📊 CHECKLIST COMPLETO DE TESTING

### **Backend (Postman):**
- [ ] Login Admin funciona
- [ ] Login Cliente funciona
- [ ] Login Veterinario funciona
- [ ] Token se genera correctamente
- [ ] GET /api/usuarios/veterinarios/activos (sin auth)
- [ ] GET /api/mascotas/mias (con auth cliente)
- [ ] POST /api/citas (agendar como cliente)
- [ ] GET /api/citas/hoy (como veterinario)
- [ ] PUT /api/citas/{id}/estado (como veterinario)
- [ ] PUT /api/citas/{id}/cancelar (como cliente)
- [ ] GET /api/historias/mascota/{id}/completo
- [ ] POST /api/historias/{id}/entrada (como veterinario)
- [ ] Errores 401/403 funcionan correctamente

### **Frontend:**
- [ ] Login de 3 roles funciona
- [ ] Dashboard específico por rol
- [ ] Menú cambia según rol
- [ ] Cliente ve sus mascotas
- [ ] Cliente agenda citas
- [ ] Cliente cancela citas
- [ ] Cliente ve historia clínica
- [ ] Veterinario ve citas de hoy
- [ ] Veterinario cambia estados
- [ ] Veterinario agrega entradas médicas
- [ ] Logout limpia sesión
- [ ] Rutas protegidas funcionan
- [ ] Manejo de errores (mensajes claros)

---

**Total de tests:** 24 (12 Postman + 12 Frontend)  
**Tiempo estimado:** 45-60 minutos  
**Última actualización:** 2025-01-20
# 🧪 GUÍA DE TESTING - POSTMAN & FRONTEND

> **Proyecto:** Clínica Veterinaria  
> **Fecha:** 2025-01-20  
> **Backend:** http://localhost:8080  
> **Frontend:** http://localhost:5173 (o puerto que uses)  

---

## 📋 USUARIOS DE PRUEBA

### **Credenciales disponibles:**

| Email | Password | Rol | ID Cliente | ID Veterinario |
|-------|----------|-----|------------|----------------|
| `admin@clinicaveterinaria.com` | `admin123` | ADMIN | - | - |
| `ana.vet@clinicaveterinaria.com` | `vet123` | VETERINARIO | - | 3 |
| `juan.perez@veterinaria.com` | `vet123` | VETERINARIO | - | 1 |
| `maria.rodriguez@veterinaria.com` | `vet123` | VETERINARIO | - | 2 |
| `lucia.cliente@clinicaveterinaria.com` | `cliente123` | CLIENTE | 1 | - |
| `juan.perez@email.com` | `123456` | CLIENTE | 1 | - |
| `carlos.martinez@email.com` | `123456` | CLIENTE | 2 | - |

---

## 🔧 CONFIGURACIÓN DE POSTMAN

### **1. Crear nueva Collection**
- Nombre: `Clínica Veterinaria API`
- Base URL: `http://localhost:8080`

### **2. Crear Variable de Entorno**
```json
{
  "token": "",
  "baseUrl": "http://localhost:8080"
}
```

### **3. Configurar Authorization automática**
En la Collection → Authorization:
- Type: `Bearer Token`
- Token: `{{token}}`

---

## 🚀 TESTS EN POSTMAN (PASO A PASO)

### **TEST 1: Login (Obtener Token)** ✅

**Endpoint:** `POST /api/auth/login`

**Request Body:**
```json
{
  "email": "admin@clinicaveterinaria.com",
  "password": "admin123"
}
```

**Response esperado:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "email": "admin@clinicaveterinaria.com",
  "rol": "ADMIN"
}
```

**Tests Script (en Postman):**
```javascript
// Guardar token automáticamente
if (pm.response.code === 200) {
    const jsonData = pm.response.json();
    pm.environment.set("token", jsonData.token);
    console.log("Token guardado:", jsonData.token);
}
```

**✅ Verificación:** Variable `token` debe tener valor en Environment

---

### **TEST 2: Listar Veterinarios Activos** ✅

**Endpoint:** `GET /api/usuarios/veterinarios/activos`  
**Authorization:** ❌ No requiere token (público)

**Response esperado:**
```json
[
  {
    "id": 3,
    "nombre": "Ana",
    "email": "ana.vet@clinicaveterinaria.com",
    "rol": "VETERINARIO",
    "activo": true,
    "idVeterinario": 3
  },
  {
    "id": 10,
    "nombre": "Dr. Juan Carlos Pérez",
    "email": "juan.perez@veterinaria.com",
    "rol": "VETERINARIO",
    "activo": true,
    "idVeterinario": 1
  }
]
```

**✅ Verificación:**
- Status: `200 OK`
- Array con veterinarios
- Todos tienen `activo: true`
- Todos tienen `idVeterinario` no nulo

---

### **TEST 3: Login como Cliente** ✅

**Endpoint:** `POST /api/auth/login`

**Request Body:**
```json
{
  "email": "lucia.cliente@clinicaveterinaria.com",
  "password": "cliente123"
}
```

**Response esperado:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "email": "lucia.cliente@clinicaveterinaria.com",
  "rol": "CLIENTE"
}
```

**✅ Guardar este token como `tokenCliente` en Environment**

---

### **TEST 4: Ver Mis Mascotas (Cliente)** ✅

**Endpoint:** `GET /api/mascotas/mias`  
**Authorization:** Bearer `{{tokenCliente}}`

**Response esperado:**
```json
[
  {
    "idMascota": 1,
    "nombre": "Firulais",
    "especie": "Perro",
    "raza": "Labrador",
    "edad": 3,
    "peso": 5.80,
    "color": "Marrón",
    "sexo": "Macho",
    "estado": "Activo"
  },
  {
    "idMascota": 3,
    "nombre": "Luna",
    "especie": "Perro",
    "raza": "Golden Retriever",
    "edad": 2,
    "peso": 25.50,
    "sexo": "Hembra"
  }
]
```

**✅ Verificación:**
- Solo mascotas del cliente autenticado (ID_Cliente = 1)
- Si usas otro cliente, debe mostrar otras mascotas

---

### **TEST 5: Agendar Cita (Cliente)** ✅

**Endpoint:** `POST /api/citas`  
**Authorization:** Bearer `{{tokenCliente}}`

**Request Body:**
```json
{
  "fechaCita": "2025-01-25",
  "horaCita": "10:00:00",
  "duracionMinutos": 30,
  "motivo": "Control de vacunas",
  "idMascota": 1,
  "idVeterinario": 1
}
```

**Response esperado:**
```json
{
  "id": 28,
  "fechaCita": "2025-01-25",
  "horaCita": "10:00:00",
  "duracionMinutos": 30,
  "motivo": "Control de vacunas",
  "estadoCita": "Programada",
  "idMascota": 1,
  "idVeterinario": 1,
  "fechaActualizacion": "2025-01-20T20:30:00"
}
```

**✅ Verificación:**
- Status: `200 OK`
- `estadoCita` es "Programada"
- `fechaActualizacion` se crea automáticamente
- `id` es asignado por la BD

---

### **TEST 6: Login como Veterinario** ✅

**Endpoint:** `POST /api/auth/login`

**Request Body:**
```json
{
  "email": "ana.vet@clinicaveterinaria.com",
  "password": "vet123"
}
```

**✅ Guardar token como `tokenVeterinario`**

---

### **TEST 7: Ver Citas de Hoy (Veterinario)** ✅

**Endpoint:** `GET /api/citas/hoy`  
**Authorization:** Bearer `{{tokenVeterinario}}`

**Response esperado:**
```json
[
  {
    "id": 20,
    "fechaCita": "2025-01-20",
    "horaCita": "09:30:00",
    "duracionMinutos": 30,
    "motivo": "Control de peso",
    "estadoCita": "Completada",
    "idMascota": 7,
    "idVeterinario": 3
  },
  {
    "id": 21,
    "fechaCita": "2025-01-20",
    "horaCita": "15:00:00",
    "motivo": "Desparasitación",
    "estadoCita": "Programada",
    "idMascota": 13,
    "idVeterinario": 3
  }
]
```

**✅ Verificación:**
- Solo citas donde `Fecha_Cita = HOY`
- Solo citas del veterinario autenticado (`ID_Veterinario = 3`)

---

### **TEST 8: Actualizar Estado de Cita (Veterinario)** ✅

**Endpoint:** `PUT /api/citas/21/estado`  
**Authorization:** Bearer `{{tokenVeterinario}}`

**Request Body:**
```json
{
  "nuevoEstado": "En curso"
}
```

**Response esperado:**
```json
{
  "id": 21,
  "estadoCita": "En curso",
  "fechaActualizacion": "2025-01-20T20:45:00"
}
```

**Estados permitidos:**
- `Programada`
- `En curso`
- `Completada`
- `Cancelada`

---

### **TEST 9: Ver Historia Clínica Completa (Cliente)** ✅

**Endpoint:** `GET /api/historias/mascota/1/completo`  
**Authorization:** Bearer `{{tokenCliente}}`

**Response esperado:**
```json
{
  "historia": {
    "idHistoria": 1,
    "fechaCreacion": "2025-10-05",
    "idMascota": 1
  },
  "entradas": [
    {
      "idEntrada": 1,
      "fechaEntrada": "2025-10-05",
      "descripcion": "Control de rutina. Mascota en buen estado general.",
      "pesoActual": 25.50,
      "temperatura": 38.30,
      "frecuenciaCardiaca": 120
    }
  ],
  "mascotaId": 1,
  "totalEntradas": 1
}
```

**✅ Verificación:**
- Cliente solo puede ver historias de SUS mascotas
- Si intenta ver mascota de otro cliente → `403 Forbidden`

---

### **TEST 10: Agregar Entrada Médica (Veterinario)** ✅

**Endpoint:** `POST /api/historias/1/entrada`  
**Authorization:** Bearer `{{tokenVeterinario}}`

**Request Body:**
```json
{
  "fechaEntrada": "2025-01-20",
  "descripcion": "Control post-vacunación. Sin reacciones adversas.",
  "observaciones": "Continuar con calendario de vacunación.",
  "pesoActual": 26.0,
  "temperatura": 38.5,
  "frecuenciaCardiaca": 115
}
```

**Response esperado:**
```json
{
  "idEntrada": 14,
  "fechaEntrada": "2025-01-20",
  "descripcion": "Control post-vacunación. Sin reacciones adversas.",
  "observaciones": "Continuar con calendario de vacunación.",
  "pesoActual": 26.0,
  "temperatura": 38.5,
  "frecuenciaCardiaca": 115,
  "idHistoria": 1,
  "idVeterinario": 3
}
```

---

### **TEST 11: Cancelar Cita (Cliente)** 🆕

**Endpoint:** `PUT /api/citas/28/cancelar`  
**Authorization:** Bearer `{{tokenCliente}}`

**Request Body:**
```json
{
  "motivo": "Cliente no puede asistir por enfermedad"
}
```

**Response esperado:**
```json
{
  "id": 28,
  "estadoCita": "Cancelada",
  "motivoCancelacion": "Cliente no puede asistir por enfermedad",
  "fechaActualizacion": "2025-01-20T21:00:00"
}
```

**✅ Verificación:**
- Solo citas en estado "Programada" pueden cancelarse
- Cliente solo puede cancelar SUS citas

---

### **TEST 12: Listar Todas las Citas (Admin)** ✅

**Endpoint:** `GET /api/citas`  
**Authorization:** Bearer `{{token}}` (Admin)

**Response esperado:**
```json
[
  {
    "id": 1,
    "fechaCita": "2025-08-15",
    "horaCita": "10:30:00",
    "motivo": "Vacunación",
    "estadoCita": "Programada",
    "idMascota": 1,
    "idVeterinario": 2
  },
  // ... más citas
]
```

**✅ Admin ve TODAS las citas del sistema**

---

## 🌐 TESTS EN FRONTEND

### **PREPARACIÓN:**
1. Abrir navegador en modo incógnito (para limpiar localStorage)
2. Abrir DevTools → Console y Network tabs
3. Ir a: `http://localhost:5173`

---

### **TEST F1: Login como Cliente** ✅

**Pasos:**
1. Ir a página de Login
2. Ingresar:
   - Email: `lucia.cliente@clinicaveterinaria.com`
   - Password: `cliente123`
3. Click en "Iniciar Sesión"

**Verificación:**
- ✅ Redirige a Dashboard de Cliente
- ✅ En DevTools → Application → Local Storage:
  - `token` está guardado
  - `userEmail` = "lucia.cliente@clinicaveterinaria.com"
  - `userRole` = "CLIENTE"
- ✅ Menú muestra opciones de cliente (Mis Mascotas, Agendar Cita)

---

### **TEST F2: Ver Mis Mascotas** ✅

**Pasos:**
1. Estando logueado como cliente
2. Click en "Mis Mascotas" en el menú
3. Debe mostrar lista de mascotas

**Verificación:**
- ✅ Muestra mascotas: Firulais, Luna, Max
- ✅ Cada tarjeta muestra: nombre, especie, raza, peso
- ✅ En Network tab: `GET /api/mascotas/mias` retorna 200 OK

---

### **TEST F3: Agendar Nueva Cita** ✅

**Pasos:**
1. Click en "Agendar Cita"
2. Seleccionar:
   - Mascota: Firulais
   - Veterinario: (debe mostrar lista)
   - Fecha: Mañana
   - Hora: 10:00
   - Motivo: "Revisión de vacunas"
3. Click "Agendar"

**Verificación:**
- ✅ Select de veterinarios muestra 3 opciones
- ✅ No permite seleccionar fechas pasadas
- ✅ Tras agendar, muestra mensaje de éxito
- ✅ En Network: `POST /api/citas` retorna 200 OK
- ✅ Redirige a "Mis Citas"

---

### **TEST F4: Ver Mis Citas** ✅

**Pasos:**
1. Click en "Mis Citas" en el menú

**Verificación:**
- ✅ Muestra lista de citas del cliente
- ✅ Cada cita muestra:
  - Fecha, hora, motivo
  - Nombre de mascota
  - Estado (badge con color)
- ✅ Puede filtrar por estado
- ✅ Citas futuras tienen botón "Cancelar"

---

### **TEST F5: Cancelar Cita** ✅

**Pasos:**
1. En "Mis Citas", encontrar una cita "Programada"
2. Click en "Cancelar"
3. En modal, ingresar motivo: "No puedo asistir"
4. Confirmar cancelación

**Verificación:**
- ✅ Modal solicita motivo (obligatorio)
- ✅ Tras cancelar, estado cambia a "Cancelada"
- ✅ Badge cambia de color
- ✅ Botón "Cancelar" desaparece
- ✅ En Network: `PUT /api/citas/X/cancelar` retorna 200 OK

---

### **TEST F6: Ver Historia Clínica de Mascota** ✅

**Pasos:**
1. En "Mis Mascotas", click en una mascota
2. Click en "Ver Historia Clínica"

**Verificación:**
- ✅ Muestra información de la mascota
- ✅ Lista de entradas médicas ordenadas por fecha DESC
- ✅ Cada entrada muestra:
  - Fecha
  - Descripción
  - Signos vitales (peso, temperatura, FC)
  - Observaciones
- ✅ En Network: `GET /api/historias/mascota/1/completo` retorna 200 OK

---

### **TEST F7: Logout** ✅

**Pasos:**
1. Click en botón de usuario (esquina superior derecha)
2. Click en "Cerrar Sesión"

**Verificación:**
- ✅ Redirige a página de Login
- ✅ localStorage se limpia (token eliminado)
- ✅ Si intentas ir a ruta protegida, redirige a Login

---

### **TEST F8: Login como Veterinario** ✅

**Pasos:**
1. Login con:
   - Email: `ana.vet@clinicaveterinaria.com`
   - Password: `vet123`

**Verificación:**
- ✅ Redirige a Dashboard de Veterinario
- ✅ Menú muestra opciones: Citas de Hoy, Todas las Citas, Historias
- ✅ userRole = "VETERINARIO" en localStorage

---

### **TEST F9: Ver Citas de Hoy (Veterinario)** ✅

**Pasos:**
1. Estando logueado como veterinario
2. Click en "Citas de Hoy"

**Verificación:**
- ✅ Muestra solo citas de HOY del veterinario actual
- ✅ Cada cita muestra:
  - Hora, mascota, dueño
  - Estado actual
  - Botones para cambiar estado
- ✅ En Network: `GET /api/citas/hoy` retorna 200 OK

---

### **TEST F10: Cambiar Estado de Cita (Veterinario)** ✅

**Pasos:**
1. En "Citas de Hoy", seleccionar una cita "Programada"
2. Click en "Iniciar Consulta"
3. Estado cambia a "En curso"
4. Click en "Completar"
5. Estado cambia a "Completada"

**Verificación:**
- ✅ Estados cambian en tiempo real
- ✅ Badge cambia de color según estado
- ✅ Botones se habilitan/deshabilitan según estado
- ✅ En Network: `PUT /api/citas/X/estado` retorna 200 OK

---

### **TEST F11: Agregar Entrada a Historia Clínica (Veterinario)** ✅

**Pasos:**
1. En cita "Completada", click en "Agregar a Historia"
2. Modal con formulario:
   - Descripción: "Control post-operatorio satisfactorio"
   - Observaciones: "Cicatrización correcta"
   - Peso: 26.5
   - Temperatura: 38.3
   - FC: 110
3. Click "Guardar"

**Verificación:**
- ✅ Modal se cierra tras guardar
- ✅ Mensaje de éxito
- ✅ En Network: `POST /api/historias/X/entrada` retorna 200 OK
- ✅ Si vas a historia de la mascota, aparece la nueva entrada

---

### **TEST F12: Login como Admin** ✅

**Pasos:**
1. Login con:
   - Email: `admin@clinicaveterinaria.com`
   - Password: `admin123`

**Verificación:**
- ✅ Redirige a Dashboard de Admin
- ✅ Menú muestra: Usuarios, Métricas, Reportes
- ✅ userRole = "ADMIN" en localStorage

---

## 🐛 TESTS DE ERRORES

### **TEST E1: Login con credenciales incorrectas** ❌

**Request:**
```json
{
  "email": "usuario@inexistente.com",
  "password": "incorrecta"
}
```

**Response esperado:**
- Status: `401 Unauthorized`
- Mensaje de error

---

### **TEST E2: Acceder sin token** ❌

**Request:** `GET /api/mascotas/mias` (sin header Authorization)

**Response esperado:**
- Status: `403 Forbidden` o `401 Unauthorized`

---

### **TEST E3: Cliente intenta ver mascotas de otro** ❌

