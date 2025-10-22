# 🧪 VERIFICACIÓN DE PERMISOS CON POSTMAN

## 🎯 OBJETIVO
Verificar que los últimos cambios de permisos funcionan correctamente:

### ✅ Cambios Implementados:
1. **CITAS**: 
   - ✅ ADMIN: Ver todas las citas, crear, pero NO actualizar estado
   - ✅ VETERINARIO: Ver todas las citas (coordinación), crear, actualizar solo SUS citas
   - ✅ CLIENTE: Ver solo sus citas, crear (agendar)

2. **HISTORIAS CLÍNICAS**:
   - ✅ ADMIN: Ver TODAS las historias (supervisión, reportes)
   - ✅ VETERINARIO: Ver TODAS las historias, agregar entradas
   - ✅ CLIENTE: Ver solo historias de SUS mascotas

---

## 📋 PASO 1: PREPARAR DATOS DE PRUEBA

### Ejecutar este script SQL para crear datos de prueba:

```sql
-- 1. Crear una cita de HOY para la Dra. Ana (ID_Veterinario = 3)
INSERT INTO cita (Fecha_Cita, Hora_Cita, Duracion_Minutos, Motivo, Estado_Cita, ID_Mascota, ID_Veterinario)
VALUES (CURDATE(), '09:00:00', 30, 'Vacunación - Prueba Dra. Ana', 'Programada', 1, 3);

-- 2. Crear una cita de HOY para Dr. Juan (ID_Veterinario = 1)
INSERT INTO cita (Fecha_Cita, Hora_Cita, Duracion_Minutos, Motivo, Estado_Cita, ID_Mascota, ID_Veterinario)
VALUES (CURDATE(), '10:00:00', 30, 'Consulta - Prueba Dr. Juan', 'Programada', 2, 1);

-- 3. Crear una cita futura para la Dra. Ana
INSERT INTO cita (Fecha_Cita, Hora_Cita, Duracion_Minutos, Motivo, Estado_Cita, ID_Mascota, ID_Veterinario)
VALUES (DATE_ADD(CURDATE(), INTERVAL 2 DAY), '14:00:00', 30, 'Control futuro', 'Programada', 1, 3);

-- 4. Verificar los IDs creados
SELECT ID_Cita, Fecha_Cita, Hora_Cita, Motivo, Estado_Cita, 
       ID_Veterinario, 
       (SELECT Nombre FROM veterinario WHERE ID_Veterinario = cita.ID_Veterinario) as Veterinario
FROM cita 
WHERE Fecha_Cita >= CURDATE()
ORDER BY Fecha_Cita, Hora_Cita;
```

**📝 IMPORTANTE:** Anota los ID_Cita que se generaron, los usarás en las pruebas.

---

## 🔐 PASO 2: PRUEBAS COMO ADMIN

### ✅ TEST 1: Login como ADMIN
```http
POST http://localhost:8080/api/auth/login
Content-Type: application/json

{
  "email": "admin@clinicaveterinaria.com",
  "password": "admin123"
}
```

**✅ Resultado Esperado:** 
- Status 200 OK
- Recibes un token JWT
- Rol: "ADMIN"

**📋 Acción:** Copia el token y configúralo en Postman (Authorization → Bearer Token)

---

### ✅ TEST 2: Ver TODAS las citas (Supervisión)
```http
GET http://localhost:8080/api/citas
Authorization: Bearer {TOKEN_ADMIN}
```

**✅ Resultado Esperado:** 
- Status 200 OK
- Lista con TODAS las citas del sistema
- Incluye citas de TODOS los veterinarios

**🔍 Verificar:**
- ¿Ves citas de la Dra. Ana (ID_Veterinario = 3)?
- ¿Ves citas del Dr. Juan (ID_Veterinario = 1)?
- ¿Ves citas de otros veterinarios?

---

### ✅ TEST 3: Ver TODAS las citas de hoy
```http
GET http://localhost:8080/api/citas/hoy
Authorization: Bearer {TOKEN_ADMIN}
```

