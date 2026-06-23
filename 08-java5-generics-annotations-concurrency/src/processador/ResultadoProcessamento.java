package processador;

public class ResultadoProcessamento {
    private final int idTarefa;
    private final String nomeTarefa;
    private final StatusTarefa status;
    private final String mensagem;

    public ResultadoProcessamento(int idTarefa, String nomeTarefa, StatusTarefa status, String mensagem) {
        this.idTarefa = idTarefa;
        this.nomeTarefa = nomeTarefa;
        this.status = status;
        this.mensagem = mensagem;
    }

    public int getIdTarefa() { return idTarefa; }
    public StatusTarefa getStatus() { return status; }
    public String getMensagem() { return mensagem; }
    public String getNomeTarefa() { return nomeTarefa; }
}
