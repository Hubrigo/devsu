package com.devsu.cuentas.controller;

import com.devsu.cuentas.dto.request.MovimientoRequestDto;
import com.devsu.cuentas.dto.response.MovimientoResponseDto;
import com.devsu.cuentas.service.MovimientoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/movimientos")
@RequiredArgsConstructor
@Tag(name = "Movimientos", description = "Registro de movimientos bancarios")
public class MovimientoController {

    private final MovimientoService movimientoService;

    @PostMapping
    @Operation(summary = "Registrar un movimiento (depósito o retiro)")
    public ResponseEntity<MovimientoResponseDto> registrar(@Valid @RequestBody MovimientoRequestDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(movimientoService.registrar(dto));
    }

    @GetMapping
    @Operation(summary = "Listar todos los movimientos")
    public ResponseEntity<List<MovimientoResponseDto>> listar() {
        return ResponseEntity.ok(movimientoService.listar());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar movimiento por ID")
    public ResponseEntity<MovimientoResponseDto> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(movimientoService.buscarPorId(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar movimiento")
    public ResponseEntity<MovimientoResponseDto> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody MovimientoRequestDto dto) {
        return ResponseEntity.ok(movimientoService.actualizar(id, dto));
    }
}
