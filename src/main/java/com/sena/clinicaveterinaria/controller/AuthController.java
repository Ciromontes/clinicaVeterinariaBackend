package com.sena.clinicaveterinaria.controller;

import com.sena.clinicaveterinaria.dto.LoginRequest;
import com.sena.clinicaveterinaria.dto.AuthResponse;
import com.sena.clinicaveterinaria.model.Usuario;
import com.sena.clinicaveterinaria.service.JwtService;
import com.sena.clinicaveterinaria.service.UsuarioService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    private final UsuarioService usuarioService;
    private final JwtService jwtService;

    public AuthController(UsuarioService usuarioService, JwtService jwtService) {
        this.usuarioService = usuarioService;
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        log.info("Intento de login para email: {}", loginRequest.getEmail());

        try {
            Usuario usuario = usuarioService.buscarPorEmail(loginRequest.getEmail());

            if (usuario == null) {
                log.warn("Login fallido - Usuario no encontrado: {}", loginRequest.getEmail());
                return ResponseEntity.status(401).body("Credenciales inválidas");
            }

            if (!usuario.getPassword().equals(loginRequest.getPassword())) {
                log.warn("Login fallido - Contraseña incorrecta para: {}", loginRequest.getEmail());
                return ResponseEntity.status(401).body("Credenciales inválidas");
            }

            if (!usuario.getActivo()) {
                log.warn("Login fallido - Usuario inactivo: {}", loginRequest.getEmail());
                return ResponseEntity.status(403).body("Usuario inactivo");
            }

            String token = jwtService.generateToken(usuario);
            log.info("Login exitoso - Usuario: {} | Rol: {}", usuario.getEmail(), usuario.getRol());

            return ResponseEntity.ok(new AuthResponse(token, usuario.getRol()));

        } catch (Exception e) {
            log.error("Error inesperado durante login para {}: {}", loginRequest.getEmail(), e.getMessage(), e);
            return ResponseEntity.status(500).body("Error interno del servidor");
        }
    }
}