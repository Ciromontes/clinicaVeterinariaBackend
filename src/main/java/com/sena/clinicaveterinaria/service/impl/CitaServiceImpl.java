// src/main/java/com/sena/clinicaveterinaria/service/impl/CitaServiceImpl.java
package com.sena.clinicaveterinaria.service.impl;

import com.sena.clinicaveterinaria.model.Cita;
import com.sena.clinicaveterinaria.model.Mascota;
import com.sena.clinicaveterinaria.model.Usuario;
import com.sena.clinicaveterinaria.repository.CitaRepository;
import com.sena.clinicaveterinaria.service.CitaService;
import com.sena.clinicaveterinaria.service.MascotaService;
import com.sena.clinicaveterinaria.service.UsuarioService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CitaServiceImpl implements CitaService {
    private final CitaRepository repository;
    private final UsuarioService usuarioService;
    private final MascotaService mascotaService;

    public CitaServiceImpl(CitaRepository repository, UsuarioService usuarioService, MascotaService mascotaService) {
        this.repository = repository;
        this.usuarioService = usuarioService;
        this.mascotaService = mascotaService;
    }

    @Override
    public List<Cita> listar() {
        return repository.findAll();
    }

    @Override
    public Cita buscarPorId(Integer id) {
        return repository.findById(id).orElse(null);
    }

    @Override
    public Cita guardar(Cita cita) {
        return repository.save(cita);
    }

    @Override
    public void eliminar(Integer id) {
        repository.deleteById(id);
    }

    // ✅ IMPLEMENTAR métodos nuevos
    @Override
    public List<Cita> findCitasByUsuarioEmail(String email) {
        Usuario usuario = usuarioService.buscarPorEmail(email);
        if (usuario == null || usuario.getIdCliente() == null) {
            throw new RuntimeException("Usuario no tiene cliente asociado");
        }

        // Obtener mascotas del cliente
        List<Mascota> mascotas = mascotaService.findMascotasByUsuarioEmail(email);
        List<Integer> mascotaIds = mascotas.stream()
                .map(Mascota::getIdMascota)
                .collect(Collectors.toList());

        if (mascotaIds.isEmpty()) {
            return List.of(); // Retornar lista vacía si no tiene mascotas
        }

        // Buscar citas de esas mascotas
        return repository.findByIdMascotaIn(mascotaIds);
    }

    @Override
    public Cita agendarCita(Cita cita, String emailUsuario) {
        Usuario usuario = usuarioService.buscarPorEmail(emailUsuario);
        if (usuario == null || usuario.getIdCliente() == null) {
            throw new RuntimeException("Usuario no puede agendar citas");
        }

        // Validar que la mascota pertenece al usuario
        Mascota mascota = mascotaService.buscarPorId(cita.getIdMascota());
        if (mascota == null || !mascota.getIdCliente().equals(usuario.getIdCliente())) {
            throw new RuntimeException("La mascota no pertenece al usuario");
        }

        return repository.save(cita);
    }
}
