// src/main/java/com/sena/clinicaveterinaria/service/MascotaService.java
package com.sena.clinicaveterinaria.service;

import com.sena.clinicaveterinaria.model.Mascota;
import java.util.List;

public interface MascotaService {
    List<Mascota> listar();
    Mascota buscarPorId(Integer id);
    Mascota guardar(Mascota mascota);
    void eliminar(Integer id);
    List<Mascota> findMascotasByUsuarioEmail(String email);
}