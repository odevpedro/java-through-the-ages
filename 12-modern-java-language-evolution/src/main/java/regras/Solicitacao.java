package regras;

public sealed interface Solicitacao permits Solicitacao.Emprestimo, Solicitacao.Credito, Solicitacao.Consorcio {

    record Emprestimo(String cliente, double valor, int parcelas, double rendaMensal) implements Solicitacao {}
    record Credito(String cliente, double valor, double limiteDisponivel, boolean possuiRestricao) implements Solicitacao {}
    record Consorcio(String cliente, double valor, int totalParcelas, int parcelasPagas) implements Solicitacao {}

    String cliente();
    double valor();
}
