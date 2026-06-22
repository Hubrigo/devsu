package com.devsu.cuentas.service;

import com.devsu.cuentas.dto.request.CuentaPatchDto;
import com.devsu.cuentas.dto.request.CuentaRequestDto;
import com.devsu.cuentas.dto.response.CuentaResponseDto;

import java.util.List;

public interface CuentaService {
    CuentaResponseDto crear(CuentaRequestDto dto);
    List<CuentaResponseDto> listar();
    CuentaResponseDto buscarPorId(Long id);
    CuentaResponseDto actualizar(Long id, CuentaRequestDto dto);
    CuentaResponseDto actualizarParcial(Long id, CuentaPatchDto dto);
}
