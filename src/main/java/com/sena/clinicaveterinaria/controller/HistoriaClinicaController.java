package com.sena.clinicaveterinaria.controller;

import com.sena.clinicaveterinaria.model.EntradaHistoria;
import com.sena.clinicaveterinaria.model.HistoriaClinica;
import com.sena.clinicaveterinaria.service.HistoriaClinicaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para gestionar historias clínicas y entradas médicas.
 *
 * Endpoints protegidos por Spring Security, solo accesibles para usuarios
 * con rol VETERINARIO o ADMIN.
 */
@RestController
@RequestMapping("/api/historias")
@CrossOrigin(origins = "*")
public class HistoriaClinicaController {

    private static final Logger log = LoggerFactory.getLogger(HistoriaClinicaController.class);

    private final HistoriaClinicaService service;

    public HistoriaClinicaController(HistoriaClinicaService service) {
        this.service = service;
    }

    /**
     * Obtiene la historia clínica de una mascota específica.
     * Si no existe, se crea automáticamente.
     *
     * @param idMascota ID de la mascota
     * @return Historia clínica encontrada o recién creada
     */
    @GetMapping("/mascota/{idMascota}")
    public ResponseEntity<HistoriaClinica> getHistoriaPorMascota(@PathVariable Integer idMascota) {
        log.info("GET /api/historias/mascota/{} - Obteniendo historia clínica", idMascota);

        try {
            HistoriaClinica historia = service.obtenerPorMascota(idMascota);
            log.info("Historia clínica encontrada: ID={}, Mascota={}",
                    historia.getIdHistoria(), idMascota);
            return ResponseEntity.ok(historia);
        } catch (Exception e) {
            log.error("Error al obtener historia clínica para mascota {}: {}",
                    idMascota, e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Lista todas las entradas médicas de una historia clínica.
     * Las entradas se devuelven ordenadas de más reciente a más antigua.
     *
     * @param idHistoria ID de la historia clínica
     * @return Lista de entradas médicas
     */
    @GetMapping("/{idHistoria}/entradas")
    public ResponseEntity<List<EntradaHistoria>> getEntradas(@PathVariable Integer idHistoria) {
        log.info("GET /api/historias/{}/entradas - Listando entradas médicas", idHistoria);

        try {
            List<EntradaHistoria> entradas = service.obtenerEntradas(idHistoria);
            log.info("Entradas encontradas: {} para historia ID={}", entradas.size(), idHistoria);
            return ResponseEntity.ok(entradas);
        } catch (Exception e) {
            log.error("Error al obtener entradas de historia {}: {}",
                    idHistoria, e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Agrega una nueva entrada médica a una historia clínica.
     * El veterinario logueado queda registrado como autor de la entrada.
     *
     * @param idHistoria ID de la historia clínica
     * @param entrada Datos de la entrada médica (descripción, observaciones, signos vitales)
     * @param authentication Usuario autenticado (inyectado por Spring Security)
     * @return Entrada médica guardada con ID asignado
     */
    @PostMapping("/{idHistoria}/entrada")
    public ResponseEntity<?> agregarEntrada(
            @PathVariable Integer idHistoria,
            @RequestBody EntradaHistoria entrada,
            Authentication authentication) {

        String username = authentication.getName();
        log.info("POST /api/historias/{}/entrada - Veterinario: {}", idHistoria, username);
        log.debug("Entrada recibida: Descripción={}, Peso={}, Temp={}, FC={}",
                entrada.getDescripcion(),
                entrada.getPesoActual(),
                entrada.getTemperatura(),
                entrada.getFrecuenciaCardiaca());

        try {
            EntradaHistoria entradaGuardada = service.agregarEntrada(idHistoria, entrada, username);
            log.info("Entrada médica agregada exitosamente - ID={}", entradaGuardada.getIdEntrada());
            return ResponseEntity.ok(entradaGuardada);
        } catch (RuntimeException e) {
            log.warn("Validación fallida al agregar entrada: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            log.error("Error al agregar entrada a historia {}: {}",
                    idHistoria, e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
}