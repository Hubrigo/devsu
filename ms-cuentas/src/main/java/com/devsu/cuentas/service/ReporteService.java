package com.devsu.cuentas.service;

import com.devsu.cuentas.dto.response.ReporteResponseDto;

import java.time.LocalDate;
import java.util.List;

public interface ReporteService {
    List<ReporteResponseDto> generarReporte(LocalDate fechaInicio, LocalDate fechaFin, Long clienteId);
}
