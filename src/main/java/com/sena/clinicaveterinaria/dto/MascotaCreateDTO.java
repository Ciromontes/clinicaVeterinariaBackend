package com.sena.clinicaveterinaria.dto;

import com.sena.clinicaveterinaria.model.Mascota;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class MascotaCreateDTO {
    private String nombre;
    private String especie;  // Se convertirá a Enum
    private String raza;
    private Integer edad;
    private BigDecimal peso;
    private String color;
    private String sexo;  // Se convertirá a Enum
    private Integer idCliente;  // ✅ Acepta el ID del cliente directamente
}

