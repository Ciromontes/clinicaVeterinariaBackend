package com.sena.clinicaveterinaria.service;

import com.sena.clinicaveterinaria.model.EntradaHistoria;
import com.sena.clinicaveterinaria.model.HistoriaClinica;

import java.util.List;

public interface HistoriaClinicaService {
    HistoriaClinica obtenerPorMascota(Integer idMascota);
    EntradaHistoria agregarEntrada(Integer idHistoria, EntradaHistoria entrada, String emailVeterinario);
    List<EntradaHistoria> obtenerEntradas(Integer idHistoria);
}