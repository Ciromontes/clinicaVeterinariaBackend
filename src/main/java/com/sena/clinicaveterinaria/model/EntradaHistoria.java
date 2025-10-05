package com.sena.clinicaveterinaria.model;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entidad que representa una entrada en la historia clínica de una mascota.
 * Mapea a la tabla 'entradahistoria' en la base de datos.
 *
 * Cada entrada registra información médica de una consulta veterinaria,
 * incluyendo descripción del estado, signos vitales y observaciones.
 */
@Entity
@Table(name = "entradahistoria")
@Data
public class EntradaHistoria {

    /**
     * Identificador único de la entrada médica (clave primaria)
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_Entrada")
    private Integer idEntrada;

    /**
     * Referencia a la historia clínica a la que pertenece esta entrada
     */
    @Column(name = "ID_Historia", nullable = false)
    private Integer idHistoria;

    /**
     * Fecha en que se realizó la entrada médica
     * Nota: La BD usa DATE, no DATETIME
     */
    @Column(name = "Fecha_Entrada", nullable = false)
    private LocalDate fechaEntrada;

    /**
     * Descripción general de la consulta médica.
     * Incluye diagnóstico, motivo de consulta y hallazgos principales.
     * Campo obligatorio de tipo TEXT.
     */
    @Column(name = "Descripcion", nullable = false, columnDefinition = "TEXT")
    private String descripcion;

    /**
     * Observaciones adicionales, tratamiento prescrito o recomendaciones.
     * Campo opcional de tipo TEXT.
     */
    @Column(name = "Observaciones", columnDefinition = "TEXT")
    private String observaciones;

    /**
     * Peso actual de la mascota en kilogramos (ej: 25.50 kg)
     */
    @Column(name = "Peso_Actual", precision = 5, scale = 2)
    private BigDecimal pesoActual;

    /**
     * Temperatura corporal en grados Celsius (ej: 38.50 °C)
     */
    @Column(name = "Temperatura", precision = 4, scale = 2)
    private BigDecimal temperatura;

    /**
     * Frecuencia cardíaca en latidos por minuto (ej: 120 lpm)
     */
    @Column(name = "Frecuencia_Cardiaca")
    private Integer frecuenciaCardiaca;

    /**
     * Fecha y hora de registro automático en el sistema.
     * Se genera automáticamente por la BD con CURRENT_TIMESTAMP.
     */
    @Column(name = "Fecha_Registro", insertable = false, updatable = false)
    private LocalDateTime fechaRegistro;

    /**
     * Referencia al veterinario que realizó la entrada médica
     */
    @Column(name = "ID_Veterinario", nullable = false)
    private Integer idVeterinario;

    /**
     * Referencia opcional a un tratamiento específico
     */
    @Column(name = "ID_Tratamiento")
    private Integer idTratamiento;

    /**
     * Hook de JPA que se ejecuta antes de persistir la entidad.
     * Asigna la fecha actual si no se especificó.
     */
    @PrePersist
    protected void onCreate() {
        if (fechaEntrada == null) {
            fechaEntrada = LocalDate.now();
        }
    }
}