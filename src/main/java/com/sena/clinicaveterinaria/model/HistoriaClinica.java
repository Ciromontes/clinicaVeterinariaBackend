package com.sena.clinicaveterinaria.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Entity
@Table(name = "historiaclinica")
@Data
public class HistoriaClinica {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_Historia")
    private Integer idHistoria;

    @Column(name = "Fecha_Creacion", nullable = false)
    private LocalDate fechaCreacion;

    @Column(name = "ID_Mascota", nullable = false)
    private Integer idMascota;

    @PrePersist
    protected void onCreate() {
        if (fechaCreacion == null) {
            fechaCreacion = LocalDate.now();
        }
    }
}