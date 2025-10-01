// src/main/java/com/sena/clinicaveterinaria/controller/CitaController.java
package com.sena.clinicaveterinaria.controller;

import com.sena.clinicaveterinaria.model.Cita;
import com.sena.clinicaveterinaria.service.CitaService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/citas")
@CrossOrigin(origins = "*")
public class CitaController {
    private final CitaService service;

    public CitaController(CitaService service) {
        this.service = service;
    }

    @GetMapping
    public List<Cita> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    public Cita buscarPorId(@PathVariable Integer id) {
        return service.buscarPorId(id);
    }

    @PutMapping("/{id}")
    public Cita actualizar(@PathVariable Integer id, @RequestBody Cita cita) {
        cita.setId(id);
        return service.guardar(cita);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Integer id) {
        service.eliminar(id);
    }

    @PostMapping
    public Cita guardar(@RequestBody Cita cita) {
        return service.guardar(cita);
    }

    // ✅ AGREGAR estos nuevos endpoints
    @GetMapping("/mis-citas")
    public List<Cita> getMisCitas(Authentication authentication) {
        String username = authentication.getName();
        return service.findCitasByUsuarioEmail(username);
    }

    @PostMapping("/agendar")
    public Cita agendarCita(@RequestBody Cita cita, Authentication authentication) {
        String username = authentication.getName();
        return service.agendarCita(cita, username);
    }
}
