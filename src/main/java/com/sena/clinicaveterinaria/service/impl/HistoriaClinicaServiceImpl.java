package com.sena.clinicaveterinaria.service.impl;

import com.sena.clinicaveterinaria.model.EntradaHistoria;
import com.sena.clinicaveterinaria.model.HistoriaClinica;
import com.sena.clinicaveterinaria.model.Usuario;
import com.sena.clinicaveterinaria.repository.EntradaHistoriaRepository;
import com.sena.clinicaveterinaria.repository.HistoriaClinicaRepository;
import com.sena.clinicaveterinaria.repository.UsuarioRepository;
import com.sena.clinicaveterinaria.service.HistoriaClinicaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class HistoriaClinicaServiceImpl implements HistoriaClinicaService {

    private static final Logger log = LoggerFactory.getLogger(HistoriaClinicaServiceImpl.class);

    private final HistoriaClinicaRepository historiaRepository;
    private final EntradaHistoriaRepository entradaRepository;
    private final UsuarioRepository usuarioRepository;

    public HistoriaClinicaServiceImpl(
            HistoriaClinicaRepository historiaRepository,
            EntradaHistoriaRepository entradaRepository,
            UsuarioRepository usuarioRepository) {
        this.historiaRepository = historiaRepository;
        this.entradaRepository = entradaRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public HistoriaClinica obtenerPorMascota(Integer idMascota) {
        log.debug("Servicio: Buscando historia clínica para mascota ID={}", idMascota);

        // Buscar historia existente o crear una nueva si no existe
        HistoriaClinica historia = historiaRepository.findByIdMascota(idMascota)
                .orElseGet(() -> {
                    log.info("Servicio: Historia clínica no existe para mascota ID={}, creando nueva", idMascota);
                    HistoriaClinica nuevaHistoria = new HistoriaClinica();
                    nuevaHistoria.setIdMascota(idMascota);
                    nuevaHistoria.setFechaCreacion(LocalDateTime.now());
                    return historiaRepository.save(nuevaHistoria);
                });

        log.info("Servicio: Historia clínica obtenida - ID={}, Mascota={}",
                historia.getIdHistoria(), idMascota);

        return historia;
    }

    @Override
    public EntradaHistoria agregarEntrada(Integer idHistoria, EntradaHistoria entrada, String emailVeterinario) {
        log.debug("Servicio: Agregando entrada a historia ID={} por veterinario {}",
                idHistoria, emailVeterinario);

        // Validar que el usuario sea un veterinario válido
        Usuario usuario = usuarioRepository.findByEmail(emailVeterinario);
        if (usuario == null || usuario.getIdVeterinario() == null) {
            log.warn("Servicio: Usuario {} no es veterinario válido", emailVeterinario);
            throw new RuntimeException("Usuario no autorizado para agregar entradas médicas");
        }

        // Validar que la historia clínica exista
        HistoriaClinica historia = historiaRepository.findById(idHistoria).orElse(null);
        if (historia == null) {
            log.warn("Servicio: Historia clínica ID={} no encontrada", idHistoria);
            throw new RuntimeException("Historia clínica no encontrada");
        }

        // Configurar los campos obligatorios de la entrada
        entrada.setIdHistoria(idHistoria);
        entrada.setIdVeterinario(usuario.getIdVeterinario());
        entrada.setFechaEntrada(LocalDate.now()); // Fecha actual

        // Guardar la entrada en la base de datos
        EntradaHistoria entradaGuardada = entradaRepository.save(entrada);

        log.info("Servicio: Entrada médica agregada - ID={}, Historia={}, Veterinario ID={}, Signos vitales: Peso={}, Temp={}, FC={}",
                entradaGuardada.getIdEntrada(),
                idHistoria,
                usuario.getIdVeterinario(),
                entrada.getPesoActual(),
                entrada.getTemperatura(),
                entrada.getFrecuenciaCardiaca());

        return entradaGuardada;
    }

    @Override
    public List<EntradaHistoria> obtenerEntradas(Integer idHistoria) {
        log.debug("Servicio: Obteniendo entradas de historia ID={}", idHistoria);

        // Obtener todas las entradas ordenadas de más reciente a más antigua
        List<EntradaHistoria> entradas = entradaRepository.findByIdHistoriaOrderByFechaEntradaDesc(idHistoria);

        log.info("Servicio: {} entradas encontradas para historia ID={}", entradas.size(), idHistoria);

        return entradas;
    }
}