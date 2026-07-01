package pedidos.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import pedidos.dto.PedidoDTO;
import pedidos.model.Pedido;
import pedidos.service.PedidoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PedidoController.class)
class PedidoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PedidoService service;

    private final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    private Pedido pedido;

    @BeforeEach
    void setUp() {
        pedido = new Pedido();
        pedido.setId(1L);
        pedido.setCliente("Joao");
        pedido.setDescricao("Notebook");
        pedido.setValor(new BigDecimal("5000.00"));
        pedido.setStatus("CRIADO");
    }

    @Test
    void listar_deveRetornar200() throws Exception {
        when(service.listarTodos()).thenReturn(List.of(pedido));

        mockMvc.perform(get("/api/pedidos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].cliente").value("Joao"))
                .andExpect(jsonPath("$[0].valor").value(5000.00));
    }

    @Test
    void criar_deveRetornar201() throws Exception {
        when(service.criarPedido(any())).thenReturn(pedido);

        PedidoDTO dto = new PedidoDTO(pedido);
        String json = mapper.writeValueAsString(dto);

        mockMvc.perform(post("/api/pedidos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.cliente").value("Joao"));
    }

    @Test
    void buscar_quandoExiste_deveRetornar200() throws Exception {
        when(service.buscarPorId(1L)).thenReturn(pedido);

        mockMvc.perform(get("/api/pedidos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cliente").value("Joao"));
    }
}
