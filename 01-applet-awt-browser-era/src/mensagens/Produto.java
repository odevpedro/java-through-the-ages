package mensagens;

public class Produto {
    private String nome;
    private double precoUnitario;

    public Produto(String nome, double precoUnitario) {
        this.nome = nome;
        this.precoUnitario = precoUnitario;
    }

    public String getNome() { return nome; }
    public double getPrecoUnitario() { return precoUnitario; }

    public String toString() {
        return nome + " - R$ " + String.format("%.2f", precoUnitario);
    }
}
