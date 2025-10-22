package com.sena.clinicaveterinaria.repository;

import com.sena.clinicaveterinaria.model.Cita;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface CitaRepository extends JpaRepository<Cita, Integer> {
    List<Cita> findByIdMascotaIn(List<Integer> idMascotas);

    // ✅ MÉTODOS PARA OBTENER CITAS POR FECHA
    List<Cita> findByIdVeterinarioAndFechaCita(Integer idVeterinario, LocalDate fecha);
    List<Cita> findByFechaCita(LocalDate fecha); // Para ADMIN - Ver todas las citas de una fecha
    List<Cita> findByIdVeterinario(Integer idVeterinario);
}