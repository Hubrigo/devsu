package com.devsu.cuentas.service;

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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MovimientoServiceImpl implements MovimientoService {

    private final MovimientoRepository movimientoRepository;
    private final CuentaRepository cuentaRepository;
    private final MovimientoMapper movimientoMapper;

    @Override
    @Transactional
    public MovimientoResponseDto registrar(MovimientoRequestDto dto) {
        Cuenta cuenta = cuentaRepository.findById(dto.getCuentaId())
                .orElseThrow(() -> new ResourceNotFoundException("Cuenta no encontrada con id: " + dto.getCuentaId()));

        BigDecimal nuevoSaldo = calcularNuevoSaldo(cuenta, dto.getTipoMovimiento(), dto.getValor());

        cuenta.setSaldoDisponible(nuevoSaldo);
        cuentaRepository.save(cuenta);

        Movimiento movimiento = Movimiento.builder()
                .fecha(LocalDateTime.now())
                .tipoMovimiento(dto.getTipoMovimiento())
                .valor(dto.getValor())
                .saldo(nuevoSaldo)
                .cuenta(cuenta)
                .build();

        Movimiento saved = movimientoRepository.save(movimiento);
        log.info("Movimiento registrado id={} en cuenta={}", saved.getId(), cuenta.getNumeroCuenta());
        return movimientoMapper.toResponseDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MovimientoResponseDto> listar() {
        return movimientoRepository.findAll().stream()
                .map(movimientoMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public MovimientoResponseDto buscarPorId(Long id) {
        return movimientoMapper.toResponseDto(findOrThrow(id));
    }

    @Override
    @Transactional
    public MovimientoResponseDto actualizar(Long id, MovimientoRequestDto dto) {
        Movimiento movimiento = findOrThrow(id);
        Cuenta cuenta = movimiento.getCuenta();

        // Revertir efecto del movimiento anterior
        BigDecimal saldoRevertido = revertirMovimiento(cuenta.getSaldoDisponible(),
                movimiento.getTipoMovimiento(), movimiento.getValor());
        // Aplicar nuevo movimiento
        BigDecimal nuevoSaldo = calcularNuevoSaldo(
                buildCuentaConSaldo(cuenta, saldoRevertido), dto.getTipoMovimiento(), dto.getValor());

        cuenta.setSaldoDisponible(nuevoSaldo);
        cuentaRepository.save(cuenta);

        movimiento.setTipoMovimiento(dto.getTipoMovimiento());
        movimiento.setValor(dto.getValor());
        movimiento.setSaldo(nuevoSaldo);
        movimiento.setFecha(LocalDateTime.now());

        return movimientoMapper.toResponseDto(movimientoRepository.save(movimiento));
    }

    private BigDecimal calcularNuevoSaldo(Cuenta cuenta, TipoMovimiento tipo, BigDecimal valor) {
        BigDecimal saldoActual = cuenta.getSaldoDisponible();
        if (tipo == TipoMovimiento.RETIRO) {
            if (saldoActual.compareTo(valor) < 0) {
                throw new SaldoInsuficienteException();
            }
            return saldoActual.subtract(valor);
        }
        return saldoActual.add(valor);
    }

    private BigDecimal revertirMovimiento(BigDecimal saldoActual, TipoMovimiento tipo, BigDecimal valor) {
        return tipo == TipoMovimiento.RETIRO ? saldoActual.add(valor) : saldoActual.subtract(valor);
    }

    private Cuenta buildCuentaConSaldo(Cuenta cuenta, BigDecimal saldo) {
        cuenta.setSaldoDisponible(saldo);
        return cuenta;
    }

    private Movimiento findOrThrow(Long id) {
        return movimientoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Movimiento no encontrado con id: " + id));
    }
}
