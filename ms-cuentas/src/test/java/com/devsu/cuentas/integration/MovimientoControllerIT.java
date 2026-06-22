package com.devsu.cuentas.integration;

import com.devsu.cuentas.dto.request.CuentaRequestDto;
import com.devsu.cuentas.dto.request.MovimientoRequestDto;
import com.devsu.cuentas.entity.Cuenta.TipoCuenta;
import com.devsu.cuentas.entity.ClienteCache;
import com.devsu.cuentas.entity.Movimiento.TipoMovimiento;
import com.devsu.cuentas.repository.ClienteCacheRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("MovimientoController - Pruebas de integración")
class MovimientoControllerIT {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private ClienteCacheRepository clienteCacheRepository;

    private Long cuentaId;

    @BeforeEach
    void setUp() throws Exception {
        // Seed cliente en cache
        clienteCacheRepository.save(ClienteCache.builder()
                .clienteId(100L)
                .nombre("Test Cliente")
                .estado(true)
                .updatedAt(LocalDateTime.now())
                .build());

        // Crear cuenta
        CuentaRequestDto cuentaDto = CuentaRequestDto.builder()
                .numeroCuenta("TEST-" + System.currentTimeMillis())
                .tipo(TipoCuenta.AHORROS)
                .saldoInicial(new BigDecimal("2000.00"))
                .estado(true)
                .clienteId(100L)
                .build();

        String response = mockMvc.perform(post("/cuentas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cuentaDto)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        cuentaId = objectMapper.readTree(response).get("id").asLong();
    }

    @Test
    @DisplayName("POST /movimientos - retiro exitoso reduce saldo")
    void registrarRetiro_exitoso() throws Exception {
        MovimientoRequestDto dto = MovimientoRequestDto.builder()
                .tipoMovimiento(TipoMovimiento.RETIRO)
                .valor(new BigDecimal("575.00"))
                .cuentaId(cuentaId)
                .build();

        mockMvc.perform(post("/movimientos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.saldo").value(1425.00))
                .andExpect(jsonPath("$.tipoMovimiento").value("RETIRO"));
    }

    @Test
    @DisplayName("POST /movimientos - depósito exitoso aumenta saldo")
    void registrarDeposito_exitoso() throws Exception {
        MovimientoRequestDto dto = MovimientoRequestDto.builder()
                .tipoMovimiento(TipoMovimiento.DEPOSITO)
                .valor(new BigDecimal("500.00"))
                .cuentaId(cuentaId)
                .build();

        mockMvc.perform(post("/movimientos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.saldo").value(2500.00));
    }

    @Test
    @DisplayName("POST /movimientos - saldo insuficiente retorna 400 con mensaje")
    void registrarRetiro_saldoInsuficiente_retorna400() throws Exception {
        MovimientoRequestDto dto = MovimientoRequestDto.builder()
                .tipoMovimiento(TipoMovimiento.RETIRO)
                .valor(new BigDecimal("99999.00"))
                .cuentaId(cuentaId)
                .build();

        mockMvc.perform(post("/movimientos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Saldo no disponible"))
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    @DisplayName("GET /movimientos - retorna lista")
    void listarMovimientos_retornaLista() throws Exception {
        mockMvc.perform(get("/movimientos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }
}
