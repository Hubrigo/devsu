package com.devsu.clientes.service;

import com.devsu.clientes.dto.request.ClientePatchDto;
import com.devsu.clientes.dto.request.ClienteRequestDto;
import com.devsu.clientes.dto.response.ClienteResponseDto;
import com.devsu.clientes.entity.Cliente;
import com.devsu.clientes.exception.BusinessException;
import com.devsu.clientes.exception.ResourceNotFoundException;
import com.devsu.clientes.mapper.ClienteMapper;
import com.devsu.clientes.messaging.ClienteEvent;
import com.devsu.clientes.messaging.ClienteEventPublisher;
import com.devsu.clientes.repository.ClienteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClienteServiceImpl implements ClienteService {

    private final ClienteRepository clienteRepository;
    private final ClienteMapper clienteMapper;
    private final ClienteEventPublisher eventPublisher;

    @Override
    @Transactional
    public ClienteResponseDto crear(ClienteRequestDto dto) {
        if (clienteRepository.existsByIdentificacion(dto.getIdentificacion())) {
            throw new BusinessException("Ya existe un cliente con la identificación: " + dto.getIdentificacion());
        }
        Cliente cliente = clienteMapper.toEntity(dto);
        cliente.setClienteId(UUID.randomUUID().toString());
        cliente.setEstado(dto.getEstado() != null ? dto.getEstado() : true);
        Cliente saved = clienteRepository.save(cliente);
        log.info("Cliente creado con id={}", saved.getId());
        eventPublisher.publishCreated(toEvent(saved, "CREATED"));
        return clienteMapper.toResponseDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClienteResponseDto> listar() {
        return clienteRepository.findAll()
                .stream()
                .map(clienteMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ClienteResponseDto buscarPorId(Long id) {
        Cliente cliente = findOrThrow(id);
        return clienteMapper.toResponseDto(cliente);
    }

    @Override
    @Transactional
    public ClienteResponseDto actualizar(Long id, ClienteRequestDto dto) {
        Cliente cliente = findOrThrow(id);
        String clienteIdActual = cliente.getClienteId();
        clienteMapper.toEntity(dto);
        cliente.setNombre(dto.getNombre());
        cliente.setGenero(dto.getGenero());
        cliente.setEdad(dto.getEdad());
        cliente.setDireccion(dto.getDireccion());
        cliente.setTelefono(dto.getTelefono());
        cliente.setContrasena(dto.getContrasena());
        if (dto.getEstado() != null) cliente.setEstado(dto.getEstado());
        cliente.setClienteId(clienteIdActual);
        Cliente saved = clienteRepository.save(cliente);
        eventPublisher.publishUpdated(toEvent(saved, "UPDATED"));
        return clienteMapper.toResponseDto(saved);
    }

    @Override
    @Transactional
    public ClienteResponseDto actualizarParcial(Long id, ClientePatchDto dto) {
        Cliente cliente = findOrThrow(id);
        clienteMapper.patchEntity(cliente, dto);
        Cliente saved = clienteRepository.save(cliente);
        eventPublisher.publishUpdated(toEvent(saved, "UPDATED"));
        return clienteMapper.toResponseDto(saved);
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        Cliente cliente = findOrThrow(id);
        cliente.setEstado(false);
        clienteRepository.save(cliente);
        eventPublisher.publishDeleted(toEvent(cliente, "DELETED"));
        log.info("Cliente id={} desactivado", id);
    }

    private Cliente findOrThrow(Long id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado con id: " + id));
    }

    private ClienteEvent toEvent(Cliente c, String type) {
        return ClienteEvent.builder()
                .id(c.getId())
                .clienteId(c.getClienteId())
                .nombre(c.getNombre())
                .estado(c.getEstado())
                .eventType(type)
                .build();
    }
}
