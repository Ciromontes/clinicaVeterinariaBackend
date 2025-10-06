package com.sena.clinicaveterinaria.controller;

import com.sena.clinicaveterinaria.model.Usuario;
import com.sena.clinicaveterinaria.service.UsuarioService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "*")
public class UsuarioController {

    private static final Logger log = LoggerFactory.getLogger(UsuarioController.class);

    private final UsuarioService service;

    public UsuarioController(UsuarioService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<Usuario>> listar() {
        log.info("GET /api/usuarios - Listando todos los usuarios");
        try {
            List<Usuario> usuarios = service.listar();
            log.info("Usuarios encontrados: {}", usuarios.size());
            return ResponseEntity.ok(usuarios);
        } catch (Exception e) {
            log.error("Error al listar usuarios: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Usuario> buscarPorId(@PathVariable Integer id) {
        log.info("GET /api/usuarios/{} - Buscando usuario por ID", id);
        try {
            Usuario usuario = service.buscarPorId(id);
            if (usuario != null) {
                log.info("Usuario encontrado: ID={}, Email={}, Rol={}",
                        id, usuario.getEmail(), usuario.getRol());
                return ResponseEntity.ok(usuario);
            } else {
                log.warn("Usuario no encontrado: ID={}", id);
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            log.error("Error al buscar usuario ID={}: {}", id, e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping
    public ResponseEntity<Usuario> guardar(@RequestBody Usuario usuario) {
        log.info("POST /api/usuarios - Creando nuevo usuario");
        log.debug("Email: {}, Rol: {}", usuario.getEmail(), usuario.getRol());

        try {
            Usuario usuarioGuardado = service.guardar(usuario);
            log.info("Usuario creado exitosamente: ID={}, Email={}",
                    usuarioGuardado.getId(), usuarioGuardado.getEmail());
            return ResponseEntity.ok(usuarioGuardado);
        } catch (Exception e) {
            log.error("Error al guardar usuario {}: {}", usuario.getEmail(), e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Usuario> actualizar(@PathVariable Integer id, @RequestBody Usuario usuario) {
        log.info("PUT /api/usuarios/{} - Actualizando usuario", id);
        try {
            usuario.setId(id);
            Usuario usuarioActualizado = service.guardar(usuario);
            log.info("Usuario actualizado: ID={}, Email={}", id, usuarioActualizado.getEmail());
            return ResponseEntity.ok(usuarioActualizado);
        } catch (Exception e) {
            log.error("Error al actualizar usuario ID={}: {}", id, e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        log.info("DELETE /api/usuarios/{} - Eliminando usuario", id);
        try {
            service.eliminar(id);
            log.info("Usuario eliminado: ID={}", id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            log.error("Error al eliminar usuario ID={}: {}", id, e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<Usuario> buscarPorEmail(@PathVariable String email) {
        log.info("GET /api/usuarios/email/{} - Buscando usuario por email", email);
        try {
            Usuario usuario = service.buscarPorEmail(email);
            if (usuario != null) {
                log.info("Usuario encontrado por email: {}, Rol: {}", email, usuario.getRol());
                return ResponseEntity.ok(usuario);
            } else {
                log.warn("Usuario no encontrado por email: {}", email);
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            log.error("Error al buscar usuario por email {}: {}", email, e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    // ========================================
    // FASE 4: GESTIÓN DE USUARIOS (ADMIN)
    // ========================================

    /**
     * Endpoint para activar/desactivar usuarios (solo ADMIN)
     * PUT /api/usuarios/{id}/estado
     * Body: {"activo": true/false}
     */
    @PutMapping("/{id}/estado")
    public ResponseEntity<Usuario> cambiarEstado(
            @PathVariable Integer id,
            @RequestBody Map<String, Boolean> request) {

        log.info("PUT /api/usuarios/{}/estado - Cambiando estado de usuario", id);

        try {
            // Extraer el valor del campo 'activo' del request
            Boolean activo = request.get("activo");

            if (activo == null) {
                log.warn("Campo 'activo' no proporcionado en el request");
                return ResponseEntity.badRequest().build();
            }

            log.debug("Cambiando estado a: {}", activo);

            // Llamar al servicio para cambiar el estado
            Usuario usuarioActualizado = service.cambiarEstado(id, activo);

            log.info("Estado del usuario {} cambiado exitosamente a: {}",
                    id, activo ? "ACTIVO" : "INACTIVO");

            return ResponseEntity.ok(usuarioActualizado);

        } catch (RuntimeException e) {
            log.error("Error al cambiar estado del usuario ID={}: {}", id, e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Error inesperado al cambiar estado del usuario ID={}: {}",
                    id, e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Endpoint para listar veterinarios activos (disponible para CLIENTES)
     * GET /api/usuarios/veterinarios/activos
     */
    @GetMapping("/veterinarios/activos")
    public ResponseEntity<List<Usuario>> listarVeterinariosActivos() {
        log.info("GET /api/usuarios/veterinarios/activos - Listando veterinarios disponibles");
        try {
            List<Usuario> veterinarios = service.listarVeterinariosActivos();
            log.info("Veterinarios activos encontrados: {}", veterinarios.size());
            return ResponseEntity.ok(veterinarios);
        } catch (Exception e) {
            log.error("Error al listar veterinarios activos: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
}