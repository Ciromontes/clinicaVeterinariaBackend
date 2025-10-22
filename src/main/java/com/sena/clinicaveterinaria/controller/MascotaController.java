package com.sena.clinicaveterinaria.controller;

import com.sena.clinicaveterinaria.dto.MascotaCreateDTO;
import com.sena.clinicaveterinaria.model.Cliente;
import com.sena.clinicaveterinaria.model.Mascota;
import com.sena.clinicaveterinaria.repository.ClienteRepository;
import com.sena.clinicaveterinaria.service.MascotaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/mascotas")
@CrossOrigin(origins = "*")
public class MascotaController {

    private static final Logger log = LoggerFactory.getLogger(MascotaController.class);

    private final MascotaService service;
    private final ClienteRepository clienteRepository;

    public MascotaController(MascotaService service, ClienteRepository clienteRepository) {
        this.service = service;
        this.clienteRepository = clienteRepository;
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

    @PostMapping
    public ResponseEntity<?> crear(@RequestBody MascotaCreateDTO dto) {
        log.info("POST /api/mascotas - Creando nueva mascota");
        log.debug("Datos recibidos: Nombre={}, Especie={}, Raza={}, ID Cliente={}",
                dto.getNombre(), dto.getEspecie(), dto.getRaza(), dto.getIdCliente());

        try {
            // Validar que el ID del cliente no sea nulo
            if (dto.getIdCliente() == null) {
                log.warn("ID de cliente no proporcionado");
                return ResponseEntity.badRequest().body("El ID del cliente es obligatorio");
            }

            // Buscar el cliente por ID
            Optional<Cliente> clienteOpt = clienteRepository.findById(dto.getIdCliente());
            if (!clienteOpt.isPresent()) {
                log.warn("Cliente no encontrado con ID: {}", dto.getIdCliente());
                return ResponseEntity.badRequest().body("Cliente no encontrado con ID: " + dto.getIdCliente());
            }

            Cliente cliente = clienteOpt.get();

            // Crear la mascota y asignar los datos del DTO
            Mascota mascota = new Mascota();
            mascota.setNombre(dto.getNombre());
            mascota.setEspecie(Mascota.Especie.valueOf(dto.getEspecie()));
            mascota.setRaza(dto.getRaza());
            mascota.setEdad(dto.getEdad());
            mascota.setPeso(dto.getPeso());
            mascota.setColor(dto.getColor());
            mascota.setSexo(Mascota.Sexo.valueOf(dto.getSexo()));
            mascota.setCliente(cliente);  // ✅ Asignar el cliente completo

            // Guardar la mascota
            Mascota mascotaCreada = service.guardar(mascota);
            log.info("Mascota creada exitosamente: ID={}, Nombre={}",
                    mascotaCreada.getIdMascota(), mascotaCreada.getNombre());
            return ResponseEntity.ok(mascotaCreada);
        } catch (IllegalArgumentException e) {
            log.error("Error en los datos de la mascota: {}", e.getMessage());
            return ResponseEntity.badRequest().body("Datos inválidos: " + e.getMessage());
        } catch (Exception e) {
            log.error("Error al crear mascota: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body("Error al crear la mascota");
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