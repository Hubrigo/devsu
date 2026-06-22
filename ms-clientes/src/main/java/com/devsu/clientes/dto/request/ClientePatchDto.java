package com.devsu.clientes.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClientePatchDto {
    private String nombre;
    private String genero;
    private Integer edad;
    private String direccion;
    private String telefono;
    private String contrasena;
    private Boolean estado;
}
