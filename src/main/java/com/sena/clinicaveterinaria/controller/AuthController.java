package com.sena.clinicaveterinaria.controller;

import com.sena.clinicaveterinaria.dto.LoginRequest;
import com.sena.clinicaveterinaria.dto.AuthResponse;
import com.sena.clinicaveterinaria.model.Usuario;
import com.sena.clinicaveterinaria.service.JwtService;
import com.sena.clinicaveterinaria.service.UsuarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {
    private final UsuarioService usuarioService;
    private final JwtService jwtService;

    public AuthController(UsuarioService usuarioService, JwtService jwtService) {
        this.usuarioService = usuarioService;
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        Usuario usuario = usuarioService.buscarPorEmail(loginRequest.getEmail());
        if (usuario != null && usuario.getPassword().equals(loginRequest.getPassword())) {
            String token = jwtService.generateToken(usuario);
            return ResponseEntity.ok(new AuthResponse(token, usuario.getRol()));
        }
        return ResponseEntity.status(401).body("Credenciales inválidas");
    }
}