package com.sena.clinicaveterinaria.config;

import com.sena.clinicaveterinaria.model.Usuario;
import com.sena.clinicaveterinaria.service.JwtService;
import com.sena.clinicaveterinaria.service.UsuarioService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final UsuarioService usuarioService;
    private final JwtService jwtService;

    public JwtAuthenticationFilter(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
        this.jwtService = new JwtService();
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String requestPath = request.getRequestURI();
        String method = request.getMethod();

        // Log inicial de la petición
        log.debug("🔍 Filtro JWT - Procesando: {} {}", method, requestPath);

        String authHeader = request.getHeader("Authorization");

        // Permitir endpoints públicos
        if (requestPath.equals("/api/auth/login") || !requestPath.startsWith("/api/")) {
            log.debug("✅ Endpoint público permitido: {}", requestPath);
            filterChain.doFilter(request, response);
            return;
        }

        // Validar presencia de token
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("⚠️ Token ausente o formato inválido en: {} {}", method, requestPath);
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String token = authHeader.substring(7);
            String tokenPreview = token.length() > 20
                    ? token.substring(0, 10) + "..." + token.substring(token.length() - 10)
                    : "***";

            log.debug("🔑 Token recibido: {}", tokenPreview);

            // Extraer información del token
            String email = jwtService.extractEmail(token);
            String rol = jwtService.extractRol(token);

            log.debug("👤 Usuario autenticado: {} | Rol: {}", email, rol);

            // Buscar usuario en BD
            Usuario usuario = usuarioService.buscarPorEmail(email);

            if (usuario != null && usuario.getActivo()) {
                log.debug("✅ Usuario válido y activo: {}", email);

                // Crear autenticación
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                email,
                                null,
                                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + rol))
                        );

                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);

                log.info("🔐 Autenticación exitosa: {} accediendo a {} {}", email, method, requestPath);
            } else {
                log.warn("⚠️ Usuario inválido o inactivo: {}", email);
            }

        } catch (Exception e) {
            log.error("❌ Error procesando token: {}", e.getMessage());
            log.debug("Stack trace:", e);
        }

        filterChain.doFilter(request, response);
    }
}