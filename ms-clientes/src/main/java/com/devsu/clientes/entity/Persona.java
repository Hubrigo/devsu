package com.devsu.clientes.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.SuperBuilder;

@MappedSuperclass
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public abstract class Persona {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(length = 20)
    private String genero;

    private Integer edad;

    @Column(unique = true, nullable = false, length = 20)
    private String identificacion;

    @Column(length = 200)
    private String direccion;

    @Column(length = 20)
    private String telefono;
}
