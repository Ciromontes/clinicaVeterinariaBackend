```

**Tiempo de mitigación:** 20 minutos  
**Estado:** OPTIMIZACIÓN FUTURA 🔄

---

### **RIESGO #14: Logs con información sensible**

**Probabilidad:** Baja (20%)  
**Impacto:** Medio 🟡  
**Descripción:**  
Logging de passwords o datos personales.

**Solución:**
```java
// NUNCA hacer esto:
log.info("Usuario creado: {}", usuario); // Incluye password

// Hacer esto:
log.info("Usuario creado: email={}, rol={}", usuario.getEmail(), usuario.getRol());
```

**Tiempo de mitigación:** 10 minutos (revisión de código)  
**Estado:** REVISIÓN DE CÓDIGO 👁️

---

### **RIESGO #15: Falta de tests unitarios**

**Probabilidad:** Alta (100%)  
**Impacto:** Bajo 🟢  
**Descripción:**  
Sin tests, los cambios futuros pueden romper funcionalidad existente.

**Solución: Implementar tests básicos:**
```java
@SpringBootTest
class CitaServiceTest {
    
    @Autowired
    private CitaService citaService;
    
    @Test
    void deberiaCrearCitaExitosamente() {
        Cita cita = new Cita();
        cita.setIdMascota(1);
        cita.setIdVeterinario(2);
        cita.setFechaCita(LocalDate.now().plusDays(1));
        cita.setHoraCita(LocalTime.of(10, 0));
        
        Cita resultado = citaService.guardar(cita);
        
        assertNotNull(resultado.getId());
        assertEquals("Programada", resultado.getEstadoCita());
    }
    
