package com.sena.clinicaveterinaria.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sena.clinicaveterinaria.dto.LoginRequest;
import com.sena.clinicaveterinaria.model.Usuario;
import com.sena.clinicaveterinaria.service.JwtService;
import com.sena.clinicaveterinaria.service.UsuarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;  // ✅ IMPORT CORRECTO
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
@SuppressWarnings("removal")
@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)

class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean  // ✅ Usar @MockBean (no @MockitoBean)
    private UsuarioService usuarioService;

    @MockBean
    private JwtService jwtService;

    private Usuario usuarioTest;

    @BeforeEach
    void setUp() {
        usuarioTest = new Usuario();
        usuarioTest.setId(1);
        usuarioTest.setEmail("juan.perez@email.com");
        usuarioTest.setPassword("123456");
        usuarioTest.setRol("CLIENTE");
        usuarioTest.setActivo(true);
    }

    @Test
    void login_ConCredencialesValidas_DebeRetornarToken() throws Exception {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("juan.perez@email.com");
        loginRequest.setPassword("123456");

        when(usuarioService.buscarPorEmail("juan.perez@email.com")).thenReturn(usuarioTest);
        when(jwtService.generateToken(any(Usuario.class))).thenReturn("fake-jwt-token");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("fake-jwt-token"))
                .andExpect(jsonPath("$.rol").value("CLIENTE"));
    }

    @Test
    void login_ConEmailInvalido_DebeRetornar401() throws Exception {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("noexiste@email.com");
        loginRequest.setPassword("123456");

        when(usuarioService.buscarPorEmail("noexiste@email.com")).thenReturn(null);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void login_ConPasswordIncorrecta_DebeRetornar401() throws Exception {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("juan.perez@email.com");
        loginRequest.setPassword("password-incorrecta");

        when(usuarioService.buscarPorEmail("juan.perez@email.com")).thenReturn(usuarioTest);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized());
    }
}