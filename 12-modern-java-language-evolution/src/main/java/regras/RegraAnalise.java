package regras;

import java.math.BigDecimal;
import java.math.RoundingMode;

public sealed interface RegraAnalise permits RegraAnalise.Aprovada, RegraAnalise.Negada, RegraAnalise.RevisaoManual {

    record Aprovada(String motivo) implements RegraAnalise {}
    record Negada(String motivo) implements RegraAnalise {}
    record RevisaoManual(String motivo) implements RegraAnalise {}

    static RegraAnalise analisar(Solicitacao s) {
        return switch (s) {
            case Solicitacao.Emprestimo(var cliente, var valor, int parcelas, BigDecimal renda) -> {
                BigDecimal comprometimento = valor.divide(
                        BigDecimal.valueOf(parcelas).multiply(renda),
                        4, RoundingMode.HALF_UP);
                if (comprometimento.compareTo(new BigDecimal("0.3")) > 0) {
                    yield new Negada("Comprometimento de renda excede 30%");
                }
                yield new Aprovada("Emprestimo aprovado");
            }
            case Solicitacao.Credito(var cliente, var valor, BigDecimal limite, boolean restricao) -> {
                if (restricao) {
                    yield new Negada("Cliente possui restricao cadastral");
                }
                if (valor.compareTo(limite) > 0) {
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
