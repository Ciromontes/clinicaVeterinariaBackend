// src/main/java/com/sena/clinicaveterinaria/service/impl/MascotaServiceImpl.java
package com.sena.clinicaveterinaria.service.impl;

import com.sena.clinicaveterinaria.model.Mascota;
import com.sena.clinicaveterinaria.repository.MascotaRepository;
import com.sena.clinicaveterinaria.service.MascotaService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MascotaServiceImpl implements MascotaService {
    private final MascotaRepository repository;

    public MascotaServiceImpl(MascotaRepository repository) {
        this.repository = repository;
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
}