package com.devsu.cuentas.unit;

import com.devsu.cuentas.dto.request.MovimientoRequestDto;
import com.devsu.cuentas.dto.response.MovimientoResponseDto;
import com.devsu.cuentas.entity.Cuenta;
import com.devsu.cuentas.entity.Movimiento;
import com.devsu.cuentas.entity.Movimiento.TipoMovimiento;
import com.devsu.cuentas.exception.ResourceNotFoundException;
import com.devsu.cuentas.exception.SaldoInsuficienteException;
import com.devsu.cuentas.mapper.MovimientoMapper;
import com.devsu.cuentas.repository.CuentaRepository;
import com.devsu.cuentas.repository.MovimientoRepository;
import com.devsu.cuentas.service.MovimientoServiceImpl;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("MovimientoService - Pruebas unitarias")
class MovimientoServiceTest {

    @Mock private MovimientoRepository movimientoRepository;
    @Mock private CuentaRepository cuentaRepository;
    @Mock private MovimientoMapper movimientoMapper;

    @InjectMocks
    private MovimientoServiceImpl movimientoService;

    private Cuenta cuentaAhorros;
    private MovimientoResponseDto responseDto;

    @BeforeEach
    void setUp() {
        cuentaAhorros = Cuenta.builder()
                .id(1L)
                .numeroCuenta("478758")
                .tipo(Cuenta.TipoCuenta.AHORROS)
                .saldoInicial(new BigDecimal("2000.00"))
                .saldoDisponible(new BigDecimal("2000.00"))
                .estado(true)
                .clienteId(1L)
                .build();

        responseDto = MovimientoResponseDto.builder()
                .id(1L)
                .tipoMovimiento(TipoMovimiento.RETIRO)
                .valor(new BigDecimal("575.00"))
                .saldo(new BigDecimal("1425.00"))
                .cuentaId(1L)
                .build();
    }

    @Test
    @DisplayName("Registrar retiro reduce saldo correctamente (2000 - 575 = 1425)")
    void registrarRetiro_reduceSaldo() {
        MovimientoRequestDto dto = MovimientoRequestDto.builder()
                .tipoMovimiento(TipoMovimiento.RETIRO)
                .valor(new BigDecimal("575.00"))
                .cuentaId(1L)
                .build();

        Movimiento movimientoGuardado = Movimiento.builder()
                .id(1L)
                .fecha(LocalDateTime.now())
                .tipoMovimiento(TipoMovimiento.RETIRO)
                .valor(new BigDecimal("575.00"))
                .saldo(new BigDecimal("1425.00"))
                .cuenta(cuentaAhorros)
                .build();

        when(cuentaRepository.findById(1L)).thenReturn(Optional.of(cuentaAhorros));
        when(cuentaRepository.save(any(Cuenta.class))).thenReturn(cuentaAhorros);
        when(movimientoRepository.save(any(Movimiento.class))).thenReturn(movimientoGuardado);
        when(movimientoMapper.toResponseDto(movimientoGuardado)).thenReturn(responseDto);

        MovimientoResponseDto result = movimientoService.registrar(dto);

        assertThat(cuentaAhorros.getSaldoDisponible()).isEqualByComparingTo("1425.00");
        assertThat(result.getSaldo()).isEqualByComparingTo("1425.00");
        verify(cuentaRepository).save(cuentaAhorros);
        verify(movimientoRepository).save(any(Movimiento.class));
    }

    @Test
    @DisplayName("Registrar depósito aumenta saldo correctamente (100 + 600 = 700)")
    void registrarDeposito_aumentaSaldo() {
        cuentaAhorros.setSaldoDisponible(new BigDecimal("100.00"));
        MovimientoRequestDto dto = MovimientoRequestDto.builder()
                .tipoMovimiento(TipoMovimiento.DEPOSITO)
                .valor(new BigDecimal("600.00"))
                .cuentaId(1L)
                .build();

        Movimiento mov = Movimiento.builder()
                .id(2L)
                .tipoMovimiento(TipoMovimiento.DEPOSITO)
                .valor(new BigDecimal("600.00"))
                .saldo(new BigDecimal("700.00"))
                .cuenta(cuentaAhorros)
                .build();

        when(cuentaRepository.findById(1L)).thenReturn(Optional.of(cuentaAhorros));
        when(cuentaRepository.save(any())).thenReturn(cuentaAhorros);
        when(movimientoRepository.save(any())).thenReturn(mov);
        when(movimientoMapper.toResponseDto(mov)).thenReturn(
                MovimientoResponseDto.builder().saldo(new BigDecimal("700.00")).build());

        MovimientoResponseDto result = movimientoService.registrar(dto);

        assertThat(cuentaAhorros.getSaldoDisponible()).isEqualByComparingTo("700.00");
        assertThat(result.getSaldo()).isEqualByComparingTo("700.00");
    }

    @Test
    @DisplayName("Retiro mayor al saldo lanza SaldoInsuficienteException")
    void retiro_saldoInsuficiente_lanzaExcepcion() {
        cuentaAhorros.setSaldoDisponible(new BigDecimal("0.00"));
        MovimientoRequestDto dto = MovimientoRequestDto.builder()
                .tipoMovimiento(TipoMovimiento.RETIRO)
                .valor(new BigDecimal("540.00"))
                .cuentaId(1L)
                .build();

        when(cuentaRepository.findById(1L)).thenReturn(Optional.of(cuentaAhorros));

        assertThatThrownBy(() -> movimientoService.registrar(dto))
                .isInstanceOf(SaldoInsuficienteException.class)
                .hasMessage("Saldo no disponible");

        verify(movimientoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Registrar movimiento en cuenta inexistente lanza ResourceNotFoundException")
    void registrar_cuentaInexistente_lanzaExcepcion() {
        when(cuentaRepository.findById(99L)).thenReturn(Optional.empty());

        MovimientoRequestDto dto = MovimientoRequestDto.builder()
                .tipoMovimiento(TipoMovimiento.DEPOSITO)
                .valor(new BigDecimal("100.00"))
                .cuentaId(99L)
                .build();

        assertThatThrownBy(() -> movimientoService.registrar(dto))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("Listar movimientos retorna lista correcta")
    void listarMovimientos_retornaLista() {
        Movimiento mov = Movimiento.builder().id(1L).cuenta(cuentaAhorros).build();
        when(movimientoRepository.findAll()).thenReturn(List.of(mov));
        when(movimientoMapper.toResponseDto(mov)).thenReturn(responseDto);

        List<MovimientoResponseDto> result = movimientoService.listar();

        assertThat(result).hasSize(1);
    }
}
