package com.devsu.clientes.messaging;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClienteEvent implements Serializable {
    private Long id;
    private String clienteId;
    private String nombre;
    private Boolean estado;
    private String eventType; // CREATED, UPDATED, DELETED
}
