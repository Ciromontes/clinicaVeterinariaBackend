package com.sena.clinicaveterinaria.service.impl;

import com.sena.clinicaveterinaria.model.Cita;
import com.sena.clinicaveterinaria.model.Mascota;
import com.sena.clinicaveterinaria.model.Usuario;
import com.sena.clinicaveterinaria.repository.CitaRepository;
import com.sena.clinicaveterinaria.repository.MascotaRepository;
import com.sena.clinicaveterinaria.repository.UsuarioRepository;
import com.sena.clinicaveterinaria.service.CitaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class CitaServiceImpl implements CitaService {

    private static final Logger log = LoggerFactory.getLogger(CitaServiceImpl.class);

    private final CitaRepository citaRepository;
    private final UsuarioRepository usuarioRepository;
    private final MascotaRepository mascotaRepository;

    public CitaServiceImpl(CitaRepository citaRepository,
                           UsuarioRepository usuarioRepository,
                           MascotaRepository mascotaRepository) {
        this.citaRepository = citaRepository;
        this.usuarioRepository = usuarioRepository;
        this.mascotaRepository = mascotaRepository;
    }

    @Override
    public List<Cita> listar() {
        log.debug("Servicio: Listando todas las citas");
        List<Cita> citas = citaRepository.findAll();
        log.debug("Servicio: {} citas encontradas en BD", citas.size());
        return citas;
    }

    @Override
    public Cita buscarPorId(Integer id) {
        log.debug("Servicio: Buscando cita con ID={}", id);
        Cita cita = citaRepository.findById(id).orElse(null);
        if (cita != null) {
            log.debug("Servicio: Cita encontrada - ID={}, Estado={}", id, cita.getEstadoCita());
        } else {
            log.warn("Servicio: Cita no encontrada con ID={}", id);
        }
        return cita;
    }

    @Override
    public Cita guardar(Cita cita) {
        log.debug("Servicio: Guardando cita - Mascota={}, Veterinario={}, Fecha={}",
                cita.getIdMascota(), cita.getIdVeterinario(), cita.getFechaCita());

        Cita citaGuardada = citaRepository.save(cita);
        log.info("Servicio: Cita guardada exitosamente - ID={}", citaGuardada.getId());
        return citaGuardada;
    }

    @Override
    public void eliminar(Integer id) {
        log.debug("Servicio: Eliminando cita ID={}", id);
        if (!citaRepository.existsById(id)) {
            log.warn("Servicio: Intento de eliminar cita inexistente ID={}", id);
            throw new RuntimeException("Cita no encontrada con ID: " + id);
        }
        citaRepository.deleteById(id);
        log.info("Servicio: Cita eliminada exitosamente - ID={}", id);
    }

    @Override
    public List<Cita> findCitasByUsuarioEmail(String email) {
        log.debug("Servicio: Buscando citas del usuario: {}", email);

        Usuario usuario = usuarioRepository.findByEmail(email);
        if (usuario == null) {
            log.warn("Servicio: Usuario no encontrado: {}", email);
            throw new RuntimeException("Usuario no encontrado");
        }

        if (usuario.getIdCliente() == null) {
            log.warn("Servicio: Usuario {} no tiene cliente asociado", email);
            throw new RuntimeException("Usuario no tiene cliente asociado");
        }

        List<Mascota> mascotas = mascotaRepository.findByClienteIdCliente(usuario.getIdCliente());
        log.debug("Servicio: Usuario {} tiene {} mascotas registradas", email, mascotas.size());

        if (mascotas.isEmpty()) {
            log.debug("Servicio: No se encontraron mascotas para cliente ID={}", usuario.getIdCliente());
            return List.of();
        }

        List<Integer> mascotaIds = mascotas.stream()
                .map(Mascota::getIdMascota)
                .toList();

        List<Cita> citas = citaRepository.findByIdMascotaIn(mascotaIds);
        log.info("Servicio: {} citas encontradas para usuario {}", citas.size(), email);

        return citas;
    }

    @Override
    public Cita agendarCita(Cita cita, String emailUsuario) {
        log.debug("Servicio: Agendando cita para usuario: {}", emailUsuario);

        Usuario usuario = usuarioRepository.findByEmail(emailUsuario);
        if (usuario == null || usuario.getIdCliente() == null) {
            log.warn("Servicio: Usuario {} no válido para agendar cita", emailUsuario);
            throw new RuntimeException("Usuario no autorizado para agendar citas");
        }

        Mascota mascota = mascotaRepository.findById(cita.getIdMascota()).orElse(null);
        if (mascota == null) {
            log.warn("Servicio: Mascota ID={} no encontrada", cita.getIdMascota());
            throw new RuntimeException("Mascota no encontrada");
        }

        if (!mascota.getCliente().getIdCliente().equals(usuario.getIdCliente())) {
            log.warn("Servicio: Usuario {} intenta agendar cita para mascota que no le pertenece (Mascota ID={})",
                    emailUsuario, cita.getIdMascota());
            throw new RuntimeException("La mascota no pertenece al usuario");
        }

        cita.setEstadoCita("Programada");

        Cita citaAgendada = citaRepository.save(cita);
        log.info("Servicio: Cita agendada exitosamente - ID={}, Usuario={}, Mascota={}, Fecha={}",
                citaAgendada.getId(), emailUsuario, mascota.getNombre(), cita.getFechaCita());

        return citaAgendada;
    }

    // ✅ NUEVO MÉTODO: Obtener citas del día para veterinario
    @Override
    public List<Cita> findCitasHoyByVeterinario(String emailVeterinario) {
        log.debug("Servicio: Buscando citas del día para veterinario: {}", emailVeterinario);

        Usuario usuario = usuarioRepository.findByEmail(emailVeterinario);
        if (usuario == null) {
            log.warn("Servicio: Veterinario no encontrado: {}", emailVeterinario);
            throw new RuntimeException("Veterinario no encontrado");
        }

        if (usuario.getIdVeterinario() == null) {
            log.warn("Servicio: Usuario {} no tiene perfil de veterinario", emailVeterinario);
            throw new RuntimeException("Usuario no tiene perfil de veterinario");
        }

        LocalDate hoy = LocalDate.now();
        List<Cita> citas = citaRepository.findByIdVeterinarioAndFechaCita(usuario.getIdVeterinario(), hoy);

        log.info("Servicio: {} citas encontradas para veterinario {} en fecha {}",
                citas.size(), emailVeterinario, hoy);

        return citas;
    }

    // ✅ NUEVO MÉTODO: Actualizar estado de cita
    @Override
    public Cita actualizarEstado(Integer idCita, String nuevoEstado, String emailVeterinario) {
        log.debug("Servicio: Actualizando estado de cita ID={} a '{}' por veterinario {}",
                idCita, nuevoEstado, emailVeterinario);

        Usuario usuario = usuarioRepository.findByEmail(emailVeterinario);
        if (usuario == null || usuario.getIdVeterinario() == null) {
            log.warn("Servicio: Veterinario no válido: {}", emailVeterinario);
            throw new RuntimeException("Usuario no autorizado");
        }

        Cita cita = citaRepository.findById(idCita).orElse(null);
        if (cita == null) {
            log.warn("Servicio: Cita no encontrada con ID={}", idCita);
            throw new RuntimeException("Cita no encontrada");
        }

        if (!cita.getIdVeterinario().equals(usuario.getIdVeterinario())) {
            log.warn("Servicio: Veterinario {} intenta modificar cita que no le pertenece (Cita ID={})",
                    emailVeterinario, idCita);
            throw new RuntimeException("No puede modificar citas de otro veterinario");
        }

        // Validar transición de estado
        if ("Completada".equals(nuevoEstado) && !"Programada".equals(cita.getEstadoCita())) {
            log.warn("Servicio: Transición de estado inválida de '{}' a '{}' para cita ID={}",
                    cita.getEstadoCita(), nuevoEstado, idCita);
            throw new RuntimeException("Solo se pueden completar citas programadas");
        }

        cita.setEstadoCita(nuevoEstado);
        Cita citaActualizada = citaRepository.save(cita);

        log.info("Servicio: Estado de cita ID={} actualizado a '{}' por veterinario {}",
                idCita, nuevoEstado, emailVeterinario);

        return citaActualizada;
    }
}