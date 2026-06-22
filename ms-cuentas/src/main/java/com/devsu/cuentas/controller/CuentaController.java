package com.devsu.cuentas.controller;

import com.devsu.cuentas.dto.request.CuentaPatchDto;
import com.devsu.cuentas.dto.request.CuentaRequestDto;
import com.devsu.cuentas.dto.response.CuentaResponseDto;
import com.devsu.cuentas.service.CuentaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cuentas")
@RequiredArgsConstructor
@Tag(name = "Cuentas", description = "Gestión de cuentas bancarias")
public class CuentaController {

    private final CuentaService cuentaService;

    @PostMapping
    @Operation(summary = "Crear una nueva cuenta")
    public ResponseEntity<CuentaResponseDto> crear(@Valid @RequestBody CuentaRequestDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(cuentaService.crear(dto));
    }

    @GetMapping
    @Operation(summary = "Listar todas las cuentas")
    public ResponseEntity<List<CuentaResponseDto>> listar() {
        return ResponseEntity.ok(cuentaService.listar());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar cuenta por ID")
    public ResponseEntity<CuentaResponseDto> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(cuentaService.buscarPorId(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar cuenta completa")
    public ResponseEntity<CuentaResponseDto> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody CuentaRequestDto dto) {
        return ResponseEntity.ok(cuentaService.actualizar(id, dto));
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Actualizar cuenta parcialmente")
    public ResponseEntity<CuentaResponseDto> actualizarParcial(
            @PathVariable Long id,
            @RequestBody CuentaPatchDto dto) {
        return ResponseEntity.ok(cuentaService.actualizarParcial(id, dto));
    }
}
