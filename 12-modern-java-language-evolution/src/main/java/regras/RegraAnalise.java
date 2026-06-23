package regras;

public sealed interface RegraAnalise permits RegraAnalise.Aprovada, RegraAnalise.Negada, RegraAnalise.RevisaoManual {

    record Aprovada(String motivo) implements RegraAnalise {}
    record Negada(String motivo) implements RegraAnalise {}
    record RevisaoManual(String motivo) implements RegraAnalise {}

    static RegraAnalise analisar(Solicitacao s) {
        return switch (s) {
            case Solicitacao.Emprestimo(var cliente, var valor, int parcelas, double renda) -> {
                double comprometimento = (valor / parcelas) / renda;
                if (comprometimento > 0.3) {
                    yield new Negada("Comprometimento de renda excede 30%");
                }
                yield new Aprovada("Emprestimo aprovado");
            }
            case Solicitacao.Credito(var cliente, var valor, double limite, boolean restricao) -> {
                if (restricao) {
                    yield new Negada("Cliente possui restricao cadastral");
                }
                if (valor > limite) {
                    yield new RevisaoManual("Valor solicitado excede limite disponivel");
                }
                yield new Aprovada("Credito aprovado dentro do limite");
            }
            case Solicitacao.Consorcio(var cliente, var total, var parcelas, int pagas) -> {
                if (pagas < 12) {
                    yield new Negada("Minimo de 12 parcelas pagas para nova contemplacao");
                }
                yield new Aprovada("Consorcio apto para contemplacao");
            }
        };
    }
}
