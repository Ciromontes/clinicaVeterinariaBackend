package com.sena.clinicaveterinaria.controller;

import com.sena.clinicaveterinaria.model.Usuario;
import com.sena.clinicaveterinaria.service.UsuarioService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
}