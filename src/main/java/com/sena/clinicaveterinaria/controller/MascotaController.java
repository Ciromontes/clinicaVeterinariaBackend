// src/main/java/com/sena/clinicaveterinaria/controller/MascotaController.java
package com.sena.clinicaveterinaria.controller;

import com.sena.clinicaveterinaria.model.Mascota;
import com.sena.clinicaveterinaria.service.MascotaService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mascotas")
@CrossOrigin(origins = "*")
public class MascotaController {
    private final MascotaService service;

    public MascotaController(MascotaService service) {
        this.service = service;
    }

    @GetMapping
    public List<Mascota> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    public Mascota buscarPorId(@PathVariable Integer id) {
        return service.buscarPorId(id);
    }

    // Nuevo endpoint para obtener las mascotas del cliente logueado
    @GetMapping("/mias")
    public List<Mascota> getMisMascotas(Authentication authentication) {
        String username = authentication.getName();
        return service.findMascotasByUsuarioEmail(username);
    }

    // src/main/java/com/sena/clinicaveterinaria/controller/MascotaController.java

    // java
    @PutMapping("/{id}")
    public Mascota actualizar(@PathVariable Integer id, @RequestBody Mascota mascota) {
        mascota.setIdMascota(id);
        return service.guardar(mascota);
    }


}