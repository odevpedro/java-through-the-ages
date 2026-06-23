package processador;

public enum StatusTarefa {
    PENDENTE("Pendente"),
    PROCESSANDO("Em processamento"),
    CONCLUIDA("Concluida"),
    ERRO("Erro");

    private final String descricao;

    StatusTarefa(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() { return descricao; }
}
