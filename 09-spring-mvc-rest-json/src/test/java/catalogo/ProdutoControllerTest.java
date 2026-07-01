package catalogo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProdutoControllerTest {

    @Mock
    private ProdutoRepository repository;

    @InjectMocks
    private ProdutoController controller;

    private Produto notebook;

    @BeforeEach
    void setUp() {
        notebook = new Produto(1, "Notebook Dell", new BigDecimal("4500.00"), "Informatica");
    }

    @Test
    void listar_deveRetornarTodosOsProdutos() {
        when(repository.findAll()).thenReturn(List.of(notebook));

        List<ProdutoDTO> resultado = controller.listar();

        assertEquals(1, resultado.size());
        assertEquals("Notebook Dell", resultado.get(0).getNome());
        assertEquals(new BigDecimal("4500.00"), resultado.get(0).getPreco());
    }

    @Test
    void obter_quandoExiste_deveRetornar200() {
        when(repository.findById(1)).thenReturn(notebook);

        ResponseEntity<ProdutoDTO> resposta = controller.obter(1);

        assertEquals(HttpStatus.OK, resposta.getStatusCode());
        assertEquals("Notebook Dell", resposta.getBody().getNome());
    }

    @Test
    void obter_quandoNaoExiste_deveRetornar404() {
        when(repository.findById(99)).thenReturn(null);

        ResponseEntity<ProdutoDTO> resposta = controller.obter(99);

        assertEquals(HttpStatus.NOT_FOUND, resposta.getStatusCode());
    }

    @Test
    void criar_deveRetornar201() {
        ProdutoDTO dto = new ProdutoDTO();
        dto.setNome("Mouse");
        dto.setPreco(new BigDecimal("120.00"));
        dto.setCategoria("Perifericos");

        Produto salvo = new Produto(1, "Mouse", new BigDecimal("120.00"), "Perifericos");
        when(repository.save(any())).thenReturn(salvo);

        ProdutoDTO resultado = controller.criar(dto);

        assertEquals("Mouse", resultado.getNome());
        assertEquals(new BigDecimal("120.00"), resultado.getPreco());
    }

    @Test
    void deletar_quandoExiste_deveRetornar200() {
        when(repository.delete(1)).thenReturn(notebook);

        ResponseEntity<Map<String, String>> resposta = controller.deletar(1);

        assertEquals(HttpStatus.OK, resposta.getStatusCode());
        assertEquals("Produto removido com sucesso", resposta.getBody().get("message"));
    }

    @Test
    void deletar_quandoNaoExiste_deveRetornar404() {
        when(repository.delete(99)).thenReturn(null);

        ResponseEntity<Map<String, String>> resposta = controller.deletar(99);

        assertEquals(HttpStatus.NOT_FOUND, resposta.getStatusCode());
    }
}
