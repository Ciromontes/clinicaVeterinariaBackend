package com.sena.clinicaveterinaria.service.impl;

import com.sena.clinicaveterinaria.model.Usuario;
import com.sena.clinicaveterinaria.repository.UsuarioRepository;
import com.sena.clinicaveterinaria.service.UsuarioService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class UsuarioServiceImpl implements UsuarioService {

    private static final Logger log = LoggerFactory.getLogger(UsuarioServiceImpl.class);

    private final UsuarioRepository usuarioRepository;

    public UsuarioServiceImpl(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public List<Usuario> listar() {
        log.debug("Servicio: Listando todos los usuarios");
        List<Usuario> usuarios = usuarioRepository.findAll();
        log.debug("Servicio: {} usuarios encontrados en BD", usuarios.size());
        return usuarios;
    }

    @Override
    public Usuario buscarPorId(Integer id) {
        log.debug("Servicio: Buscando usuario con ID={}", id);
        Usuario usuario = usuarioRepository.findById(id).orElse(null);
        if (usuario != null) {
            log.debug("Servicio: Usuario encontrado - ID={}, Email={}, Rol={}",
                    id, usuario.getEmail(), usuario.getRol());
        } else {
            log.warn("Servicio: Usuario no encontrado con ID={}", id);
        }
        return usuario;
    }

    @Override
    public Usuario guardar(Usuario usuario) {
        log.debug("Servicio: Guardando usuario - Email={}, Rol={}",
                usuario.getEmail(), usuario.getRol());

        Usuario usuarioGuardado = usuarioRepository.save(usuario);
        log.info("Servicio: Usuario guardado exitosamente - ID={}, Email={}",
                usuarioGuardado.getId(), usuarioGuardado.getEmail());
        return usuarioGuardado;
    }

    @Override
    public void eliminar(Integer id) {
        log.debug("Servicio: Eliminando usuario ID={}", id);
        if (!usuarioRepository.existsById(id)) {
            log.warn("Servicio: Intento de eliminar usuario inexistente ID={}", id);
            throw new RuntimeException("Usuario no encontrado con ID: " + id);
        }
        usuarioRepository.deleteById(id);
        log.info("Servicio: Usuario eliminado exitosamente - ID={}", id);
    }

    @Override
    public Usuario buscarPorEmail(String email) {
        log.debug("Servicio: Buscando usuario por email: {}", email);
        Usuario usuario = usuarioRepository.findByEmail(email);
        if (usuario != null) {
            log.debug("Servicio: Usuario encontrado - Email={}, Rol={}, Activo={}",
                    email, usuario.getRol(), usuario.getActivo());
        } else {
            log.warn("Servicio: Usuario no encontrado con email: {}", email);
        }
        return usuario;
    }

    // ========================================
    // FASE 4: GESTIÓN DE USUARIOS (ADMIN)
    // ========================================

    @Override
    public Usuario cambiarEstado(Integer id, Boolean activo) {
        log.info("Servicio: Cambiando estado del usuario ID={} a: {}",
                id, activo ? "ACTIVO" : "INACTIVO");

        // Buscar usuario por ID
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Servicio: Usuario no encontrado con ID={}", id);
                    return new RuntimeException("Usuario no encontrado con ID: " + id);
                });

        log.debug("Servicio: Usuario encontrado - Email={}, Rol={}, Estado actual={}",
                usuario.getEmail(), usuario.getRol(), usuario.getActivo());

        // Actualizar campo activo
        usuario.setActivo(activo);

        // Guardar cambios
        Usuario usuarioActualizado = usuarioRepository.save(usuario);

        log.info("Servicio: Estado del usuario {} cambiado exitosamente. Email={}, Nuevo estado={}",
                id, usuarioActualizado.getEmail(), activo ? "ACTIVO" : "INACTIVO");

        return usuarioActualizado;
    }

    @Override
    public List<Usuario> listarVeterinariosActivos() {
        log.debug("Servicio: Listando veterinarios activos");
        List<Usuario> veterinarios = usuarioRepository.findByRolAndActivo("VETERINARIO", true);
        log.info("Servicio: {} veterinarios activos encontrados", veterinarios.size());
        return veterinarios;
    }
}