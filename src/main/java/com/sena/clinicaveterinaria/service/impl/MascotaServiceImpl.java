package com.sena.clinicaveterinaria.service.impl;

import com.sena.clinicaveterinaria.model.HistoriaClinica;
import com.sena.clinicaveterinaria.model.Mascota;
import com.sena.clinicaveterinaria.model.Usuario;
import com.sena.clinicaveterinaria.repository.HistoriaClinicaRepository;
import com.sena.clinicaveterinaria.repository.MascotaRepository;
import com.sena.clinicaveterinaria.repository.UsuarioRepository;
import com.sena.clinicaveterinaria.service.MascotaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class MascotaServiceImpl implements MascotaService {

    private static final Logger log = LoggerFactory.getLogger(MascotaServiceImpl.class);

    private final MascotaRepository mascotaRepository;
    private final UsuarioRepository usuarioRepository;
    private final HistoriaClinicaRepository historiaClinicaRepository;

    public MascotaServiceImpl(MascotaRepository mascotaRepository,
                              UsuarioRepository usuarioRepository,
                              HistoriaClinicaRepository historiaClinicaRepository) {
        this.mascotaRepository = mascotaRepository;
        this.usuarioRepository = usuarioRepository;
        this.historiaClinicaRepository = historiaClinicaRepository;
    }

    @Override
    public List<Mascota> listar() {
        log.debug("Servicio: Listando todas las mascotas");
        List<Mascota> mascotas = mascotaRepository.findAll();
        log.debug("Servicio: {} mascotas encontradas en BD", mascotas.size());
        return mascotas;
    }

    @Override
    public Mascota buscarPorId(Integer id) {
        log.debug("Servicio: Buscando mascota con ID={}", id);
        Mascota mascota = mascotaRepository.findById(id).orElse(null);
        if (mascota != null) {
            log.debug("Servicio: Mascota encontrada - ID={}, Nombre={}, Especie={}",
                    id, mascota.getNombre(), mascota.getEspecie());
        } else {
            log.warn("Servicio: Mascota no encontrada con ID={}", id);
        }
        return mascota;
    }

    @Override
    public Mascota guardar(Mascota mascota) {
        log.debug("Servicio: Guardando mascota - Nombre={}, Especie={}",
                mascota.getNombre(), mascota.getEspecie());

        // Verificar si es una mascota nueva (sin ID)
        boolean esMascotaNueva = (mascota.getIdMascota() == null);

        // Guardar la mascota
        Mascota mascotaGuardada = mascotaRepository.save(mascota);
        log.info("Servicio: Mascota guardada exitosamente - ID={}, Nombre={}",
                mascotaGuardada.getIdMascota(), mascotaGuardada.getNombre());

        // Si es una mascota nueva, crear su historia clínica automáticamente
        if (esMascotaNueva) {
            try {
                HistoriaClinica historiaClinica = new HistoriaClinica();
                historiaClinica.setIdMascota(mascotaGuardada.getIdMascota());
                // fechaCreacion se establece automáticamente con @PrePersist
                historiaClinicaRepository.save(historiaClinica);
                log.info("Servicio: Historia clínica creada automáticamente para mascota ID={}",
                        mascotaGuardada.getIdMascota());
            } catch (Exception e) {
                log.error("Servicio: Error al crear historia clínica para mascota ID={}: {}",
                        mascotaGuardada.getIdMascota(), e.getMessage());
                // No lanzamos la excepción para no bloquear la creación de la mascota
            }
        }

        return mascotaGuardada;
    }

    @Override
    public void eliminar(Integer id) {
        log.debug("Servicio: Eliminando mascota ID={}", id);
        if (!mascotaRepository.existsById(id)) {
            log.warn("Servicio: Intento de eliminar mascota inexistente ID={}", id);
            throw new RuntimeException("Mascota no encontrada con ID: " + id);
        }
        mascotaRepository.deleteById(id);
        log.info("Servicio: Mascota eliminada exitosamente - ID={}", id);
    }

    @Override
    public List<Mascota> findMascotasByUsuarioEmail(String email) {
        log.debug("Servicio: Buscando mascotas del usuario: {}", email);

        Usuario usuario = usuarioRepository.findByEmail(email);
        if (usuario == null) {
            log.warn("Servicio: Usuario no encontrado: {}", email);
            throw new RuntimeException("Usuario no encontrado");
        }

        if (usuario.getIdCliente() == null) {
            log.warn("Servicio: Usuario {} no tiene cliente asociado", email);
            throw new RuntimeException("Usuario no tiene cliente asociado");
        }

        // ✅ CORREGIDO: Usar el método correcto del repositorio
        List<Mascota> mascotas = mascotaRepository.findByClienteIdCliente(usuario.getIdCliente());
        log.info("Servicio: {} mascotas encontradas para usuario {}", mascotas.size(), email);

        if (mascotas.isEmpty()) {
            log.debug("Servicio: Usuario {} no tiene mascotas registradas", email);
        }

        return mascotas;
    }
}