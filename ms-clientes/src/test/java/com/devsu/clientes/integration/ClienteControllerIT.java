package com.devsu.clientes.integration;

import com.devsu.clientes.dto.request.ClienteRequestDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("ClienteController - Pruebas de integración")
class ClienteControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private ClienteRequestDto clienteDto;

    @BeforeEach
    void setUp() {
        clienteDto = ClienteRequestDto.builder()
                .nombre("Test Integration")
                .identificacion("IT-" + System.currentTimeMillis())
                .genero("M")
                .edad(30)
                .direccion("Av. Test 123")
                .telefono("0999999999")
                .contrasena("pass123")
                .estado(true)
                .build();
    }

    @Test
    @DisplayName("POST /clientes - crea cliente y retorna 201")
    void crearCliente_retorna201() throws Exception {
        mockMvc.perform(post("/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(clienteDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nombre").value("Test Integration"))
                .andExpect(jsonPath("$.clienteId").isNotEmpty())
                .andExpect(jsonPath("$.estado").value(true));
    }

    @Test
    @DisplayName("GET /clientes - retorna lista no vacía")
    void listarClientes_retornaLista() throws Exception {
        // crear uno primero
        mockMvc.perform(post("/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(clienteDto)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/clientes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @DisplayName("GET /clientes/{id} inexistente retorna 404")
    void buscarPorId_noExiste_retorna404() throws Exception {
        mockMvc.perform(get("/clientes/99999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    @DisplayName("POST /clientes con datos inválidos retorna 400")
    void crearCliente_datosInvalidos_retorna400() throws Exception {
        ClienteRequestDto invalido = ClienteRequestDto.builder()
                .nombre("")
                .identificacion("")
                .contrasena("")
                .build();

        mockMvc.perform(post("/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalido)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("DELETE /clientes/{id} desactiva cliente")
    void eliminarCliente_desactiva() throws Exception {
        String response = mockMvc.perform(post("/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(clienteDto)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Long id = objectMapper.readTree(response).get("id").asLong();

        mockMvc.perform(delete("/clientes/" + id))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/clientes/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value(false));
    }
}