**✅ Resultado Esperado:** 
- Status 200 OK
- Lista con TODAS las citas de HOY
- Incluye las citas de la Dra. Ana Y del Dr. Juan creadas en el Paso 1

**🔍 Verificar:**
- ¿Aparece "Vacunación - Prueba Dra. Ana"?
- ¿Aparece "Consulta - Prueba Dr. Juan"?

---

### ✅ TEST 4: Ver historial de cualquier mascota
```http
GET http://localhost:8080/api/historias/mascota/1/completo
Authorization: Bearer {TOKEN_ADMIN}
```

**✅ Resultado Esperado:** 
- Status 200 OK
- Historial completo de la mascota
- ADMIN puede ver historias de cualquier mascota (supervisión)

---

### ❌ TEST 5: Intentar actualizar estado de cita (DEBE FALLAR)
```http
PUT http://localhost:8080/api/citas/[ID_CITA]/estado
Authorization: Bearer {TOKEN_ADMIN}
Content-Type: application/json

{
  "estado": "Completada"
}
```

**❌ Resultado Esperado:** 
- **Status 403 Forbidden** (Acceso denegado)
- Mensaje: "Solo el veterinario asignado puede actualizar el estado de la cita"

**✅ ESTO ES CORRECTO:** El ADMIN NO debe poder actualizar estados, es responsabilidad del veterinario.

---

## 👨‍⚕️ PASO 3: PRUEBAS COMO VETERINARIO (Dra. Ana)

### ✅ TEST 6: Login como VETERINARIO
```http
POST http://localhost:8080/api/auth/login
Content-Type: application/json

{
  "email": "ana.vet@clinicaveterinaria.com",
  "password": "vet123"
}
```

**✅ Resultado Esperado:** 
- Status 200 OK
- Token JWT
- Rol: "VETERINARIO"
- ID_Veterinario: 3

**📋 Acción:** Copia el nuevo token (diferente al de ADMIN)

---

### ✅ TEST 7: Ver TODAS las citas (Coordinación)
```http
GET http://localhost:8080/api/citas
Authorization: Bearer {TOKEN_VETERINARIO}
```

**✅ Resultado Esperado:** 
- Status 200 OK
- Lista con TODAS las citas del sistema
- Incluye citas de TODOS los veterinarios

**🔍 Verificar:**
- ¿Ves citas de otros veterinarios además de las tuyas?
- Esto permite coordinación entre veterinarios

---

### ✅ TEST 8: Ver solo MIS citas de hoy (Agenda personal)
```http
GET http://localhost:8080/api/citas/hoy
Authorization: Bearer {TOKEN_VETERINARIO}
```

**✅ Resultado Esperado:** 
- Status 200 OK
- Lista con SOLO las citas de hoy de la Dra. Ana (ID_Veterinario = 3)
- **NO aparece** "Consulta - Prueba Dr. Juan"
- **SÍ aparece** "Vacunación - Prueba Dra. Ana"

**🔍 Verificar:**
- ¿Solo ves citas asignadas a la Dra. Ana?
- ¿NO ves citas del Dr. Juan?

---

### ✅ TEST 9: Ver historial de cualquier mascota
```http
GET http://localhost:8080/api/historias/mascota/1/completo
Authorization: Bearer {TOKEN_VETERINARIO}
```

**✅ Resultado Esperado:** 
- Status 200 OK
- Historial completo de cualquier mascota
- VETERINARIO puede ver todas las historias (atención médica)

---

### ✅ TEST 10: Actualizar estado de MI cita
```http
PUT http://localhost:8080/api/citas/[ID_CITA_DRA_ANA]/estado
Authorization: Bearer {TOKEN_VETERINARIO}
Content-Type: application/json

{
  "estado": "Completada"
}
```

**📝 Nota:** Usa el ID de la cita "Vacunación - Prueba Dra. Ana" (ID_Veterinario = 3)

**✅ Resultado Esperado:** 
- Status 200 OK
- Cita actualizada exitosamente
- El veterinario SÍ puede actualizar SUS propias citas

---

