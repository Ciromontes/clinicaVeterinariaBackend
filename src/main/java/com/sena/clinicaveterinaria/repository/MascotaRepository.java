// src/main/java/com/sena/clinicaveterinaria/repository/MascotaRepository.java
package com.sena.clinicaveterinaria.repository;

import com.sena.clinicaveterinaria.model.Mascota;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MascotaRepository extends JpaRepository<Mascota, Integer> {

    // ✅ Método correcto: Spring Data JPA lo genera automáticamente
    // basándose en la relación: Mascota → Cliente (campo "cliente" en Mascota)
    List<Mascota> findByClienteIdCliente(Integer idCliente);
}