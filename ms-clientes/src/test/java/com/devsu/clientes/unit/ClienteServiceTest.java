package com.devsu.clientes.unit;

import com.devsu.clientes.dto.request.ClienteRequestDto;
import com.devsu.clientes.dto.response.ClienteResponseDto;
import com.devsu.clientes.entity.Cliente;
import com.devsu.clientes.exception.BusinessException;
import com.devsu.clientes.exception.ResourceNotFoundException;
import com.devsu.clientes.mapper.ClienteMapper;
import com.devsu.clientes.messaging.ClienteEventPublisher;
import com.devsu.clientes.repository.ClienteRepository;
import com.devsu.clientes.service.ClienteServiceImpl;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ClienteService - Pruebas unitarias")
class ClienteServiceTest {

    @Mock private ClienteRepository clienteRepository;
    @Mock private ClienteMapper clienteMapper;
    @Mock private ClienteEventPublisher eventPublisher;

    @InjectMocks
    private ClienteServiceImpl clienteService;

    private ClienteRequestDto requestDto;
    private Cliente clienteEntity;
    private ClienteResponseDto responseDto;

    @BeforeEach
    void setUp() {
        requestDto = ClienteRequestDto.builder()
                .nombre("Jose Lema")
                .identificacion("1234567890")
                .contrasena("1234")
                .estado(true)
                .build();

        clienteEntity = new Cliente();
        clienteEntity.setId(1L);
        clienteEntity.setClienteId("uuid-123");
        clienteEntity.setNombre("Jose Lema");
        clienteEntity.setIdentificacion("1234567890");
        clienteEntity.setEstado(true);

        responseDto = ClienteResponseDto.builder()
                .id(1L)
                .clienteId("uuid-123")
                .nombre("Jose Lema")
                .estado(true)
                .build();
    }

    @Test
    @DisplayName("Crear cliente exitosamente")
    void crearCliente_exitoso() {
        when(clienteRepository.existsByIdentificacion("1234567890")).thenReturn(false);
        when(clienteMapper.toEntity(requestDto)).thenReturn(clienteEntity);
        when(clienteRepository.save(any(Cliente.class))).thenReturn(clienteEntity);
        when(clienteMapper.toResponseDto(clienteEntity)).thenReturn(responseDto);
        doNothing().when(eventPublisher).publishCreated(any());

        ClienteResponseDto result = clienteService.crear(requestDto);

        assertThat(result).isNotNull();
        assertThat(result.getNombre()).isEqualTo("Jose Lema");
        verify(clienteRepository).save(any(Cliente.class));
        verify(eventPublisher).publishCreated(any());
    }

    @Test
    @DisplayName("Crear cliente con identificación duplicada lanza BusinessException")
    void crearCliente_identificacionDuplicada_lanzaExcepcion() {
        when(clienteRepository.existsByIdentificacion("1234567890")).thenReturn(true);

        assertThatThrownBy(() -> clienteService.crear(requestDto))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("1234567890");

        verify(clienteRepository, never()).save(any());
    }

    @Test
    @DisplayName("Buscar cliente por ID inexistente lanza ResourceNotFoundException")
    void buscarPorId_noExiste_lanzaExcepcion() {
        when(clienteRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> clienteService.buscarPorId(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    @DisplayName("Listar clientes retorna lista correcta")
    void listarClientes_retornaLista() {
        when(clienteRepository.findAll()).thenReturn(List.of(clienteEntity));
        when(clienteMapper.toResponseDto(clienteEntity)).thenReturn(responseDto);

        List<ClienteResponseDto> result = clienteService.listar();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getNombre()).isEqualTo("Jose Lema");
    }

    @Test
    @DisplayName("Eliminar cliente lo desactiva (estado=false)")
    void eliminarCliente_desactiva() {
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(clienteEntity));
        when(clienteRepository.save(any(Cliente.class))).thenReturn(clienteEntity);
        doNothing().when(eventPublisher).publishDeleted(any());

        clienteService.eliminar(1L);

        assertThat(clienteEntity.getEstado()).isFalse();
        verify(clienteRepository).save(clienteEntity);
    }
}
