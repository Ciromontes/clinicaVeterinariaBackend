package com.sena.clinicaveterinaria.service;

import com.sena.clinicaveterinaria.model.Usuario;
import java.util.List;

public interface UsuarioService {
    List<Usuario> listar();
    Usuario buscarPorId(Integer id);
    Usuario guardar(Usuario usuario);
    void eliminar(Integer id);
    Usuario buscarPorEmail(String email);
}