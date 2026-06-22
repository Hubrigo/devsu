package com.devsu.cuentas.mapper;

import com.devsu.cuentas.dto.response.MovimientoResponseDto;
import com.devsu.cuentas.entity.Movimiento;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface MovimientoMapper {

    @Mapping(target = "cuentaId", source = "cuenta.id")
    @Mapping(target = "numeroCuenta", source = "cuenta.numeroCuenta")
    MovimientoResponseDto toResponseDto(Movimiento movimiento);
}
