package com.sena.clinicaveterinaria.controller;

import com.sena.clinicaveterinaria.model.Cita;
import com.sena.clinicaveterinaria.service.CitaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/citas")
@CrossOrigin(origins = "*")
public class CitaController {

    private static final Logger log = LoggerFactory.getLogger(CitaController.class);

    private final CitaService service;

    public CitaController(CitaService service) {
        this.service = service;
    }

    // ✅ Solo ADMIN y VETERINARIO pueden ver todas las citas (coordinación)
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIO')")
    public ResponseEntity<List<Cita>> listar() {
        log.info("GET /api/citas - Listando todas las citas");
        try {
            List<Cita> citas = service.listar();
            log.info("Citas encontradas: {}", citas.size());
            return ResponseEntity.ok(citas);
        } catch (Exception e) {
            log.error("Error al listar citas: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Cita> buscarPorId(@PathVariable Integer id) {
        log.info("GET /api/citas/{} - Buscando cita por ID", id);
        try {
            Cita cita = service.buscarPorId(id);
            if (cita != null) {
                log.info("Cita encontrada: ID={}, Motivo={}", id, cita.getMotivo());
                return ResponseEntity.ok(cita);
            } else {
                log.warn("Cita no encontrada: ID={}", id);
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            log.error("Error al buscar cita ID={}: {}", id, e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping
    public ResponseEntity<Cita> guardar(@RequestBody Cita cita) {
        log.info("POST /api/citas - Creando nueva cita");
        log.debug("Datos recibidos: Mascota={}, Veterinario={}, Fecha={}",
                cita.getIdMascota(), cita.getIdVeterinario(), cita.getFechaCita());

        try {
            Cita citaGuardada = service.guardar(cita);
            log.info("Cita creada exitosamente: ID={}", citaGuardada.getId());
            return ResponseEntity.ok(citaGuardada);
        } catch (Exception e) {
            log.error("Error al guardar cita: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Cita> actualizar(@PathVariable Integer id, @RequestBody Cita cita) {
        log.info("PUT /api/citas/{} - Actualizando cita", id);
        try {
            cita.setId(id);
            Cita citaActualizada = service.guardar(cita);
            log.info("Cita actualizada: ID={}, Estado={}", id, citaActualizada.getEstadoCita());
            return ResponseEntity.ok(citaActualizada);
        } catch (Exception e) {
            log.error("Error al actualizar cita ID={}: {}", id, e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        log.info("DELETE /api/citas/{} - Eliminando cita", id);
        try {
            service.eliminar(id);
            log.info("Cita eliminada: ID={}", id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            log.error("Error al eliminar cita ID={}: {}", id, e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/mis-citas")
    public ResponseEntity<List<Cita>> getMisCitas(Authentication authentication) {
        String username = authentication.getName();
        log.info("GET /api/citas/mis-citas - Usuario: {}", username);

        try {
            List<Cita> citas = service.findCitasByUsuarioEmail(username);
            log.info("Citas del usuario {}: {} citas encontradas", username, citas.size());
            return ResponseEntity.ok(citas);
        } catch (Exception e) {
            log.error("Error al obtener citas del usuario {}: {}", username, e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/agendar")
    public ResponseEntity<?> agendarCita(@RequestBody Cita cita, Authentication authentication) {
        String username = authentication.getName();
        log.info("POST /api/citas/agendar - Usuario: {}", username);
        log.debug("Agendando cita: Mascota={}, Fecha={}, Hora={}",
                cita.getIdMascota(), cita.getFechaCita(), cita.getHoraCita());

        try {
            Cita citaAgendada = service.agendarCita(cita, username);
            log.info("Cita agendada exitosamente para usuario {}: ID={}", username, citaAgendada.getId());
            return ResponseEntity.ok(citaAgendada);
        } catch (RuntimeException e) {
            log.warn("Validación fallida al agendar cita para {}: {}", username, e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            log.error("Error al agendar cita para {}: {}", username, e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    // ✅ ENDPOINT: Obtener citas del día
    // VETERINARIO: Ve solo SUS citas
    // ADMIN: Ve TODAS las citas del día
    @GetMapping("/hoy")
    public ResponseEntity<?> getCitasHoy(Authentication authentication) {
        String username = authentication.getName();
        log.info("GET /api/citas/hoy - Usuario: {}", username);

        try {
            // Obtener el rol del usuario desde el Authentication
            String rol = authentication.getAuthorities().stream()
                    .findFirst()
                    .map(a -> a.getAuthority())
                    .orElse("");

            log.debug("Rol del usuario {}: {}", username, rol);

            List<Cita> citas;

            // Si es ADMIN, devolver TODAS las citas de hoy
            if (rol.equals("ROLE_ADMIN")) {
                log.info("ADMIN solicitando todas las citas de hoy");
                citas = service.findTodasLasCitasHoy();
                log.info("Total de citas de hoy (todos los veterinarios): {}", citas.size());
            }
            // Si es VETERINARIO, devolver solo SUS citas
            else if (rol.equals("ROLE_VETERINARIO")) {
                log.info("VETERINARIO solicitando sus citas de hoy");
                citas = service.findCitasHoyByVeterinario(username);
                log.info("Citas de hoy para veterinario {}: {}", username, citas.size());
            }
            // Otros roles no tienen acceso
            else {
                log.warn("Usuario {} con rol {} intentó acceder a citas de hoy", username, rol);
                return ResponseEntity.status(403).body("Acceso denegado. Solo ADMIN y VETERINARIO pueden ver citas de hoy");
            }

            return ResponseEntity.ok(citas);
        } catch (RuntimeException e) {
            log.warn("Validación fallida al obtener citas de hoy para {}: {}", username, e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            log.error("Error al obtener citas de hoy para {}: {}", username, e.getMessage(), e);
            return ResponseEntity.internalServerError().body("Error al obtener citas de hoy");
        }
    }

    // ✅ NUEVO ENDPOINT: Actualizar estado de cita
    @PutMapping("/{id}/estado")
    public ResponseEntity<?> actualizarEstado(
            @PathVariable Integer id,
            @RequestBody Map<String, String> body,
            Authentication authentication) {

        String username = authentication.getName();
        String nuevoEstado = body.get("estado");

        log.info("PUT /api/citas/{}/estado - Veterinario: {}, Nuevo estado: {}",
                id, username, nuevoEstado);

        try {
            Cita citaActualizada = service.actualizarEstado(id, nuevoEstado, username);
            log.info("Estado de cita ID={} actualizado exitosamente a '{}'", id, nuevoEstado);
            return ResponseEntity.ok(citaActualizada);
        } catch (RuntimeException e) {
            log.warn("Validación fallida al actualizar estado de cita ID={}: {}", id, e.getMessage());
            return ResponseEntity.status(403).body(e.getMessage());
        } catch (Exception e) {
            log.error("Error al actualizar estado de cita ID={}: {}", id, e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
}