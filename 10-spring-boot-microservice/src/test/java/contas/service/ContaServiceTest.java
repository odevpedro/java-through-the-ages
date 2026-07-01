package contas.service;

import contas.model.Conta;
import contas.repository.ContaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ContaServiceTest {

    @Mock
    private ContaRepository repository;

    @InjectMocks
    private ContaService service;

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
    void listarTodas_deveRetornarTodas() {
        when(repository.findAll()).thenReturn(List.of(conta));

        List<Conta> resultado = service.listarTodas();

        assertEquals(1, resultado.size());
        assertEquals("Aluguel", resultado.get(0).getDescricao());
    }

    @Test
    void criar_deveSetarStatusPendente() {
        when(repository.save(any())).thenReturn(conta);

        Conta resultado = service.criar(conta);

        assertEquals("PENDENTE", resultado.getStatus());
        verify(repository).save(conta);
    }

    @Test
    void pagar_deveAlterarStatusParaPago() {
        when(repository.findById(1L)).thenReturn(Optional.of(conta));
        when(repository.save(any())).thenAnswer(i -> i.getArgument(0));

        Conta resultado = service.pagar(1L);

        assertEquals("PAGO", resultado.getStatus());
    }

    @Test
    void buscarPorId_quandoExiste_deveRetornarConta() {
        when(repository.findById(1L)).thenReturn(Optional.of(conta));

        Conta resultado = service.buscarPorId(1L);

        assertEquals("Aluguel", resultado.getDescricao());
    }

    @Test
    void buscarPorId_quandoNaoExiste_deveLancarExcecao() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> service.buscarPorId(99L));
    }

    @Test
    void deletar_deveChamarRepositoryDelete() {
        when(repository.findById(1L)).thenReturn(Optional.of(conta));

        service.deletar(1L);

        verify(repository).delete(conta);
    }
}
