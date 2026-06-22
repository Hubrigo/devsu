package com.devsu.cuentas.dto.request;

import com.devsu.cuentas.entity.Cuenta.TipoCuenta;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CuentaRequestDto {

    @NotBlank(message = "El número de cuenta es obligatorio")
    private String numeroCuenta;

    @NotNull(message = "El tipo de cuenta es obligatorio")
    private TipoCuenta tipo;

    @NotNull(message = "El saldo inicial es obligatorio")
    @DecimalMin(value = "0.0", message = "El saldo inicial no puede ser negativo")
    private BigDecimal saldoInicial;

    private Boolean estado = true;

    @NotNull(message = "El clienteId es obligatorio")
    private Long clienteId;
}
