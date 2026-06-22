package com.devsu.cuentas.mapper;

import com.devsu.cuentas.dto.request.CuentaRequestDto;
import com.devsu.cuentas.dto.request.CuentaPatchDto;
import com.devsu.cuentas.dto.response.CuentaResponseDto;
import com.devsu.cuentas.entity.Cuenta;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface CuentaMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "saldoDisponible", source = "saldoInicial")
    @Mapping(target = "movimientos", ignore = true)
    Cuenta toEntity(CuentaRequestDto dto);

    CuentaResponseDto toResponseDto(Cuenta cuenta);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "numeroCuenta", ignore = true)
    @Mapping(target = "clienteId", ignore = true)
    @Mapping(target = "movimientos", ignore = true)
    @Mapping(target = "saldoDisponible", ignore = true)
    void patchEntity(@MappingTarget Cuenta cuenta, CuentaPatchDto dto);
}
