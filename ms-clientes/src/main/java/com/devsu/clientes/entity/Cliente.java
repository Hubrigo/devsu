package com.devsu.clientes.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "clientes")
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class Cliente extends Persona {

    @Column(name = "cliente_id", unique = true, nullable = false, length = 36)
    private String clienteId;

    @NotBlank
    @Column(nullable = false)
    private String contrasena;

    @Builder.Default
    @Column(nullable = false)
    private Boolean estado = true;
}
