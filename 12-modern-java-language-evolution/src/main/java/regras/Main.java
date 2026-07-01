package regras;

import java.math.BigDecimal;
import java.util.List;

import static regras.RegraAnalise.*;

public class Main {
    public static void main(String[] args) {
        var solicitacoes = List.of(
            new Solicitacao.Emprestimo("Joao Silva", new BigDecimal("50000"), 36, new BigDecimal("5000.0")),
            new Solicitacao.Emprestimo("Maria Santos", new BigDecimal("100000"), 24, new BigDecimal("2000.0")),
            new Solicitacao.Credito("Carlos Oliveira", new BigDecimal("5000"), new BigDecimal("10000"), false),
            new Solicitacao.Credito("Ana Costa", new BigDecimal("15000"), new BigDecimal("10000"), false),
            new Solicitacao.Credito("Pedro Alves", new BigDecimal("3000"), new BigDecimal("5000"), true),
            new Solicitacao.Consorcio("Lucia Pereira", new BigDecimal("80000"), 60, 24),
            new Solicitacao.Consorcio("Rafael Souza", new BigDecimal("60000"), 48, 6)
        );

        System.out.println("=== MOTOR DE REGRAS — ANALISE DE SOLICITACOES ===\n");

        solicitacoes.forEach(s -> {
            RegraAnalise resultado = RegraAnalise.analisar(s);
            String tipo = switch (s) {
                case Solicitacao.Emprestimo e -> "Emprestimo";
                case Solicitacao.Credito c -> "Credito";
                case Solicitacao.Consorcio c -> "Consorcio";
            };
            String descricaoResultado = switch (resultado) {
                case Aprovada a -> "APROVADA: " + a.motivo();
                case Negada n -> "NEGADA: " + n.motivo();
                case RevisaoManual r -> "REVISAO MANUAL: " + r.motivo();
            };
            System.out.printf("[%s] %-50s | %s%n",
                tipo + "  ", s.cliente() + " (R$" + s.valor() + ")", descricaoResultado);
        });

        System.out.println("\n=== ESTATISTICAS ===");
        long aprovadas = solicitacoes.stream().filter(s ->
            RegraAnalise.analisar(s) instanceof Aprovada).count();
        long negadas = solicitacoes.stream().filter(s ->
            RegraAnalise.analisar(s) instanceof Negada).count();
        long revisao = solicitacoes.stream().filter(s ->
            RegraAnalise.analisar(s) instanceof RevisaoManual).count();

        System.out.println("Aprovadas: " + aprovadas);
        System.out.println("Negadas: " + negadas);
        System.out.println("Revisao manual: " + revisao);
    }
}
