package com.sena.clinicaveterinaria.service.impl;

import com.sena.clinicaveterinaria.model.EntradaHistoria;
import com.sena.clinicaveterinaria.model.HistoriaClinica;
import com.sena.clinicaveterinaria.model.Mascota;
import com.sena.clinicaveterinaria.model.Usuario;
import com.sena.clinicaveterinaria.repository.EntradaHistoriaRepository;
import com.sena.clinicaveterinaria.repository.HistoriaClinicaRepository;
import com.sena.clinicaveterinaria.repository.MascotaRepository;
import com.sena.clinicaveterinaria.repository.UsuarioRepository;
import com.sena.clinicaveterinaria.service.HistoriaClinicaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class HistoriaClinicaServiceImpl implements HistoriaClinicaService {

    private static final Logger log = LoggerFactory.getLogger(HistoriaClinicaServiceImpl.class);

    private final HistoriaClinicaRepository historiaRepository;
    private final EntradaHistoriaRepository entradaRepository;
    private final UsuarioRepository usuarioRepository;
    private final MascotaRepository mascotaRepository;

    public HistoriaClinicaServiceImpl(
            HistoriaClinicaRepository historiaRepository,
            EntradaHistoriaRepository entradaRepository,
            UsuarioRepository usuarioRepository,
            MascotaRepository mascotaRepository) {
        this.historiaRepository = historiaRepository;
        this.entradaRepository = entradaRepository;
        this.usuarioRepository = usuarioRepository;
        this.mascotaRepository = mascotaRepository;
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
                    nuevaHistoria.setFechaCreacion(LocalDate.now());
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

    @Override
    public Map<String, Object> obtenerHistorialCompleto(Integer idMascota, String emailUsuario) {
        log.debug("Servicio: Obteniendo historial completo de mascota ID={} para usuario {}",
                idMascota, emailUsuario);

        // Validar que el usuario exista
        Usuario usuario = usuarioRepository.findByEmail(emailUsuario);
        if (usuario == null) {
            log.warn("Servicio: Usuario {} no encontrado", emailUsuario);
            throw new RuntimeException("Usuario no autorizado");
        }

        // Validar que la mascota exista
        Mascota mascota = mascotaRepository.findById(idMascota).orElse(null);
        if (mascota == null) {
            log.warn("Servicio: Mascota ID={} no encontrada", idMascota);
            throw new RuntimeException("Mascota no encontrada");
        }

        // Obtener el rol del usuario
        String rol = usuario.getRol();
        log.debug("Servicio: Usuario {} tiene rol {}", emailUsuario, rol);

        // VALIDACIÓN POR ROL:
        // - ADMIN: Puede ver TODAS las historias (supervisión, reportes)
        // - VETERINARIO: Puede ver TODAS las historias (atención médica)
        // - CLIENTE: Solo puede ver historias de SUS mascotas
        if ("CLIENTE".equals(rol)) {
            if (usuario.getIdCliente() == null) {
                log.warn("Servicio: Usuario {} es CLIENTE pero no tiene ID de cliente", emailUsuario);
                throw new RuntimeException("Usuario no autorizado");
            }

            if (!mascota.getCliente().getIdCliente().equals(usuario.getIdCliente())) {
                log.warn("Servicio: Cliente {} intenta acceder a historial de mascota que no le pertenece (Mascota ID={})",
                        emailUsuario, idMascota);
                throw new RuntimeException("No tiene permiso para ver esta mascota");
            }
            log.info("Servicio: CLIENTE {} accediendo a historial de su mascota ID={}", emailUsuario, idMascota);
        }
        else if ("VETERINARIO".equals(rol)) {
            log.info("Servicio: VETERINARIO {} accediendo a historial de mascota ID={} (acceso completo)", emailUsuario, idMascota);
        }
        else if ("ADMIN".equals(rol)) {
            log.info("Servicio: ADMIN {} accediendo a historial de mascota ID={} (acceso completo)", emailUsuario, idMascota);
        }
        else {
            log.warn("Servicio: Usuario {} tiene rol desconocido: {}", emailUsuario, rol);
            throw new RuntimeException("Usuario no autorizado");
        }

        // Obtener historia y entradas
        HistoriaClinica historia = obtenerPorMascota(idMascota);
        List<EntradaHistoria> entradas = obtenerEntradas(historia.getIdHistoria());

        log.info("Servicio: Historial completo obtenido - Mascota ID={}, Usuario={}, Rol={}, {} entradas encontradas",
                idMascota, emailUsuario, rol, entradas.size());

        return Map.of(
                "historia", historia,
                "entradas", entradas,
                "mascotaId", idMascota,
                "totalEntradas", entradas.size()
        );
    }
}