package com.sena.clinicaveterinaria.service.impl;

import com.sena.clinicaveterinaria.model.Cita;
import com.sena.clinicaveterinaria.repository.CitaRepository;
import com.sena.clinicaveterinaria.service.CitaService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CitaServiceImpl implements CitaService {
    private final CitaRepository repository;

    public CitaServiceImpl(CitaRepository repository) {
        this.repository = repository;
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
}
