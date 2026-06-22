package com.devsu.cuentas.service;

import com.devsu.cuentas.dto.response.ReporteResponseDto;
import com.devsu.cuentas.entity.ClienteCache;
import com.devsu.cuentas.entity.Movimiento;
import com.devsu.cuentas.exception.ResourceNotFoundException;
import com.devsu.cuentas.repository.ClienteCacheRepository;
import com.devsu.cuentas.repository.MovimientoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReporteServiceImpl implements ReporteService {

    private final MovimientoRepository movimientoRepository;
    private final ClienteCacheRepository clienteCacheRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ReporteResponseDto> generarReporte(LocalDate fechaInicio, LocalDate fechaFin, Long clienteId) {
        ClienteCache cliente = clienteCacheRepository.findById(clienteId)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado con id: " + clienteId));

        LocalDateTime inicio = fechaInicio.atStartOfDay();
        LocalDateTime fin = fechaFin.atTime(LocalTime.MAX);

        List<Movimiento> movimientos = movimientoRepository
                .findByClienteIdAndFechaBetween(clienteId, inicio, fin);

        return movimientos.stream().map(m -> ReporteResponseDto.builder()
                .fecha(m.getFecha().toLocalDate())
                .cliente(cliente.getNombre())
                .numeroCuenta(m.getCuenta().getNumeroCuenta())
                .tipo(m.getCuenta().getTipo().name())
                .saldoInicial(m.getCuenta().getSaldoInicial())
                .estado(m.getCuenta().getEstado())
                .movimiento(m.getTipoMovimiento().name().equals("RETIRO")
                        ? m.getValor().negate() : m.getValor())
                .saldoDisponible(m.getSaldo())
                .build()
        ).collect(Collectors.toList());
    }
}
