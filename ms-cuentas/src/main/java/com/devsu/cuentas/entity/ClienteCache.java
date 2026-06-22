package com.devsu.cuentas.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "clientes_cache")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClienteCache {

    @Id
    @Column(name = "cliente_id")
    private Long clienteId;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(nullable = false)
    private Boolean estado;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
