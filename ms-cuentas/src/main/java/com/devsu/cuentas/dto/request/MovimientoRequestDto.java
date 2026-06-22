package com.devsu.cuentas.dto.request;

import com.devsu.cuentas.entity.Movimiento.TipoMovimiento;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MovimientoRequestDto {

    @NotNull(message = "El tipo de movimiento es obligatorio")
    private TipoMovimiento tipoMovimiento;

    @NotNull(message = "El valor es obligatorio")
    @DecimalMin(value = "0.01", message = "El valor debe ser mayor a cero")
    private BigDecimal valor;

    @NotNull(message = "El cuentaId es obligatorio")
    private Long cuentaId;
}
