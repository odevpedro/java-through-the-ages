package contas.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import contas.dto.ContaDTO;
import contas.model.Conta;
import contas.service.ContaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ContaController.class)
class ContaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ContaService service;

    private final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    private Conta conta;

    @BeforeEach
    void setUp() {
        conta = new Conta();
        conta.setId(1L);
        conta.setDescricao("Aluguel");
        conta.setValor(new BigDecimal("2500.00"));
        conta.setTipo("PAGAR");
        conta.setStatus("PENDENTE");
        conta.setVencimento(LocalDate.of(2024, 6, 10));
    }

    @Test
    void listar_deveRetornar200() throws Exception {
        when(service.listarTodas()).thenReturn(List.of(conta));

        mockMvc.perform(get("/api/contas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].descricao").value("Aluguel"))
                .andExpect(jsonPath("$[0].valor").value(2500.00));
    }

    @Test
    void criar_deveRetornar201() throws Exception {
        when(service.criar(any())).thenReturn(conta);

        ContaDTO dto = new ContaDTO(conta);
        String json = mapper.writeValueAsString(dto);

        mockMvc.perform(post("/api/contas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.descricao").value("Aluguel"));
    }

    @Test
    void pagar_deveRetornar200() throws Exception {
        conta.setStatus("PAGO");
        when(service.pagar(1L)).thenReturn(conta);

        mockMvc.perform(put("/api/contas/1/pagar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PAGO"));
    }

    @Test
    void deletar_deveRetornar204() throws Exception {
        mockMvc.perform(delete("/api/contas/1"))
                .andExpect(status().isNoContent());
    }
}
