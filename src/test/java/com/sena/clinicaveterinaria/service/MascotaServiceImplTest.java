package com.sena.clinicaveterinaria.service;

import com.sena.clinicaveterinaria.model.Cliente;
import com.sena.clinicaveterinaria.model.Mascota;
import com.sena.clinicaveterinaria.model.Usuario;
import com.sena.clinicaveterinaria.repository.MascotaRepository;
import com.sena.clinicaveterinaria.repository.UsuarioRepository;
import com.sena.clinicaveterinaria.service.impl.MascotaServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MascotaServiceImplTest {

    @Mock
    private MascotaRepository mascotaRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private MascotaServiceImpl mascotaService;

    private Mascota mascotaTest;
    private Usuario usuarioTest;
    private Cliente clienteTest;

    @BeforeEach
    void setUp() {
        clienteTest = new Cliente();
        clienteTest.setIdCliente(1);
        clienteTest.setNombre("Juan");

        mascotaTest = new Mascota();
        mascotaTest.setIdMascota(1);
        mascotaTest.setNombre("Firulais");
        mascotaTest.setEspecie(Mascota.Especie.Perro);
        mascotaTest.setRaza("Labrador");
        mascotaTest.setPeso(new BigDecimal("25.50"));
        mascotaTest.setCliente(clienteTest);

        usuarioTest = new Usuario();
        usuarioTest.setId(1);
        usuarioTest.setEmail("juan.perez@email.com");
        usuarioTest.setRol("CLIENTE");
        usuarioTest.setIdCliente(1);
        usuarioTest.setActivo(true);
    }

    @Test
    void listar_DebeRetornarListaDeMascotas() {
        // Arrange
        List<Mascota> mascotasEsperadas = Arrays.asList(mascotaTest, new Mascota());
        when(mascotaRepository.findAll()).thenReturn(mascotasEsperadas);

        // Act
        List<Mascota> resultado = mascotaService.listar();

        // Assert
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        verify(mascotaRepository, times(1)).findAll();
    }

    @Test
    void buscarPorId_CuandoExiste_DebeRetornarMascota() {
        // Arrange
        when(mascotaRepository.findById(1)).thenReturn(Optional.of(mascotaTest));

        // Act
        Mascota resultado = mascotaService.buscarPorId(1);

        // Assert
        assertNotNull(resultado);
        assertEquals("Firulais", resultado.getNombre());
        assertEquals(Mascota.Especie.Perro, resultado.getEspecie());
    }

    @Test
    void findMascotasByUsuarioEmail_CuandoUsuarioTieneMascotas_DebeRetornarLista() {
        // Arrange
        when(usuarioRepository.findByEmail("juan.perez@email.com")).thenReturn(usuarioTest);
        when(mascotaRepository.findByClienteIdCliente(1)).thenReturn(Arrays.asList(mascotaTest));

        // Act
        List<Mascota> resultado = mascotaService.findMascotasByUsuarioEmail("juan.perez@email.com");

        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("Firulais", resultado.get(0).getNombre());
    }

    @Test
    void findMascotasByUsuarioEmail_CuandoUsuarioNoExiste_DebeLanzarExcepcion() {
        // Arrange
        when(usuarioRepository.findByEmail("noexiste@email.com")).thenReturn(null);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> mascotaService.findMascotasByUsuarioEmail("noexiste@email.com"));

        assertTrue(exception.getMessage().contains("Usuario no encontrado"));
    }
}