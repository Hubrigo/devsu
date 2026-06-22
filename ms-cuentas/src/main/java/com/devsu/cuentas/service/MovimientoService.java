package com.devsu.cuentas.service;

import com.devsu.cuentas.dto.request.MovimientoRequestDto;
import com.devsu.cuentas.dto.response.MovimientoResponseDto;

import java.util.List;

public interface MovimientoService {
    MovimientoResponseDto registrar(MovimientoRequestDto dto);
    List<MovimientoResponseDto> listar();
    MovimientoResponseDto buscarPorId(Long id);
    MovimientoResponseDto actualizar(Long id, MovimientoRequestDto dto);
}