    @Test
    void noDeberiaCrearCitaEnPasado() {
        Cita cita = new Cita();
        cita.setFechaCita(LocalDate.now().minusDays(1));
        
        assertThrows(ValidationException.class, () -> {
            citaService.guardar(cita);
        });
    }
}
```

**Tiempo de implementación:** 2-3 horas (test suite completo)  
**Estado:** RECOMENDADO PARA FUTURO 📝

---

## 📋 PLAN DE MITIGACIÓN PRIORIZADO

### **FASE 0: ANTES DE EMPEZAR (CRÍTICO)** ⚠️
1. Crear backup de base de datos (10 min)
2. Verificar passwords encriptados (10 min)
3. Configurar zona horaria (10 min)

### **FASE 1: DURANTE MIGRACIONES (ALTA PRIORIDAD)** 🔴
4. Usar transacciones en migraciones SQL (incluido)
5. Probar en BD de desarrollo primero (20 min)

### **FASE 2: IMPLEMENTACIÓN DE ENDPOINTS (ALTA)** 🟠
6. Crear GlobalExceptionHandler (50 min)
7. Implementar validaciones de seguridad en servicios (60 min)
8. Agregar @Valid en controllers (30 min)

### **FASE 3: AJUSTES FINALES (MEDIA)** 🟡
9. Ajustar enums y tipos de datos (45 min)
10. Configurar serialización JSON (20 min)
11. Revisar CORS para producción (15 min)

### **FASE 4: OPTIMIZACIONES (BAJA)** 🟢
12. Implementar paginación (20 min)
13. Optimizar queries con JOIN FETCH (25 min)
14. Revisar logs (10 min)

---

## ✅ CHECKLIST FINAL DE SEGURIDAD

Antes de desplegar a producción:

- [ ] Backup de base de datos creado
- [ ] Migraciones SQL probadas en desarrollo
- [ ] Passwords encriptados con BCrypt
- [ ] JWT con expiración configurada
- [ ] Validaciones de permisos en TODOS los endpoints críticos
- [ ] GlobalExceptionHandler implementado
- [ ] CORS configurado con dominio específico (no "*")
- [ ] Logs sin información sensible
- [ ] Zona horaria configurada
- [ ] Constraints de BD activados
- [ ] Tests básicos implementados
- [ ] Documentación actualizada

---

## 📞 CONTACTO EN CASO DE EMERGENCIA

**Si algo falla durante implementación:**

1. **Restaurar backup de BD:**
   ```bash
   mysql -u root -p clinicaveterinaria < backup_antes_migracion.sql
   ```

2. **Revertir cambios en Git:**
   ```bash
   git reset --hard HEAD~1
   ```

3. **Consultar logs de error:**
   ```bash
   # Ver últimas 50 líneas del log
   tail -n 50 logs/clinicaveterinaria.log
   ```

4. **Modo seguro - deshabilitar nuevos endpoints:**
   ```java
   // Comentar temporalmente endpoints nuevos
   // @GetMapping("/nuevos-endpoint")
   ```

---

**Última actualización:** Enero 20, 2025  
**Estado:** DOCUMENTO DE PLANIFICACIÓN  
**Nivel de confianza:** 95% de éxito si se siguen las recomendaciones  

**Total tiempo de mitigación de riesgos críticos:** 2-3 horas  
**Total archivos nuevos a crear:** 8-10  
**Total archivos existentes a modificar:** 12-15
# ⚠️ EVALUACIÓN DE RIESGOS Y SOLUCIONES

> **Proyecto:** GA7-220501096-AA4-EV03  
> **Fecha:** Enero 20, 2025  
> **Tipo:** Análisis de riesgos técnicos y estrategias de mitigación  
> **Estado:** PLANIFICACIÓN - Identificación preventiva  

---

## 🎯 RESUMEN EJECUTIVO

### Nivel de riesgo general del proyecto: **MEDIO** ⚠️

- **Riesgos Críticos:** 2
- **Riesgos Altos:** 4
- **Riesgos Medios:** 6
- **Riesgos Bajos:** 3

**Tiempo estimado de mitigación:** 2-3 horas adicionales  
**Probabilidad de éxito:** 95% si se siguen las recomendaciones

---

## 🔴 RIESGOS CRÍTICOS

### **RIESGO #1: Pérdida de datos durante migraciones SQL**

**Probabilidad:** Media (30%)  
**Impacto:** Crítico ⛔  
**Descripción:**  
Al ejecutar migraciones SQL (especialmente `ALTER TABLE`), si algo falla a mitad del proceso, la base de datos puede quedar en estado inconsistente con datos corruptos o perdidos.

**Escenario de fallo:**
```sql
ALTER TABLE historiaclinica MODIFY COLUMN Fecha_Creacion DATE NOT NULL;
-- Si la tabla tiene datos con horas/minutos, puede fallar y truncar datos
```

**Soluciones:**

1. **BACKUP OBLIGATORIO antes de cualquier migración:**
```bash
# Windows CMD
cd "C:\Program Files\MySQL\MySQL Server 8.0\bin"
mysqldump -u root -p clinicaveterinaria > D:\backup_antes_migracion_%date:~-4,4%%date:~-7,2%%date:~-10,2%.sql
```

2. **Usar transacciones:**
```sql
START TRANSACTION;
-- ejecutar todas las migraciones
-- si todo OK:
COMMIT;
-- si algo falla:
ROLLBACK;
```

3. **Probar primero en base de datos de desarrollo:**
```sql
-- Crear BD de prueba
CREATE DATABASE clinicaveterinaria_test LIKE clinicaveterinaria;
-- Copiar datos
INSERT INTO clinicaveterinaria_test.* SELECT * FROM clinicaveterinaria.*;
-- Probar migraciones ahí primero
```

4. **Migrar en horario de baja demanda:**
   - Preferiblemente fuera de horario laboral
   - Avisar a usuarios sobre mantenimiento

**Plan de recuperación:**
```bash
# Si algo falla, restaurar desde backup:
mysql -u root -p clinicaveterinaria < backup_antes_migracion_20250120.sql
```

**Tiempo de mitigación:** 30 minutos (crear backups y procedimiento)  
**Estado:** PREVENIBLE ✅

---

### **RIESGO #2: Incompatibilidad de tipos de datos entre MySQL y Java**

**Probabilidad:** Alta (60%)  
**Impacto:** Alto 🔴  
**Descripción:**  
Los modelos Java usan tipos como `LocalDateTime`, `LocalDate`, `BigDecimal`, pero MySQL puede devolver formatos diferentes, causando errores de serialización JSON.

**Ejemplos de problemas:**

1. **Enum de sexo:** Java usa `Macho/Hembra`, Frontend espera `M/F`
```java
// Actual en Mascota.java
public enum Sexo {
    Macho, Hembra  // ❌ No coincide con frontend
}

