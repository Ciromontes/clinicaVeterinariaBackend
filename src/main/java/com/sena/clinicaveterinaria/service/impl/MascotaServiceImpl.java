// src/main/java/com/sena/clinicaveterinaria/service/impl/MascotaServiceImpl.java
// src/main/java/com/sena/clinicaveterinaria/service/impl/MascotaServiceImpl.java
package com.sena.clinicaveterinaria.service.impl;

import com.sena.clinicaveterinaria.model.Mascota;
import com.sena.clinicaveterinaria.model.Usuario;
import com.sena.clinicaveterinaria.repository.MascotaRepository;
import com.sena.clinicaveterinaria.service.MascotaService;
import com.sena.clinicaveterinaria.service.UsuarioService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MascotaServiceImpl implements MascotaService {
    private final MascotaRepository repository;
    private final UsuarioService usuarioService; // dependencia inyectada

    public MascotaServiceImpl(MascotaRepository repository, UsuarioService usuarioService) {
        this.repository = repository;
        this.usuarioService = usuarioService;
    }

    @Override
    public List<Mascota> listar() {
        return repository.findAll();
    }

    @Override
    public Mascota buscarPorId(Integer id) {
        return repository.findById(id).orElse(null);
    }

    @Override
    public Mascota guardar(Mascota mascota) {
        return repository.save(mascota);
    }

    @Override
    public void eliminar(Integer id) {
        repository.deleteById(id);
    }

    @Override
    public List<Mascota> findMascotasByUsuarioEmail(String email) {
        System.out.println("🔍 DEBUG: Buscando mascotas para: " + email);

        Usuario usuario = usuarioService.buscarPorEmail(email);
        System.out.println("✅ Usuario encontrado: " + (usuario != null ? usuario.getNombre() : "NULL"));

        if (usuario == null) {
            System.out.println("❌ ERROR: Usuario no encontrado");
            throw new RuntimeException("Usuario no encontrado: " + email);
        }

        System.out.println("📋 ID Cliente del usuario: " + usuario.getIdCliente());

        if (usuario.getIdCliente() == null) {
            System.out.println("❌ ERROR: Usuario no tiene cliente asociado");
            throw new RuntimeException("El usuario no tiene un cliente asociado");
        }

        List<Mascota> mascotas = repository.findByClienteIdCliente(usuario.getIdCliente());
        System.out.println("🐕 Mascotas encontradas: " + (mascotas != null ? mascotas.size() : "0"));

        if (mascotas != null) {
            for (Mascota mascota : mascotas) {
                System.out.println("   - " + mascota.getNombre() + " (ID: " + mascota.getIdMascota() + ")");
            }
        }

        return mascotas;
    }
}
