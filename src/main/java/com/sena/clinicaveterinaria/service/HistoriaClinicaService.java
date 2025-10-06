package com.sena.clinicaveterinaria.service;

import com.sena.clinicaveterinaria.model.EntradaHistoria;
import com.sena.clinicaveterinaria.model.HistoriaClinica;

import java.util.List;
import java.util.Map;

public interface HistoriaClinicaService {
    HistoriaClinica obtenerPorMascota(Integer idMascota);
    EntradaHistoria agregarEntrada(Integer idHistoria, EntradaHistoria entrada, String emailVeterinario);
    List<EntradaHistoria> obtenerEntradas(Integer idHistoria);

    /**
     * Obtiene el historial completo de una mascota (historia + entradas)
     * Valida que la mascota pertenezca al cliente autenticado
     * @param idMascota ID de la mascota
     * @param emailUsuario Email del usuario autenticado
     * @return Mapa con "historia" y "entradas"
     */
    Map<String, Object> obtenerHistorialCompleto(Integer idMascota, String emailUsuario);
}