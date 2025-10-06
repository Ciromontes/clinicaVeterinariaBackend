package com.sena.clinicaveterinaria.controller;

import com.sena.clinicaveterinaria.service.AdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Controlador para funcionalidades administrativas
 * Solo accesible por usuarios con rol ADMIN
 */
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    /**
     * Obtiene las métricas del dashboard administrativo
     * 
     * @return JSON con métricas: citasMes, mascotasActivas, productosMinimos
     */
    @GetMapping("/metricas")
    public ResponseEntity<Map<String, Object>> obtenerMetricas() {
        Map<String, Object> metricas = adminService.obtenerMetricas();
        return ResponseEntity.ok(metricas);
    }
}
