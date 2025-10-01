
// src/main/java/com/sena/clinicaveterinaria/model/Usuario.java
package com.sena.clinicaveterinaria.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "usuario")
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false, length = 255)
    private String password;

    @Column(nullable = false, length = 20)
    private String rol;

    @Column(nullable = false)
    private Boolean activo = true;

    // ✅ AGREGAR estos campos para las relaciones
    @Column(name = "id_cliente")
    private Integer idCliente;

    @Column(name = "id_veterinario")
    private Integer idVeterinario;
}