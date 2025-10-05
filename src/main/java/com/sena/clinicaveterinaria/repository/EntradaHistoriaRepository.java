package com.sena.clinicaveterinaria.repository;

import com.sena.clinicaveterinaria.model.EntradaHistoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio para gestionar operaciones de base de datos
 * sobre la entidad EntradaHistoria.
 *
 * Spring Data JPA genera automáticamente la implementación
 * de los métodos de consulta basándose en su nombre.
 */
@Repository
public interface EntradaHistoriaRepository extends JpaRepository<EntradaHistoria, Integer> {

    /**
     * Busca todas las entradas médicas de una historia clínica específica
     * y las ordena de más reciente a más antigua.
     *
     * @param idHistoria ID de la historia clínica
     * @return Lista de entradas ordenadas por fecha descendente
     */
    List<EntradaHistoria> findByIdHistoriaOrderByFechaEntradaDesc(Integer idHistoria);
}