package com.devsu.cuentas.controller;

import com.devsu.cuentas.dto.response.ReporteResponseDto;
import com.devsu.cuentas.service.ReporteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/reportes")
@RequiredArgsConstructor
@Tag(name = "Reportes", description = "Estado de cuenta por cliente y rango de fechas")
public class ReporteController {

    private final ReporteService reporteService;

    @GetMapping
    @Operation(summary = "Generar estado de cuenta")
    public ResponseEntity<List<ReporteResponseDto>> generarReporte(
            @Parameter(description = "Fecha de inicio (YYYY-MM-DD)")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @Parameter(description = "Fecha de fin (YYYY-MM-DD)")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin,
            @Parameter(description = "ID del cliente")
            @RequestParam Long clienteId) {
        return ResponseEntity.ok(reporteService.generarReporte(fechaInicio, fechaFin, clienteId));
    }
}
