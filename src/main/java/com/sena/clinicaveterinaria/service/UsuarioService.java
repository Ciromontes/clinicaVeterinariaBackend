package com.sena.clinicaveterinaria.service;

import com.sena.clinicaveterinaria.model.Usuario;
import java.util.List;

public interface UsuarioService {
    List<Usuario> listar();
    Usuario buscarPorId(Integer id);
    Usuario guardar(Usuario usuario);
    void eliminar(Integer id);
    Usuario buscarPorEmail(String email);

    // FASE 4: Gestión de usuarios (ADMIN)
    /**
     * Cambia el estado activo/inactivo de un usuario
     * @param id ID del usuario
     * @param activo true para activar, false para desactivar
     * @return Usuario actualizado
     */
    Usuario cambiarEstado(Integer id, Boolean activo);

    /**
     * Lista todos los veterinarios activos del sistema
     * @return Lista de usuarios con rol VETERINARIO y activo=true
     */
    List<Usuario> listarVeterinariosActivos();
}