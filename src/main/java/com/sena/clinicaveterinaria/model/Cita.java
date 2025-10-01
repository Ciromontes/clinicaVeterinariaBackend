// src/main/java/com/sena/clinicaveterinaria/model/Cita.java
package com.sena.clinicaveterinaria.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Entity
@Table(name = "cita")
public class Cita {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_Cita")
    private Integer id; // ✅ Mantener como 'id' para consistencia

    @Column(name = "Fecha_Cita", nullable = false)
    private LocalDate fechaCita;

    @Column(name = "Hora_Cita", nullable = false)
    private LocalTime horaCita;

    @Column(name = "Duracion_Minutos")
    private Integer duracionMinutos = 30; // ✅ Valor por defecto

    @Column(name = "Motivo", nullable = false, length = 200)
    private String motivo;

    @Column(name = "Estado_Cita")
    private String estadoCita = "Programada"; // ✅ Valor por defecto

    @Column(name = "Observaciones")
    private String observaciones;

    @Column(name = "ID_Mascota", nullable = false)
    private Integer idMascota;

    @Column(name = "ID_Veterinario", nullable = false)
    private Integer idVeterinario;
}