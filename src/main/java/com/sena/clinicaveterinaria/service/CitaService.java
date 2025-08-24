package com.sena.clinicaveterinaria.service;

import com.sena.clinicaveterinaria.model.Cita;
import java.util.List;

public interface CitaService {
    List<Cita> listar();
    Cita buscarPorId(Integer id);
    Cita guardar(Cita cita);
    void eliminar(Integer id);
}
