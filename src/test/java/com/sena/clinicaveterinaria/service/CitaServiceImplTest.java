package com.sena.clinicaveterinaria.service;

import com.sena.clinicaveterinaria.model.Cita;
import com.sena.clinicaveterinaria.repository.CitaRepository;
import com.sena.clinicaveterinaria.repository.MascotaRepository;
import com.sena.clinicaveterinaria.repository.UsuarioRepository;
import com.sena.clinicaveterinaria.service.impl.CitaServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CitaServiceImplTest {

    @Mock
    private CitaRepository citaRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private MascotaRepository mascotaRepository;

    @InjectMocks
    private CitaServiceImpl citaService;

    private Cita citaTest;

    @BeforeEach
    void setUp() {
        citaTest = new Cita();
        citaTest.setId(1);
        citaTest.setFechaCita(LocalDate.of(2025, 10, 15));
        citaTest.setHoraCita(LocalTime.of(10, 30));
        citaTest.setMotivo("Vacunación");
        citaTest.setEstadoCita("Programada");
        citaTest.setIdMascota(1);
        citaTest.setIdVeterinario(2);
    }

    @Test
    void listar_DebeRetornarListaDeCitas() {
        // Arrange
        List<Cita> citasEsperadas = Arrays.asList(citaTest, new Cita());
        when(citaRepository.findAll()).thenReturn(citasEsperadas);

        // Act
        List<Cita> resultado = citaService.listar();

        // Assert
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        verify(citaRepository, times(1)).findAll();
    }

    @Test
    void buscarPorId_CuandoExiste_DebeRetornarCita() {
        // Arrange
        when(citaRepository.findById(1)).thenReturn(Optional.of(citaTest));

        // Act
        Cita resultado = citaService.buscarPorId(1);

        // Assert
        assertNotNull(resultado);
        assertEquals("Vacunación", resultado.getMotivo());
        verify(citaRepository, times(1)).findById(1);
    }

    @Test
    void buscarPorId_CuandoNoExiste_DebeRetornarNull() {
        // Arrange
        when(citaRepository.findById(999)).thenReturn(Optional.empty());

        // Act
        Cita resultado = citaService.buscarPorId(999);

        // Assert
        assertNull(resultado);
        verify(citaRepository, times(1)).findById(999);
    }

    @Test
    void guardar_DebeGuardarYRetornarCita() {
        // Arrange
        when(citaRepository.save(any(Cita.class))).thenReturn(citaTest);

        // Act
        Cita resultado = citaService.guardar(citaTest);

        // Assert
        assertNotNull(resultado);
        assertEquals("Vacunación", resultado.getMotivo());
        verify(citaRepository, times(1)).save(any(Cita.class));
    }

    @Test
    void eliminar_CuandoExiste_DebeEliminarCita() {
        // Arrange
        when(citaRepository.existsById(1)).thenReturn(true);
        doNothing().when(citaRepository).deleteById(1);

        // Act & Assert
        assertDoesNotThrow(() -> citaService.eliminar(1));
        verify(citaRepository, times(1)).deleteById(1);
    }

    @Test
    void eliminar_CuandoNoExiste_DebeLanzarExcepcion() {
        // Arrange
        when(citaRepository.existsById(999)).thenReturn(false);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> citaService.eliminar(999));

        assertTrue(exception.getMessage().contains("no encontrada"));
        verify(citaRepository, never()).deleteById(999);
    }
}