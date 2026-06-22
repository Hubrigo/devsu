package com.devsu.clientes.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClienteRequestDto {

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    private String genero;

    @Min(value = 0, message = "La edad debe ser positiva")
    private Integer edad;

    @NotBlank(message = "La identificación es obligatoria")
    private String identificacion;

    private String direccion;

    private String telefono;

    @NotBlank(message = "La contraseña es obligatoria")
    private String contrasena;

    @Builder.Default
    private Boolean estado = true;
}
