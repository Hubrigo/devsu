package com.devsu.clientes.mapper;

import com.devsu.clientes.dto.request.ClienteRequestDto;
import com.devsu.clientes.dto.response.ClienteResponseDto;
import com.devsu.clientes.entity.Cliente;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface ClienteMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "clienteId", ignore = true)
    Cliente toEntity(ClienteRequestDto dto);

    ClienteResponseDto toResponseDto(Cliente cliente);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "clienteId", ignore = true)
    @Mapping(target = "identificacion", ignore = true)
    void patchEntity(@MappingTarget Cliente cliente,
                     com.devsu.clientes.dto.request.ClientePatchDto dto);
}
