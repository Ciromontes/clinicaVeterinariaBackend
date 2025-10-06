package com.sena.clinicaveterinaria.service;

import java.util.Map;

/**
 * Servicio para funcionalidades administrativas
 */
public interface AdminService {
    
    /**
     * Obtiene las métricas del sistema para el dashboard administrativo
     * 
     * @return Map con las siguientes claves:
     *         - citasMes: número de citas del mes actual
     *         - mascotasActivas: número de mascotas con estado "Activo"
     *         - productosMinimos: número de productos con stock mínimo (placeholder)
     */
    Map<String, Object> obtenerMetricas();
}
