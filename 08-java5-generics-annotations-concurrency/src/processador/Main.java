package processador;

import java.util.*;

public class Main {
    public static void main(String[] args) throws Exception {
        System.out.println("=== PROCESSADOR DE LOTE ===");
        System.out.println("Java 5: Generics, Enums, Annotations, ExecutorService\n");

        List<Tarefa> tarefas = Arrays.asList(
            new Tarefa(1, "Importar arquivo de clientes", 1500),
            new Tarefa(2, "Gerar relatorio mensal", 2500),
            new Tarefa(3, "Exportar dados para SAP", 1000),
            new Tarefa(4, "Validar notas fiscais", 2000),
            new Tarefa(5, "Processar pagamentos", 3500), // vai exceder timeout
            new Tarefa(6, "Atualizar estoque", 800)
        );

        ProcessadorLote processador = new ProcessadorLote();
        System.out.println("Iniciando processamento com 3 threads...\n");

        long inicio = System.currentTimeMillis();
        List<ResultadoProcessamento> resultados = processador.processar(tarefas, 3);
        long fim = System.currentTimeMillis();

        System.out.println("\n=== RESULTADOS ===");
        for (ResultadoProcessamento r : resultados) {
            System.out.printf("  #%d [%s] %s%n",
                r.getIdTarefa(),
                r.getStatus().getDescricao(),
                r.getMensagem());
        }

        System.out.println("\n=== RELATORIO ===");
        Map<StatusTarefa, Integer> relatorio = processador.gerarRelatorio(resultados);
        for (Map.Entry<StatusTarefa, Integer> entry : relatorio.entrySet()) {
            System.out.println("  " + entry.getKey().getDescricao() + ": " + entry.getValue());
        }

        System.out.printf("%nTempo total: %d ms (processamento paralelo com %d tarefas em 3 threads)%n",
            fim - inicio, tarefas.size());
    }
}
