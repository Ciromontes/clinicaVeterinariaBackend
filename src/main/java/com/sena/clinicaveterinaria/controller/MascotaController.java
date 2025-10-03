// src/main/java/com/sena/clinicaveterinaria/controller/MascotaController.java
package com.sena.clinicaveterinaria.controller;

import com.sena.clinicaveterinaria.model.Mascota;
import com.sena.clinicaveterinaria.service.MascotaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mascotas")
@CrossOrigin(origins = "*")
public class MascotaController {

    private static final Logger log = LoggerFactory.getLogger(MascotaController.class);

    private final MascotaService service;

    public MascotaController(MascotaService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<Mascota>> listar() {
        log.info("GET /api/mascotas - Listando todas las mascotas");
        try {
            List<Mascota> mascotas = service.listar();
            log.info("Mascotas encontradas: {}", mascotas.size());
            return ResponseEntity.ok(mascotas);
        } catch (Exception e) {
            log.error("Error al listar mascotas: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Mascota> buscarPorId(@PathVariable Integer id) {
        log.info("GET /api/mascotas/{} - Buscando mascota por ID", id);
        try {
            Mascota mascota = service.buscarPorId(id);
            if (mascota != null) {
                log.info("Mascota encontrada: ID={}, Nombre={}, Especie={}",
                        id, mascota.getNombre(), mascota.getEspecie());
                return ResponseEntity.ok(mascota);
            } else {
                log.warn("Mascota no encontrada: ID={}", id);
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            log.error("Error al buscar mascota ID={}: {}", id, e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/mias")
    public ResponseEntity<?> getMisMascotas(Authentication authentication) {
        String username = authentication.getName();
        log.info("GET /api/mascotas/mias - Usuario: {}", username);

        try {
            List<Mascota> mascotas = service.findMascotasByUsuarioEmail(username);
            log.info("Mascotas del usuario {}: {} encontradas", username, mascotas.size());

            if (mascotas.isEmpty()) {
                log.debug("Usuario {} no tiene mascotas registradas", username);
            }

            return ResponseEntity.ok(mascotas);
        } catch (RuntimeException e) {
            log.warn("Validación fallida para usuario {}: {}", username, e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            log.error("Error al obtener mascotas del usuario {}: {}", username, e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Mascota> actualizar(@PathVariable Integer id, @RequestBody Mascota mascota) {
        log.info("PUT /api/mascotas/{} - Actualizando mascota", id);
        log.debug("Datos recibidos: Nombre={}, Especie={}, Peso={}",
                mascota.getNombre(), mascota.getEspecie(), mascota.getPeso());

        try {
            mascota.setIdMascota(id);
            Mascota mascotaActualizada = service.guardar(mascota);
            log.info("Mascota actualizada: ID={}, Nombre={}", id, mascotaActualizada.getNombre());
            return ResponseEntity.ok(mascotaActualizada);
        } catch (Exception e) {
            log.error("Error al actualizar mascota ID={}: {}", id, e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
}