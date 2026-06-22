package com.devsu.cuentas.service;

import com.devsu.cuentas.dto.request.CuentaPatchDto;
import com.devsu.cuentas.dto.request.CuentaRequestDto;
import com.devsu.cuentas.dto.response.CuentaResponseDto;
import com.devsu.cuentas.entity.Cuenta;
import com.devsu.cuentas.exception.BusinessException;
import com.devsu.cuentas.exception.ResourceNotFoundException;
import com.devsu.cuentas.mapper.CuentaMapper;
import com.devsu.cuentas.repository.ClienteCacheRepository;
import com.devsu.cuentas.repository.CuentaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CuentaServiceImpl implements CuentaService {

    private final CuentaRepository cuentaRepository;
    private final ClienteCacheRepository clienteCacheRepository;
    private final CuentaMapper cuentaMapper;

    @Override
    @Transactional
    public CuentaResponseDto crear(CuentaRequestDto dto) {
        if (cuentaRepository.existsByNumeroCuenta(dto.getNumeroCuenta())) {
            throw new BusinessException("Ya existe una cuenta con número: " + dto.getNumeroCuenta());
        }
        clienteCacheRepository.findById(dto.getClienteId())
                .filter(c -> Boolean.TRUE.equals(c.getEstado()))
                .orElseThrow(() -> new BusinessException("Cliente no encontrado o inactivo: " + dto.getClienteId()));

        Cuenta cuenta = cuentaMapper.toEntity(dto);
        Cuenta saved = cuentaRepository.save(cuenta);
        log.info("Cuenta creada id={} para clienteId={}", saved.getId(), saved.getClienteId());
        return cuentaMapper.toResponseDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CuentaResponseDto> listar() {
        return cuentaRepository.findAll().stream()
                .map(cuentaMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CuentaResponseDto buscarPorId(Long id) {
        return cuentaMapper.toResponseDto(findOrThrow(id));
    }

    @Override
    @Transactional
    public CuentaResponseDto actualizar(Long id, CuentaRequestDto dto) {
        Cuenta cuenta = findOrThrow(id);
        cuenta.setTipo(dto.getTipo());
        cuenta.setSaldoInicial(dto.getSaldoInicial());
        cuenta.setEstado(dto.getEstado() != null ? dto.getEstado() : cuenta.getEstado());
        return cuentaMapper.toResponseDto(cuentaRepository.save(cuenta));
    }

    @Override
    @Transactional
    public CuentaResponseDto actualizarParcial(Long id, CuentaPatchDto dto) {
        Cuenta cuenta = findOrThrow(id);
        cuentaMapper.patchEntity(cuenta, dto);
        return cuentaMapper.toResponseDto(cuentaRepository.save(cuenta));
    }

    private Cuenta findOrThrow(Long id) {
        return cuentaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cuenta no encontrada con id: " + id));
    }
}
