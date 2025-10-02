// src/main/java/com/sena/clinicaveterinaria/model/Mascota.java
package com.sena.clinicaveterinaria.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "mascota")
@Data
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "cliente"})
public class Mascota {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_Mascota")
    private Integer idMascota;

    @Column(nullable = false, length = 50)
    private String nombre;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Especie especie;

    @Column(length = 50)
    private String raza;

    private Integer edad;

    @Column(precision = 5, scale = 2)
    private BigDecimal peso;

    @Column(length = 50)
    private String color;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Sexo sexo;

    @Column(name = "Fecha_Registro")
    private LocalDateTime fechaRegistro;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Estado estado = Estado.Activo;

    // ✅ ESTA ES LA RELACIÓN QUE FALTABA
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_Cliente", nullable = false)
    private Cliente cliente;

    // Enums
    public enum Especie {
        Perro, Gato, Ave, Conejo, Hamster, Reptil, Otro
    }

    public enum Sexo {
        Macho, Hembra
    }

    public enum Estado {
        Activo, Inactivo, Fallecido
    }

    @PrePersist
    protected void onCreate() {
        fechaRegistro = LocalDateTime.now();
    }
}