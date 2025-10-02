// src/main/java/com/sena/clinicaveterinaria/model/Cliente.java
package com.sena.clinicaveterinaria.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "cliente")
@Data
public class Cliente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_Cliente")
    private Integer idCliente;

    @Column(nullable = false, length = 50)
    private String nombre;

    @Column(nullable = false, length = 50)
    private String apellidos;

    @Column(nullable = false, unique = true, length = 20)
    private String identificacion;

    @Column(nullable = false, length = 200)
    private String direccion;

    @Column(nullable = false, length = 15)
    private String telefono;

    @Column(unique = true, length = 100)
    private String correo;

    @Column(name = "Fecha_Registro")
    private LocalDateTime fechaRegistro;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Estado estado = Estado.Activo;

    // ✅ Relación bidireccional con Mascota
    @OneToMany(mappedBy = "cliente", fetch = FetchType.LAZY)
    @JsonIgnore  // Evita ciclos infinitos en JSON
    private List<Mascota> mascotas;

    public enum Estado {
        Activo, Inactivo
    }

    @PrePersist
    protected void onCreate() {
        fechaRegistro = LocalDateTime.now();
    }
}