// src/main/java/com/sena/clinicaveterinaria/model/Mascota.java
package com.sena.clinicaveterinaria.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "mascota")
public class Mascota {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_Mascota")
    private Integer id;

    @Column(name = "Nombre")
    private String nombre;

    @Column(name = "Especie")
    private String especie;

    @Column(name = "Raza")
    private String raza;

    @Column(name = "Edad")
    private Integer edad;

    @Column(name = "Peso")
    private Double peso;

    @Column(name = "Color")
    private String color;

    @Column(name = "Sexo")
    private String sexo;

    @Column(name = "Fecha_Registro")
    private java.sql.Timestamp fechaRegistro;

    @Column(name = "Estado")
    private String estado;

    @Column(name = "ID_Cliente")
    private Integer idCliente;
}