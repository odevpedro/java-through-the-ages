package processador;

public class Tarefa {
    private final int id;
    private final String nome;
    private final long simulacaoMs; // simula tempo de processamento

    public Tarefa(int id, String nome, long simulacaoMs) {
        this.id = id;
        this.nome = nome;
        this.simulacaoMs = simulacaoMs;
    }

    public int getId() { return id; }
    public String getNome() { return nome; }
    public long getSimulacaoMs() { return simulacaoMs; }
}
