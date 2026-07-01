package regras;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class RegraAnaliseTest {

    @Test
    void emprestimoComRendaCompativel_deveSerAprovado() {
        var s = new Solicitacao.Emprestimo("Joao", new BigDecimal("50000"), 36, new BigDecimal("5000"));
        var resultado = RegraAnalise.analisar(s);
        assertInstanceOf(RegraAnalise.Aprovada.class, resultado);
    }

    @Test
    void emprestimoComRendaIncompativel_deveSerNegado() {
        var s = new Solicitacao.Emprestimo("Maria", new BigDecimal("100000"), 24, new BigDecimal("2000"));
        var resultado = RegraAnalise.analisar(s);
        assertInstanceOf(RegraAnalise.Negada.class, resultado);
    }

    @Test
    void creditoSemRestricaoDentroDoLimite_deveSerAprovado() {
        var s = new Solicitacao.Credito("Carlos", new BigDecimal("5000"), new BigDecimal("10000"), false);
        var resultado = RegraAnalise.analisar(s);
        assertInstanceOf(RegraAnalise.Aprovada.class, resultado);
    }

    @Test
    void creditoComRestricao_deveSerNegado() {
        var s = new Solicitacao.Credito("Pedro", new BigDecimal("3000"), new BigDecimal("5000"), true);
        var resultado = RegraAnalise.analisar(s);
        assertInstanceOf(RegraAnalise.Negada.class, resultado);
    }

    @Test
    void creditoAcimaDoLimite_deveRequererRevisao() {
        var s = new Solicitacao.Credito("Ana", new BigDecimal("15000"), new BigDecimal("10000"), false);
        var resultado = RegraAnalise.analisar(s);
        assertInstanceOf(RegraAnalise.RevisaoManual.class, resultado);
    }

    @Test
    void consorcioComParcelasMinimasPagas_deveSerAprovado() {
        var s = new Solicitacao.Consorcio("Lucia", new BigDecimal("80000"), 60, 24);
        var resultado = RegraAnalise.analisar(s);
        assertInstanceOf(RegraAnalise.Aprovada.class, resultado);
    }

    @Test
    void consorcioSemParcelasMinimas_deveSerNegado() {
        var s = new Solicitacao.Consorcio("Rafael", new BigDecimal("60000"), 48, 6);
        var resultado = RegraAnalise.analisar(s);
        assertInstanceOf(RegraAnalise.Negada.class, resultado);
    }
}
