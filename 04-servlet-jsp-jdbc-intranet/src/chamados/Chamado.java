package chamados;

public class Chamado {
    private int id;
    private String titulo;
    private String descricao;
    private String solicitante;
    private String dataAbertura;

    public Chamado() {}

    public Chamado(int id, String titulo, String descricao, String solicitante, String dataAbertura) {
        this.id = id;
        this.titulo = titulo;
        this.descricao = descricao;
        this.solicitante = solicitante;
        this.dataAbertura = dataAbertura;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public String getSolicitante() { return solicitante; }
    public void setSolicitante(String solicitante) { this.solicitante = solicitante; }

    public String getDataAbertura() { return dataAbertura; }
    public void setDataAbertura(String dataAbertura) { this.dataAbertura = dataAbertura; }
}
