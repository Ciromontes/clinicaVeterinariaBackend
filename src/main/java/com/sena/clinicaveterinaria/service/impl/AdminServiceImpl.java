package com.sena.clinicaveterinaria.service.impl;

import com.sena.clinicaveterinaria.model.Mascota;
import com.sena.clinicaveterinaria.repository.CitaRepository;
import com.sena.clinicaveterinaria.repository.MascotaRepository;
import com.sena.clinicaveterinaria.service.AdminService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.Map;

/**
 * Implementación del servicio administrativo
 * Calcula métricas en tiempo real del sistema
 */
@Service
public class AdminServiceImpl implements AdminService {

    private final CitaRepository citaRepository;
    private final MascotaRepository mascotaRepository;

    public AdminServiceImpl(CitaRepository citaRepository, MascotaRepository mascotaRepository) {
        this.citaRepository = citaRepository;
        this.mascotaRepository = mascotaRepository;
    }

    @Override
    public Map<String, Object> obtenerMetricas() {
        Map<String, Object> metricas = new HashMap<>();

        // 1. Contar citas del mes actual
        long citasMes = contarCitasDelMesActual();
        metricas.put("citasMes", citasMes);

        // 2. Contar mascotas activas
        long mascotasActivas = contarMascotasActivas();
        metricas.put("mascotasActivas", mascotasActivas);

        // 3. Productos con stock mínimo (placeholder - se implementará en fase de inventario)
        metricas.put("productosMinimos", 3);

        return metricas;
    }

    /**
     * Cuenta las citas del mes actual
     */
    private long contarCitasDelMesActual() {
        YearMonth mesActual = YearMonth.now();
        LocalDate primerDia = mesActual.atDay(1);
        LocalDate ultimoDia = mesActual.atEndOfMonth();

        return citaRepository.findAll().stream()
                .filter(cita -> !cita.getFechaCita().isBefore(primerDia) 
                             && !cita.getFechaCita().isAfter(ultimoDia))
                .count();
    }

    /**
     * Cuenta las mascotas con estado "Activo"
     */
    private long contarMascotasActivas() {
        
        return mascotaRepository.findAll().stream()
                .filter(mascota -> mascota.getEstado() == Mascota.Estado.Activo)
                .count();
    }
}
