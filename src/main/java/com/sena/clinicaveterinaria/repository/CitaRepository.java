// src/main/java/com/sena/clinicaveterinaria/repository/CitaRepository.java
package com.sena.clinicaveterinaria.repository;

import com.sena.clinicaveterinaria.model.Cita;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CitaRepository extends JpaRepository<Cita, Integer> {
    // ✅ AGREGAR este método
    List<Cita> findByIdMascotaIn(List<Integer> idMascotas);
}