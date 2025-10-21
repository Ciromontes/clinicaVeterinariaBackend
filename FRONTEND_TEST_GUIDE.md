# 🎨 GUÍA DE PRUEBAS DEL FRONTEND

## 🚀 PASO A PASO PARA PROBAR EL FRONTEND

### 1️⃣ Iniciar el Frontend (Vite + React)

```bash
cd D:\CopiaF\AnalisisYDesarrolloDeSoftware\2025sena\ProyectoFinalClinVet\gestion-citas\frontend-gestion-citas
npm run dev
```

El frontend debería estar corriendo en: `http://localhost:5173`

---

## 🔐 PRUEBAS DE AUTENTICACIÓN

### Test 1: Login como ADMIN
1. Abre `http://localhost:5173` en el navegador
2. Ingresa credenciales:
   - **Email:** `admin@clinicaveterinaria.com`
   - **Password:** `admin123`
3. Click en "Iniciar Sesión"
4. **Resultado esperado:** Dashboard de Administrador con opciones de gestión

### Test 2: Login como VETERINARIO
1. Ingresa credenciales:
   - **Email:** `ana.vet@clinicaveterinaria.com`
   - **Password:** `vet123`
2. **Resultado esperado:** Dashboard Veterinario con:
   - Lista de citas de hoy
   - Opción para agregar entradas a historias clínicas
   - Ver tratamientos

### Test 3: Login como CLIENTE
1. Ingresa credenciales:
   - **Email:** `lucia.cliente@clinicaveterinaria.com`
   - **Password:** `cliente123`
2. **Resultado esperado:** Dashboard Cliente con:
   - Lista de sus mascotas
   - Historial clínico de cada mascota
   - Opción para agendar citas

### Test 4: Login con credenciales incorrectas
1. Ingresa credenciales inválidas
2. **Resultado esperado:** Mensaje de error "Credenciales inválidas"

---

## 🐾 PRUEBAS DEL MÓDULO DE MASCOTAS

### Test 5: Ver lista de mascotas (Cliente)
1. Login como cliente: `lucia.cliente@clinicaveterinaria.com`
2. Deberías ver solo TUS mascotas (Firulais, Luna, Max)
3. **Verificar:** No puedes ver mascotas de otros clientes

### Test 6: Ver detalles de mascota
1. Click en una mascota de la lista
2. **Resultado esperado:** Modal con:
   - Datos completos de la mascota
   - Historial clínico resumido
   - Botón para ver historial completo

---

## 📅 PRUEBAS DEL MÓDULO DE CITAS

### Test 7: Agendar nueva cita (Cliente)
1. Login como cliente
2. Click en "Agendar Cita"
3. Llena el formulario:
   - Selecciona mascota
   - Fecha futura
   - Hora disponible
   - Motivo de la consulta
   - Veterinario preferido
4. **Resultado esperado:** Cita creada exitosamente

### Test 8: Ver citas de hoy (Veterinario)
1. Login como veterinario: `ana.vet@clinicaveterinaria.com`
2. En el dashboard, deberías ver:
   - Tabla con citas programadas para HOY
   - Información de mascota, cliente, hora
3. Click en "Atender" o "Completar"
4. **Resultado esperado:** Estado de cita actualizado

### Test 9: Cancelar cita
1. Como cliente, ve a "Mis Citas"
2. Selecciona una cita programada
3. Click en "Cancelar"
4. Ingresa motivo de cancelación
5. **Resultado esperado:** Cita cancelada, estado actualizado

---

## 📋 PRUEBAS DEL MÓDULO DE HISTORIA CLÍNICA

### Test 10: Ver historial completo de mascota (Cliente)
1. Login como cliente propietario
2. Selecciona una de tus mascotas
3. Click en "Ver Historial Clínico"
4. **Resultado esperado:**
   - Lista cronológica de entradas
   - Fecha, veterinario, diagnóstico
   - Tratamientos asociados
   - Peso, temperatura, frecuencia cardíaca

### Test 11: Agregar entrada a historia (Veterinario)
1. Login como veterinario
2. Navega a "Historias Clínicas" o desde una cita completada
3. Click en "Agregar Entrada"
4. Llena el formulario:
   - Descripción del estado actual
   - Observaciones
   - Signos vitales (peso, temperatura, FC)
   - Diagnóstico (opcional)
5. **Resultado esperado:** Entrada agregada al historial

### Test 12: Intentar agregar entrada como Cliente
1. Login como cliente
2. **Resultado esperado:** No debe existir opción para agregar entradas
3. Solo puede VER el historial, no MODIFICAR

---

## 💊 PRUEBAS DEL MÓDULO DE TRATAMIENTOS

### Test 13: Ver tratamientos de mascota (Cliente)
1. Login como cliente
2. Ve al historial de tu mascota
3. Busca entradas que tengan tratamientos asociados
4. **Resultado esperado:** Ver diagnóstico y tratamiento aplicado

### Test 14: Crear tratamiento desde cita (Veterinario)
1. Login como veterinario
2. Completa una cita
3. En el formulario, agrega:
   - Diagnóstico
   - Tratamiento aplicado
   - Notas adicionales
4. **Resultado esperado:** Tratamiento vinculado a la cita

---

## 👥 PRUEBAS DEL MÓDULO DE ADMINISTRACIÓN (ADMIN)

### Test 15: Listar todos los usuarios
1. Login como admin
2. Navega a "Gestión de Usuarios"
3. **Resultado esperado:** Tabla con todos los usuarios (clientes, veterinarios, admins)

