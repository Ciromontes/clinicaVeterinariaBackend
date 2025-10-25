package com.sena.clinicaveterinaria.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Value("${cors.allowed.origins:http://localhost:5173,http://localhost:3000,http://localhost:80,http://frontend:80}")
    private String allowedOrigins;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    /**
     * ✅ BEAN AGREGADO: PasswordEncoder para validar contraseñas
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                // DESHABILITAR formulario de login y logout por defecto
                .formLogin(form -> form.disable())
                .logout(logout -> logout.disable())
                .httpBasic(httpBasic -> httpBasic.disable())
                .authorizeHttpRequests(auth -> auth
                        // Endpoints de actuator PRIMERO (máxima prioridad)
                        .requestMatchers("/actuator/**").permitAll()
                        // Endpoints de autenticación
                        .requestMatchers("/api/auth/**").permitAll()

                        // ============================================
                        // ENDPOINTS DE CITAS
                        // ============================================
                        // Mis citas: Todos los roles autenticados
                        .requestMatchers("/api/citas/mis-citas").authenticated()
                        // Agendar cita: Todos los roles (principalmente CLIENTE)
                        .requestMatchers("/api/citas/agendar").authenticated()
                        // Citas de hoy: VETERINARIO y ADMIN
                        .requestMatchers("/api/citas/hoy").hasAnyRole("VETERINARIO", "ADMIN")
                        // Actualizar estado: SOLO VETERINARIO (es su responsabilidad)
                        .requestMatchers("/api/citas/*/estado").hasRole("VETERINARIO")
                        // Ver todas las citas (GET /api/citas): SOLO VETERINARIO y ADMIN
                        .requestMatchers("/api/citas").hasAnyRole("VETERINARIO", "ADMIN")
                        // Crear, actualizar, eliminar citas específicas: VETERINARIO y ADMIN
                        .requestMatchers("/api/citas/**").hasAnyRole("VETERINARIO", "ADMIN")

                        // ============================================
                        // ENDPOINTS DE HISTORIAS CLÍNICAS
                        // ============================================
                        // Ver historial completo: Todos los roles (con validación en el servicio)
                        .requestMatchers("/api/historias/mascota/*/completo").authenticated()
                        // Agregar entradas: VETERINARIO y ADMIN
                        .requestMatchers("/api/historias/**").hasAnyRole("VETERINARIO", "ADMIN")

                        // ============================================
                        // ENDPOINTS DE USUARIOS
                        // ============================================
                        // Gestionar estado de usuarios: SOLO ADMIN
                        .requestMatchers("/api/usuarios/*/estado").hasRole("ADMIN")
                        .requestMatchers("/api/usuarios/**").hasAnyRole("ADMIN", "VETERINARIO", "CLIENTE")

                        // ============================================
                        // OTROS ENDPOINTS DE API
                        // ============================================
                        // Mascotas, veterinarios, clientes, tratamientos: Requieren autenticación
                        .requestMatchers("/api/**").authenticated()

                        // ============================================
                        // FRONTEND Y ARCHIVOS ESTÁTICOS
                        // ============================================
                        .requestMatchers("/", "/index.html", "/assets/**", "/static/**", "/*.js", "/*.css", "/vite.svg").permitAll()
                        // Todo lo demás requiere autenticación
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Parsear los orígenes permitidos desde las variables de entorno
        List<String> origins = Arrays.asList(allowedOrigins.split(","));
        configuration.setAllowedOrigins(origins);

        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*")); // Permitir todos los headers
        configuration.setExposedHeaders(Arrays.asList("Authorization")); // Exponer el header de Authorization
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}