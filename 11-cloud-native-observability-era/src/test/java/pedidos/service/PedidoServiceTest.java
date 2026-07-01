package pedidos.service;

import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationContext;
import pedidos.model.Pedido;
import pedidos.repository.PedidoRepository;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PedidoServiceTest {

    @Mock
    private PedidoRepository repository;

    @Mock
    private ApplicationContext applicationContext;

    private final SimpleMeterRegistry meterRegistry = new SimpleMeterRegistry();

    private PedidoService service;

    private Pedido pedido;

    @BeforeEach
    void setUp() {
        service = new PedidoService(repository, applicationContext, meterRegistry);
        pedido = new Pedido();
        pedido.setCliente("Joao");
        pedido.setDescricao("Notebook");
        pedido.setValor(new BigDecimal("5000.00"));
    }

    @Test
    void criarPedido_deveSetarStatusCriado() {
        when(repository.save(any())).thenAnswer(i -> {
            Pedido p = i.getArgument(0);
            p.setId(1L);
            return p;
        });
        when(applicationContext.getBean(PedidoService.class)).thenReturn(mock(PedidoService.class));

        Pedido resultado = service.criarPedido(pedido);

        assertNotNull(resultado.getId());
        assertEquals("CRIADO", resultado.getStatus());
        assertEquals(0, resultado.getTentativas());
    }

    @Test
    void buscarPorId_quandoExiste_deveRetornarPedido() {
        pedido.setId(1L);
        when(repository.findById(1L)).thenReturn(Optional.of(pedido));

        Pedido resultado = service.buscarPorId(1L);

        assertEquals("Joao", resultado.getCliente());
    }

    @Test
    void buscarPorId_quandoNaoExiste_deveLancarExcecao() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> service.buscarPorId(99L));
    }

    @Test
    void listarTodos_deveRetornarLista() {
        when(repository.findAll()).thenReturn(java.util.List.of(pedido));

        var resultado = service.listarTodos();

        assertEquals(1, resultado.size());
    }
}
