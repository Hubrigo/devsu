package com.devsu.cuentas.dto.response;

import com.devsu.cuentas.entity.Cuenta.TipoCuenta;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CuentaResponseDto {
    private Long id;
    private String numeroCuenta;
    private TipoCuenta tipo;
    private BigDecimal saldoInicial;
    private BigDecimal saldoDisponible;
    private Boolean estado;
    private Long clienteId;
}