### ❌ TEST 11: Intentar actualizar cita de OTRO veterinario (DEBE FALLAR)
```http
PUT http://localhost:8080/api/citas/[ID_CITA_DR_JUAN]/estado
Authorization: Bearer {TOKEN_VETERINARIO}
Content-Type: application/json

{
  "estado": "Completada"
}
```

**📝 Nota:** Usa el ID de la cita "Consulta - Prueba Dr. Juan" (ID_Veterinario = 1)

**❌ Resultado Esperado:** 
- **Status 403 Forbidden**
- Mensaje: "No puede modificar citas de otro veterinario"

**✅ ESTO ES CORRECTO:** Un veterinario NO puede actualizar citas de otros.

---

### ✅ TEST 12: Agregar entrada a historia clínica
```http
POST http://localhost:8080/api/historias/1/entrada
Authorization: Bearer {TOKEN_VETERINARIO}
Content-Type: application/json

{
  "descripcion": "Control rutinario. Mascota en excelente estado de salud.",
  "observaciones": "Continuar con alimentación actual. Próximo control en 6 meses.",
  "pesoActual": 26.5,
  "temperatura": 38.2,
  "frecuenciaCardiaca": 120
}
```

**✅ Resultado Esperado:** 
- Status 200 OK
- Entrada agregada exitosamente
- VETERINARIO puede agregar entradas a historias clínicas

---

## 👤 PASO 4: PRUEBAS COMO CLIENTE

### ✅ TEST 13: Login como CLIENTE
```http
POST http://localhost:8080/api/auth/login
Content-Type: application/json

{
  "email": "lucia.cliente@clinicaveterinaria.com",
  "password": "cliente123"
}
```

**✅ Resultado Esperado:** 
- Status 200 OK
- Token JWT
- Rol: "CLIENTE"

**📋 Acción:** Copia el token de cliente

---

### ✅ TEST 14: Ver solo MIS mascotas
```http
GET http://localhost:8080/api/mascotas/mias
Authorization: Bearer {TOKEN_CLIENTE}
```

**✅ Resultado Esperado:** 
- Status 200 OK
- Lista con solo las mascotas de Lucía
- NO ve mascotas de otros clientes

---

### ✅ TEST 15: Ver historial de MI mascota
```http
GET http://localhost:8080/api/historias/mascota/[ID_MASCOTA_DE_LUCIA]/completo
Authorization: Bearer {TOKEN_CLIENTE}
```

**✅ Resultado Esperado:** 
- Status 200 OK (si la mascota le pertenece)
- Historial completo de su mascota

---

### ❌ TEST 16: Intentar ver historial de mascota de OTRO cliente (DEBE FALLAR)
```http
GET http://localhost:8080/api/historias/mascota/[ID_MASCOTA_DE_OTRO_CLIENTE]/completo
Authorization: Bearer {TOKEN_CLIENTE}
```

**❌ Resultado Esperado:** 
- **Status 403 Forbidden**
- Mensaje: "No tiene permiso para ver esta historia clínica"

---

### ❌ TEST 17: Intentar ver TODAS las citas (DEBE FALLAR)
```http
GET http://localhost:8080/api/citas
Authorization: Bearer {TOKEN_CLIENTE}
```

**❌ Resultado Esperado:** 
- **Status 403 Forbidden**
- El cliente NO debe poder ver todas las citas del sistema

---

### ❌ TEST 18: Intentar agregar entrada a historia clínica (DEBE FALLAR)
```http
POST http://localhost:8080/api/historias/1/entrada
Authorization: Bearer {TOKEN_CLIENTE}
Content-Type: application/json

{
  "descripcion": "Intento de cliente",
  "pesoActual": 25.0
}
```

**❌ Resultado Esperado:** 
- **Status 403 Forbidden**
- Solo VETERINARIO y ADMIN pueden agregar entradas médicas

---

## 📊 RESUMEN DE VERIFICACIÓN

### ✅ Permisos CORRECTOS verificados:

