package processador;

import java.util.*;
import java.util.concurrent.*;

public class ProcessadorLote {

    @SuppressWarnings("unchecked")
    public List<ResultadoProcessamento> processar(List<Tarefa> tarefas, int numThreads)
            throws InterruptedException {

        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        List<Future<ResultadoProcessamento>> futures = new ArrayList<Future<ResultadoProcessamento>>();

        for (final Tarefa t : tarefas) {
            Callable<ResultadoProcessamento> callable = new Callable<ResultadoProcessamento>() {
                @Override
                public ResultadoProcessamento call() throws Exception {
                    System.out.println("[PROCESSANDO] Tarefa #" + t.getId()
                        + ": " + t.getNome() + " (" + t.getSimulacaoMs() + "ms)");

                    // Simula processamento
                    Thread.sleep(t.getSimulacaoMs());

                    // Simula erro aleatorio para demonstracao
                    if (t.getSimulacaoMs() > 3000) {
                        return new ResultadoProcessamento(
                            t.getId(), t.getNome(), StatusTarefa.ERRO,
                            "Timeout: processamento excedeu 3s");
                    }

                    return new ResultadoProcessamento(
                        t.getId(), t.getNome(), StatusTarefa.CONCLUIDA,
                        "Processado com sucesso em " + t.getSimulacaoMs() + "ms");
                }
            };
            futures.add(executor.submit(callable));
        }

        executor.shutdown();

        List<ResultadoProcessamento> resultados = new ArrayList<ResultadoProcessamento>();
        for (Future<ResultadoProcessamento> f : futures) {
            try {
                ResultadoProcessamento r = f.get();
                resultados.add(r);
            } catch (ExecutionException e) {
                resultados.add(new ResultadoProcessamento(
                    -1, "Desconhecido", StatusTarefa.ERRO, e.getCause().getMessage()));
            }
        }

        return resultados;
    }

    public Map<StatusTarefa, Integer> gerarRelatorio(List<ResultadoProcessamento> resultados) {
        Map<StatusTarefa, Integer> relatorio = new EnumMap<StatusTarefa, Integer>(StatusTarefa.class);

        for (StatusTarefa s : StatusTarefa.values()) {
            relatorio.put(s, 0);
        }

        for (ResultadoProcessamento r : resultados) {
            relatorio.put(r.getStatus(), relatorio.get(r.getStatus()) + 1);
        }

        return relatorio;
    }
}
