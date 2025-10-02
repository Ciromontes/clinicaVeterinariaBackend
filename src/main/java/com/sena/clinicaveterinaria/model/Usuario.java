// src/main/java/com/sena/clinicaveterinaria/model/Usuario.java
package com.sena.clinicaveterinaria.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "usuario")
@Data
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String rol; // "ADMIN", "VETERINARIO", "CLIENTE"

    @Column(nullable = false)
    private Boolean activo = true;

    // ✅ Relación con Cliente
    @Column(name = "id_cliente")
    private Integer idCliente;

    // ✅ Relación con Veterinario
    @Column(name = "id_veterinario")
    private Integer idVeterinario;
}