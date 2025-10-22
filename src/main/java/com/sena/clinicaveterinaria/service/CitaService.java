package com.sena.clinicaveterinaria.service;

import com.sena.clinicaveterinaria.model.Cita;
import java.util.List;

public interface CitaService {
    List<Cita> listar();
    Cita buscarPorId(Integer id);
    Cita guardar(Cita cita);
    void eliminar(Integer id);
    List<Cita> findCitasByUsuarioEmail(String email);
    Cita agendarCita(Cita cita, String emailUsuario);

    // ✅ MÉTODOS PARA CITAS DE HOY
    List<Cita> findCitasHoyByVeterinario(String emailVeterinario);
    List<Cita> findTodasLasCitasHoy(); // Para ADMIN - Ver todas las citas del día
    Cita actualizarEstado(Integer idCita, String nuevoEstado, String emailVeterinario);
}