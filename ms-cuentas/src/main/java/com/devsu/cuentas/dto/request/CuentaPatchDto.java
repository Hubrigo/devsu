package com.devsu.cuentas.dto.request;

import com.devsu.cuentas.entity.Cuenta.TipoCuenta;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CuentaPatchDto {
    private TipoCuenta tipo;
    private BigDecimal saldoInicial;
    private Boolean estado;
}