#### ADMIN:
- ✅ Ve TODAS las citas (coordinación/supervisión)
- ✅ Ve TODAS las citas de hoy
- ✅ Ve TODAS las historias clínicas
- ❌ NO puede actualizar estados de citas (responsabilidad del veterinario)

#### VETERINARIO:
- ✅ Ve TODAS las citas (coordinación)
- ✅ Ve solo SUS citas de hoy (agenda personal)
- ✅ Actualiza solo SUS citas
- ❌ NO puede actualizar citas de otros veterinarios
- ✅ Ve TODAS las historias clínicas (atención médica)
- ✅ Agrega entradas a historias clínicas

#### CLIENTE:
- ✅ Ve solo SUS mascotas
- ✅ Ve solo historias de SUS mascotas
- ❌ NO ve todas las citas del sistema
- ❌ NO puede agregar entradas médicas

---

## 🎯 CHECKLIST DE VERIFICACIÓN RÁPIDA

Marca cada test conforme lo completes:

### ADMIN (6 tests)
- [ ] TEST 1: Login exitoso ✅
- [ ] TEST 2: Ver todas las citas ✅
- [ ] TEST 3: Ver todas las citas de hoy ✅
- [ ] TEST 4: Ver historias de cualquier mascota ✅
- [ ] TEST 5: NO puede actualizar citas ❌ (correcto)

### VETERINARIO (7 tests)
- [ ] TEST 6: Login exitoso ✅
- [ ] TEST 7: Ver todas las citas ✅
- [ ] TEST 8: Ver solo sus citas de hoy ✅
- [ ] TEST 9: Ver historias de cualquier mascota ✅
- [ ] TEST 10: Actualizar SU cita ✅
- [ ] TEST 11: NO puede actualizar citas de otros ❌ (correcto)
- [ ] TEST 12: Agregar entrada a historia ✅

### CLIENTE (6 tests)
- [ ] TEST 13: Login exitoso ✅
- [ ] TEST 14: Ver solo SUS mascotas ✅
- [ ] TEST 15: Ver historial de SU mascota ✅
- [ ] TEST 16: NO puede ver mascotas de otros ❌ (correcto)
- [ ] TEST 17: NO puede ver todas las citas ❌ (correcto)
- [ ] TEST 18: NO puede agregar entradas médicas ❌ (correcto)

**Total: 18 tests** (12 exitosos ✅ + 6 denegados correctamente ❌)

---

## 🔍 COMANDOS ÚTILES PARA DEBUG

### Ver logs del backend en tiempo real:
```cmd
docker logs clinica-backend -f
```

### Ver solo los endpoints de citas:
```cmd
docker logs clinica-backend 2>&1 | findstr "/api/citas"
```

### Ver errores de autorización:
```cmd
docker logs clinica-backend 2>&1 | findstr "403"
```

---

## ✅ CRITERIOS DE ÉXITO

**Los cambios están funcionando correctamente si:**

1. ✅ ADMIN ve todas las citas pero NO puede actualizar estados
2. ✅ VETERINARIO ve todas las citas para coordinación
3. ✅ VETERINARIO ve solo SUS citas en `/api/citas/hoy`
4. ✅ VETERINARIO solo puede actualizar SUS propias citas
5. ✅ ADMIN y VETERINARIO ven todas las historias clínicas
6. ✅ CLIENTE solo ve sus propias mascotas e historias
7. ❌ Los tests que deben fallar (403 Forbidden) fallan correctamente

---

## 📝 NOTAS IMPORTANTES

- **Usa el token correcto** para cada rol (ADMIN, VETERINARIO, CLIENTE)
- **Los tokens expiran** después de 24 horas
- **Los IDs cambian** según tu base de datos, ajústalos
- **Las fechas de prueba** usan CURDATE() para ser dinámicas
- **Los tests ❌** son tan importantes como los ✅ (seguridad)

---

## 🚀 PRÓXIMO PASO

Una vez que todos los tests pasen correctamente:

1. ✅ Marca todos los checkboxes
2. 📸 Captura pantallas de los resultados clave
3. 🔄 Hacer commit de los cambios verificados
4. 🎉 Los permisos están funcionando correctamente

