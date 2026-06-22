package com.devsu.clientes.controller;

import com.devsu.clientes.dto.request.ClientePatchDto;
import com.devsu.clientes.dto.request.ClienteRequestDto;
import com.devsu.clientes.dto.response.ClienteResponseDto;
import com.devsu.clientes.service.ClienteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/clientes")
@RequiredArgsConstructor
@Tag(name = "Clientes", description = "Gestión de clientes bancarios")
public class ClienteController {

    private final ClienteService clienteService;

    @PostMapping
    @Operation(summary = "Crear un nuevo cliente")
    public ResponseEntity<ClienteResponseDto> crear(@Valid @RequestBody ClienteRequestDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(clienteService.crear(dto));
    }

    @GetMapping
    @Operation(summary = "Listar todos los clientes")
    public ResponseEntity<List<ClienteResponseDto>> listar() {
        return ResponseEntity.ok(clienteService.listar());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar cliente por ID")
    public ResponseEntity<ClienteResponseDto> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(clienteService.buscarPorId(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar cliente completo")
    public ResponseEntity<ClienteResponseDto> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody ClienteRequestDto dto) {
        return ResponseEntity.ok(clienteService.actualizar(id, dto));
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Actualizar cliente parcialmente")
    public ResponseEntity<ClienteResponseDto> actualizarParcial(
            @PathVariable Long id,
            @RequestBody ClientePatchDto dto) {
        return ResponseEntity.ok(clienteService.actualizarParcial(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Desactivar cliente")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        clienteService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
