package regras;

import java.math.BigDecimal;

public sealed interface Solicitacao permits Solicitacao.Emprestimo, Solicitacao.Credito, Solicitacao.Consorcio {

    record Emprestimo(String cliente, BigDecimal valor, int parcelas, BigDecimal rendaMensal) implements Solicitacao {}
    record Credito(String cliente, BigDecimal valor, BigDecimal limiteDisponivel, boolean possuiRestricao) implements Solicitacao {}
    record Consorcio(String cliente, BigDecimal valor, int totalParcelas, int parcelasPagas) implements Solicitacao {}

    String cliente();
    BigDecimal valor();
}
