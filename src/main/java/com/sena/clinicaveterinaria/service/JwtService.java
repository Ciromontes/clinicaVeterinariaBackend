// src/main/java/com/sena/clinicaveterinaria/service/JwtService.java
package com.sena.clinicaveterinaria.service;

import com.sena.clinicaveterinaria.model.Usuario;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secret;

    // ✅ Método para generar token
    public String generateToken(Usuario usuario) {
        SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));

        return Jwts.builder()
                .subject(usuario.getEmail())
                .claim("rol", usuario.getRol())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 86400000)) // 24 horas
                .signWith(key)  // ✅ Usa SecretKey
                .compact();
    }

    // ✅ Método para validar token (opcional, útil para debugging)
    public Claims validateToken(String token) {
        SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));

        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    // ✅ Extraer email del token
    public String extractEmail(String token) {
        return validateToken(token).getSubject();
    }

    // ✅ Extraer rol del token
    public String extractRol(String token) {
        return validateToken(token).get("rol", String.class);
    }
}