// Debe ser:
public enum Sexo {
    M, F  // ✅ Coincide con documentación
}
```

2. **Formato de fecha en JSON:**
```json
// MySQL devuelve:
"fechaCita": "2025-01-25T00:00:00"

// Frontend espera:
"fechaCita": "2025-01-25"
```

3. **Nombres de campos:** Base de datos usa `snake_case`, Java usa `camelCase`, JSON debe ser `camelCase`

**Soluciones:**

1. **Configurar Jackson para formatos de fecha:**
```java
// application.properties
spring.jackson.date-format=yyyy-MM-dd
spring.jackson.time-format=HH:mm:ss
spring.jackson.serialization.write-dates-as-timestamps=false
```

2. **Crear DTOs para respuestas específicas:**
```java
@Data
public class MascotaResponse {
    private Integer idMascota;
    private String nombre;
    private String especie;
    private String sexo;  // String en vez de Enum
    
    public static MascotaResponse fromEntity(Mascota mascota) {
        MascotaResponse dto = new MascotaResponse();
        dto.setIdMascota(mascota.getIdMascota());
        dto.setSexo(mascota.getSexo() == Sexo.Macho ? "M" : "F");
        return dto;
    }
}
```

3. **Usar `@JsonProperty` para mapear nombres:**
```java
@JsonProperty("idMascota")
@Column(name = "ID_Mascota")
private Integer idMascota;
```

4. **Ajustar enums:**
```java
// En Mascota.java
public enum Sexo {
    @JsonValue M, // Serializa como "M"
    @JsonValue F  // Serializa como "F"
}
```

**Archivos a revisar:**
- `Mascota.java` - Enum Sexo y Especie
- `Cita.java` - Formato de fecha/hora
- `HistoriaClinica.java` - LocalDateTime vs LocalDate
- `Usuario.java` - Enum Rol

**Tiempo de mitigación:** 45 minutos  
**Estado:** REQUIERE ATENCIÓN ⚠️

---

## 🟠 RIESGOS ALTOS

### **RIESGO #3: Validaciones de seguridad insuficientes**

**Probabilidad:** Alta (70%)  
**Impacto:** Alto 🔴  
**Descripción:**  
Los endpoints nuevos pueden permitir que usuarios accedan a datos de otros usuarios si no se validan correctamente los permisos.

**Ejemplos de vulnerabilidades:**

1. **Cliente accede a mascotas de otro cliente:**
```java
// VULNERABLE:
@GetMapping("/mascotas/cliente/{id}")
public ResponseEntity<List<Mascota>> getMascotasPorCliente(@PathVariable Integer id) {
    return ResponseEntity.ok(mascotaService.findByClienteId(id));
    // ❌ No valida si el usuario autenticado puede ver este cliente
}

