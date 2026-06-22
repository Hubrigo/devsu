package com.devsu.clientes.service;

import com.devsu.clientes.dto.request.ClientePatchDto;
import com.devsu.clientes.dto.request.ClienteRequestDto;
import com.devsu.clientes.dto.response.ClienteResponseDto;

import java.util.List;

public interface ClienteService {
    ClienteResponseDto crear(ClienteRequestDto dto);
    List<ClienteResponseDto> listar();
    ClienteResponseDto buscarPorId(Long id);
    ClienteResponseDto actualizar(Long id, ClienteRequestDto dto);
    ClienteResponseDto actualizarParcial(Long id, ClientePatchDto dto);
    void eliminar(Long id);
}
