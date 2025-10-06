package com.sena.clinicaveterinaria.repository;

import com.sena.clinicaveterinaria.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
    Usuario findByEmail(String email);

    /**
     * Busca todos los usuarios con rol VETERINARIO y activos
     */
    List<Usuario> findByRolAndActivo(String rol, Boolean activo);
}