### Test 16: Desactivar/Activar usuario
1. Como admin, selecciona un usuario
2. Click en "Desactivar"
3. **Resultado esperado:** 
   - Usuario marcado como inactivo
   - No puede hacer login
4. Reactiva el usuario
5. **Resultado esperado:** Usuario puede volver a hacer login

### Test 17: Ver estadísticas del sistema
1. Como admin, ve al dashboard
2. **Resultado esperado:** Visualizar:
   - Total de clientes registrados
   - Total de mascotas
   - Citas programadas hoy
   - Citas completadas este mes

---

## 🔍 PRUEBAS DE BÚSQUEDA Y FILTROS

### Test 18: Buscar mascota por nombre
1. Login como veterinario o admin
2. Usa el buscador en "Mascotas"
3. Escribe "Firu"
4. **Resultado esperado:** Muestra "Firulais"

### Test 19: Filtrar citas por estado
1. Ve a "Gestión de Citas"
2. Selecciona filtro "Programadas"
3. **Resultado esperado:** Solo citas con estado "Programada"

### Test 20: Filtrar por fecha
1. Selecciona rango de fechas
2. **Resultado esperado:** Citas dentro del rango seleccionado

---

## 🎨 PRUEBAS DE INTERFAZ Y UX

### Test 21: Responsive Design
1. Abre el frontend en diferentes dispositivos:
   - Desktop (1920x1080)
   - Tablet (768px)
   - Mobile (375px)
2. **Resultado esperado:** Interfaz adaptable, sin elementos rotos

### Test 22: Navegación entre vistas
1. Prueba todos los enlaces del menú
2. Usa botones "Volver" o "Cancelar"
3. **Resultado esperado:** Navegación fluida sin errores 404

### Test 23: Carga de datos
1. Verifica que las tablas muestren "Cargando..."
2. Cuando no hay datos: "No se encontraron resultados"
3. **Resultado esperado:** Feedback visual claro

---

## 🐛 PRUEBAS DE ERRORES Y VALIDACIONES

### Test 24: Validación de formularios
1. Intenta crear una cita sin llenar campos obligatorios
2. **Resultado esperado:** Mensajes de error específicos

### Test 25: Manejo de errores de servidor
1. Apaga el backend (`docker-compose down`)
2. Intenta hacer login en el frontend
3. **Resultado esperado:** Mensaje "Error de conexión con el servidor"

### Test 26: Token expirado
1. Espera 24 horas (o modifica JWT expiration)
2. Intenta hacer una petición
3. **Resultado esperado:** Redirigir al login con mensaje "Sesión expirada"

---

## ✅ CHECKLIST COMPLETO

### Autenticación ✅
- [ ] Login ADMIN
- [ ] Login VETERINARIO
- [ ] Login CLIENTE
- [ ] Logout
- [ ] Credenciales incorrectas
- [ ] Token expirado

### Mascotas ✅
- [ ] Ver lista (según rol)
- [ ] Ver detalles
- [ ] Buscar mascota
- [ ] Cliente solo ve SUS mascotas

### Citas ✅
- [ ] Agendar cita (cliente)
- [ ] Ver citas de hoy (veterinario)
- [ ] Completar cita (veterinario)
- [ ] Cancelar cita
- [ ] Filtrar por estado/fecha

### Historia Clínica ✅
- [ ] Ver historial (cliente propietario)
- [ ] Ver historial (veterinario)
- [ ] Agregar entrada (veterinario)
- [ ] Cliente NO puede modificar

### Tratamientos ✅
- [ ] Ver tratamientos asociados
- [ ] Crear desde cita
- [ ] Editar tratamiento

### Administración (ADMIN) ✅
- [ ] Listar usuarios
- [ ] Activar/Desactivar usuarios
- [ ] Ver estadísticas
- [ ] Gestionar roles

### UI/UX ✅
- [ ] Responsive design
- [ ] Navegación fluida
- [ ] Mensajes de error claros
- [ ] Feedback visual (loading, success, error)

---

## 🚨 PROBLEMAS CONOCIDOS Y SOLUCIONES

### Problema: "CORS Error"
**Solución:** Verifica que el backend tenga configurado:
```java
configuration.setAllowedOrigins(Arrays.asList("http://localhost:5173"));
```

### Problema: "Token no se envía en las peticiones"
**Solución:** Verifica en DevTools > Network > Headers que incluya:
```
Authorization: Bearer {token}
```

### Problema: "Página en blanco después del login"
**Solución:** Verifica en consola del navegador (F12) si hay errores de JavaScript.

---

## 📊 PRUEBAS DE RENDIMIENTO

### Test 27: Carga de listas grandes
1. Inserta 100+ mascotas en la BD
2. Ve a la lista de mascotas
3. **Resultado esperado:** Paginación o scroll infinito

### Test 28: Múltiples usuarios simultáneos
1. Abre 3 navegadores diferentes
2. Login con 3 usuarios distintos
3. **Resultado esperado:** Sin conflictos de sesión

---

## 🎯 SIGUIENTES PASOS DESPUÉS DE LAS PRUEBAS

1. ✅ Verificar que TODAS las pruebas pasen
2. 📝 Documentar bugs encontrados
3. 🐛 Corregir errores críticos
4. 🚀 Hacer commit y push a Git
5. 🐳 Dockerizar frontend
6. ☁️ Desplegar en Azure

**NOTA:** Si encuentras algún error, anótalo con:
- Descripción del error
- Pasos para reproducirlo
- Resultado esperado vs actual
- Capturas de pantalla (opcional)