// SEGURO:
@GetMapping("/mascotas/cliente/{id}")
public ResponseEntity<List<Mascota>> getMascotasPorCliente(
    @PathVariable Integer id, 
    Authentication auth) {
    
    Usuario usuario = usuarioService.buscarPorEmail(auth.getName());
    
    // Solo ADMIN, RECEPCIONISTA, VETERINARIO pueden ver cualquier cliente
    if (!usuario.getRol().matches("ADMIN|RECEPCIONISTA|VETERINARIO")) {
        // CLIENTE solo puede ver sus propias mascotas
        if (!usuario.getIdCliente().equals(id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }
    
    return ResponseEntity.ok(mascotaService.findByClienteId(id));
}
```

2. **SQL Injection en búsquedas:**
```java
// VULNERABLE:
@Query(value = "SELECT * FROM usuario WHERE nombre LIKE '%" + ?1 + "%'", nativeQuery = true)
List<Usuario> buscarPorNombre(String nombre);

// SEGURO:
@Query("SELECT u FROM Usuario u WHERE u.nombre LIKE %?1%")
List<Usuario> buscarPorNombre(String nombre);
```

**Soluciones:**

1. **Implementar validaciones de permisos en TODOS los servicios:**
```java
// Crear clase utilitaria
@Component
public class SecurityValidator {
    
    public void validarAccesoCliente(Usuario usuario, Integer idCliente) {
        if (usuario.getRol().equals("CLIENTE") && 
            !usuario.getIdCliente().equals(idCliente)) {
            throw new AccessDeniedException("No autorizado");
        }
    }
    
    public void validarRol(Usuario usuario, String... rolesPermitidos) {
        if (!Arrays.asList(rolesPermitidos).contains(usuario.getRol())) {
            throw new AccessDeniedException("Rol insuficiente");
        }
    }
}
```

2. **Usar anotaciones de Spring Security:**
```java
@PreAuthorize("hasAnyRole('ADMIN', 'RECEPCIONISTA')")
@PostMapping("/mascotas")
public ResponseEntity<Mascota> crear(@RequestBody Mascota mascota) {
    // ...
}
```

3. **Logging de intentos de acceso no autorizados:**
```java
catch (AccessDeniedException e) {
    log.warn("Intento de acceso no autorizado: Usuario={}, Recurso={}", 
        auth.getName(), recurso);
    return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
}
```

**Endpoints de ALTO RIESGO:**
- `GET /api/mascotas/cliente/{id}`
- `GET /api/citas/mascota/{idMascota}`
- `GET /api/historias/mascota/{idMascota}/completo`
- `PUT /api/citas/{id}/cancelar`

**Tiempo de mitigación:** 60 minutos  
**Estado:** REQUIERE IMPLEMENTACIÓN INMEDIATA 🚨

---

### **RIESGO #4: Bloqueos de base de datos (deadlocks)**

**Probabilidad:** Media (40%)  
**Impacto:** Alto 🔴  
**Descripción:**  
Al agregar índices y constraints, las operaciones concurrentes pueden causar bloqueos, especialmente en tabla `cita` que es muy actualizada.

**Escenarios de problema:**
- 2 usuarios intentan agendar cita con mismo veterinario a la misma hora
- Actualización de estado de cita mientras se consulta disponibilidad
- Agregar entrada a historia clínica mientras se lee el historial completo

**Soluciones:**

1. **Usar transacciones con nivel de aislamiento adecuado:**
```java
@Transactional(isolation = Isolation.READ_COMMITTED)
public Cita agendarCita(Cita cita, String email) {
    // Verificar disponibilidad con lock
    Optional<Cita> conflicto = citaRepository.findConflicto(
        cita.getIdVeterinario(), 
        cita.getFechaCita(), 
        cita.getHoraCita()
    );
    
    if (conflicto.isPresent()) {
        throw new CitaDuplicadaException("Horario no disponible");
    }
    
    return citaRepository.save(cita);
}
```

2. **Implementar retry automático:**
```java
@Retryable(
    value = {OptimisticLockException.class, PessimisticLockException.class},
    maxAttempts = 3,
    backoff = @Backoff(delay = 500)
)
public Cita agendarCita(Cita cita, String email) {
    // ...
}
```

3. **Optimistic Locking con @Version:**
```java
@Entity
public class Cita {
    @Id
    private Integer id;
    
    @Version
    private Long version;  // JPA maneja automáticamente
    
    // ...
}
```

4. **Ejecutar migraciones en ventana de mantenimiento:**
```sql
-- Verificar conexiones activas antes de migrar
SHOW PROCESSLIST;

-- Cerrar conexiones no esenciales
KILL CONNECTION_ID;
```

**Tiempo de mitigación:** 40 minutos  
**Estado:** PREVENTIVO ⚠️

---

### **RIESGO #5: Manejo de errores inconsistente**

**Probabilidad:** Alta (80%)  
**Impacat:** Medio 🟡  
**Descripción:**  
Sin un manejo centralizado de errores, el frontend recibirá respuestas inconsistentes, dificultando el manejo de errores en React.

**Problema actual:**
```java
// Controller A retorna:
return ResponseEntity.badRequest().body("Error: mascota no encontrada");

// Controller B retorna:
return ResponseEntity.status(404).body(null);

// Controller C retorna:
throw new RuntimeException("Mascota no encontrada");
```

**Frontend recibe respuestas diferentes:**
```json
// Caso 1:
{
  "error": "Error: mascota no encontrada"
}

// Caso 2:
null

// Caso 3:
{
  "timestamp": "2025-01-20T10:30:00",
  "status": 500,
  "error": "Internal Server Error",
  "message": "Mascota no encontrada",
  "path": "/api/mascotas/123"
}
```

**Solución: Implementar @ControllerAdvice global:**

```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFoundException ex) {
        log.warn("Recurso no encontrado: {}", ex.getMessage());
        ErrorResponse error = new ErrorResponse(
            HttpStatus.NOT_FOUND.value(),
            ex.getMessage(),
            LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }
    
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse> handleValidation(ValidationException ex) {
        log.warn("Error de validación: {}", ex.getMessage());
        ErrorResponse error = new ErrorResponse(
            HttpStatus.BAD_REQUEST.value(),
            ex.getMessage(),
            LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
    
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(AccessDeniedException ex) {
        log.warn("Acceso denegado: {}", ex.getMessage());
        ErrorResponse error = new ErrorResponse(
            HttpStatus.FORBIDDEN.value(),
            "No tienes permisos para realizar esta acción",
            LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneral(Exception ex) {
        log.error("Error inesperado: {}", ex.getMessage(), ex);
        ErrorResponse error = new ErrorResponse(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "Error interno del servidor",
            LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}

@Data
@AllArgsConstructor
class ErrorResponse {
    private int status;
    private String mensaje;
    private LocalDateTime timestamp;
}
```

**Excepciones personalizadas a crear:**
```java
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String mensaje) {
        super(mensaje);
    }
}

public class ValidationException extends RuntimeException {
    public ValidationException(String mensaje) {
        super(mensaje);
    }
}

public class CitaDuplicadaException extends ValidationException {
    public CitaDuplicadaException(String mensaje) {
        super(mensaje);
    }
}
```

**Archivos a crear:**
- `exception/GlobalExceptionHandler.java`
- `exception/ResourceNotFoundException.java`
- `exception/ValidationException.java`
- `exception/CitaDuplicadaException.java`
- `dto/ErrorResponse.java`

**Tiempo de mitigación:** 50 minutos  
**Estado:** RECOMENDADO IMPLEMENTAR ⚠️

---

### **RIESGO #6: Passwords sin encriptar en base de datos**

**Probabilidad:** Media (50%)  
**Impacto:** Crítico ⛔  
**Descripción:**  
Si no se verifica que TODOS los passwords estén encriptados con BCrypt, podría haber vulnerabilidad de seguridad grave.

**Verificación necesaria:**
```sql
-- Verificar formato de passwords en BD
SELECT id, email, password FROM usuario LIMIT 5;

-- Passwords encriptados con BCrypt se ven así:
-- $2a$10$abcdefghijklmnopqrstuvwxyz...

-- Si ves contraseñas en texto plano, HAY PROBLEMA
```

**Solución:**

1. **Script de migración de passwords:**
```sql
-- Si hay passwords sin encriptar, ejecutar:
UPDATE usuario 
SET password = '$2a$10$hashtemporalaquí' 
WHERE password NOT LIKE '$2a$%';

-- Luego pedir a usuarios que cambien contraseña
```

2. **Asegurar encriptación en código:**
```java
@Service
public class UsuarioServiceImpl implements UsuarioService {
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Override
    public Usuario guardar(Usuario usuario) {
        // SIEMPRE encriptar password antes de guardar
        if (usuario.getPassword() != null && 
            !usuario.getPassword().startsWith("$2a$")) {
            usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        }
        return usuarioRepository.save(usuario);
    }
}
```

3. **Validar en SecurityConfig:**
```java
@Bean
public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder(10); // Strength 10
}
```

**Tiempo de mitigación:** 20 minutos  
**Estado:** VERIFICAR INMEDIATAMENTE 🚨

---

## 🟡 RIESGOS MEDIOS

### **RIESGO #7: Nombres de campos inconsistentes (camelCase vs snake_case)**

**Probabilidad:** Alta (90%)  
**Impacto:** Bajo 🟢  
**Descripción:**  
Si los DTOs no mapean correctamente, el frontend recibirá campos con nombres incorrectos.

**Problema:**
```json
// BD devuelve:
{
  "ID_Mascota": 1,
  "Fecha_Registro": "2024-01-15"
}

// Frontend espera:
{
  "idMascota": 1,
  "fechaRegistro": "2024-01-15"
}
```

**Solución:**
```java
// application.properties
spring.jpa.hibernate.naming.physical-strategy=org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl
```

O usar `@JsonProperty`:
```java
@JsonProperty("idMascota")
@Column(name = "ID_Mascota")
private Integer idMascota;
```

**Tiempo de mitigación:** 30 minutos  
**Estado:** MONITOREAR 👁️

---

### **RIESGO #8: Performance en consultas de historias clínicas**

**Probabilidad:** Media (50%)  
**Impacto:** Medio 🟡  
**Descripción:**  
La consulta de historial completo puede ser lenta si una mascota tiene muchas entradas (100+ consultas).

**Problema:**
```java
// Consulta N+1 (malo)
SELECT * FROM historiaclinica WHERE ID_Mascota = 1;
SELECT * FROM entradahistoria WHERE ID_Historia = 1;
SELECT * FROM usuario WHERE id = veterinario1;
SELECT * FROM usuario WHERE id = veterinario2;
// ... una query por cada veterinario
```

**Solución:**
```java
// Usar JOIN FETCH
@Query("SELECT DISTINCT h FROM HistoriaClinica h " +
       "LEFT JOIN FETCH h.entradas e " +
       "LEFT JOIN FETCH e.veterinario " +
       "WHERE h.idMascota = :idMascota")
HistoriaClinica findHistoriaCompleta(@Param("idMascota") Integer idMascota);
```

**Tiempo de mitigación:** 25 minutos  
**Estado:** OPTIMIZACIÓN FUTURA 🔄

---

### **RIESGO #9: Zona horaria (timestamps)**

**Probabilidad:** Media (40%)  
**Impacto:** Medio 🟡  
**Descripción:**  
Si servidor y BD tienen zonas horarias diferentes, las fechas/horas pueden ser incorrectas.

**Solución:**
```properties
# application.properties
spring.jpa.properties.hibernate.jdbc.time_zone=America/Bogota
```

```sql
-- MySQL
SET GLOBAL time_zone = '-05:00';  # Colombia
```

**Tiempo de mitigación:** 10 minutos  
**Estado:** CONFIGURAR AL INICIO ⚙️

---

### **RIESGO #10: CORS mal configurado**

**Probabilidad:** Baja (20%)  
**Impacto:** Medio 🟡  
**Descripción:**  
Si CORS permite `origins = "*"` en producción, hay riesgo de seguridad.

**Solución:**
```java
// SecurityConfig.java - CORRECTO
@Bean
public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    
    if (isProd()) {
        configuration.setAllowedOrigins(List.of("https://clinicaveterinaria.com"));
    } else {
        configuration.setAllowedOrigins(List.of("http://localhost:5173"));
    }
    
    configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE"));
    configuration.setAllowedHeaders(List.of("*"));
    configuration.setAllowCredentials(true);
    
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
}
```

**Tiempo de mitigación:** 15 minutos  
**Estado:** AJUSTAR ANTES DE PRODUCCIÓN 🛡️

---

### **RIESGO #11: Validación de datos de entrada insuficiente**

**Probabilidad:** Alta (70%)  
**Impacto:** Medio 🟡  
**Descripción:**  
Sin validaciones Bean Validation, pueden guardarse datos inválidos.

**Solución:**
```java
// Usar annotations de validación
@Entity
public class Cita {
    @NotNull(message = "Fecha de cita obligatoria")
    @Future(message = "La fecha debe ser futura")
    private LocalDate fechaCita;
    
    @NotNull(message = "Hora obligatoria")
    private LocalTime horaCita;
    
    @NotBlank(message = "Motivo obligatorio")
    @Size(min = 10, max = 200, message = "Motivo entre 10 y 200 caracteres")
    private String motivo;
    
    @Min(value = 15, message = "Duración mínima 15 minutos")
    @Max(value = 120, message = "Duración máxima 120 minutos")
    private Integer duracionMinutos;
}

// Controller
@PostMapping
public ResponseEntity<Cita> crear(@Valid @RequestBody Cita cita, BindingResult result) {
    if (result.hasErrors()) {
        return ResponseEntity.badRequest().body(null);
    }
    // ...
}
```

**Dependencia necesaria:**
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-validation</artifactId>
</dependency>
```

**Tiempo de mitigación:** 45 minutos  
**Estado:** RECOMENDADO ⚠️

---

### **RIESGO #12: JWT tokens sin expiración o renovación**

**Probabilidad:** Media (50%)  
**Impacto:** Medio 🟡  
**Descripción:**  
Si los tokens JWT no expiran, un token robado es válido para siempre.

**Verificar en JwtService.java:**
```java
public String generateToken(String email, String rol) {
    return Jwts.builder()
        .setSubject(email)
        .claim("rol", rol)
        .setIssuedAt(new Date())
        .setExpiration(new Date(System.currentTimeMillis() + 86400000)) // 24 horas
        .signWith(getSignKey(), SignatureAlgorithm.HS256)
        .compact();
}
```

**Solución: Implementar refresh tokens:**
```java
// Crear endpoint para renovar token
@PostMapping("/api/auth/refresh")
public ResponseEntity<AuthResponse> refresh(@RequestHeader("Authorization") String token) {
    // Validar token actual
    // Generar nuevo token con nueva expiración
    // Retornar nuevo token
}
```

**Tiempo de mitigación:** 40 minutos  
**Estado:** MEJORA DE SEGURIDAD 🔐

---

## 🟢 RIESGOS BAJOS

### **RIESGO #13: Falta de paginación en listados**

**Probabilidad:** Baja (30%)  
**Impacto:** Bajo 🟢  
**Descripción:**  
Si hay 1000+ citas, el endpoint `/api/citas` será muy lento.

**Solución:**
```java
@GetMapping
public ResponseEntity<Page<Cita>> listar(
    @RequestParam(defaultValue = "0") int page,
    @RequestParam(defaultValue = "20") int size) {
    
    Pageable pageable = PageRequest.of(page, size);
    Page<Cita> citas = citaRepository.findAll(pageable);
    return ResponseEntity.ok(citas);
}

