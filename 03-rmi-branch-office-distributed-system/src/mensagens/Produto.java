package mensagens;

import java.io.Serializable;

public class Produto implements Serializable {
    private static final long serialVersionUID = 1L;
    private int id;
    private String nome;
    private int quantidadeEmEstoque;

    public Produto(int id, String nome, int quantidadeEmEstoque) {
        this.id = id;
        this.nome = nome;
        this.quantidadeEmEstoque = quantidadeEmEstoque;
    }

    public int getId() { return id; }
    public String getNome() { return nome; }
    public int getQuantidadeEmEstoque() { return quantidadeEmEstoque; }

    public boolean reservar(int quantidade) {
        if (quantidade > 0 && quantidade <= quantidadeEmEstoque) {
            quantidadeEmEstoque -= quantidade;
            return true;
        }
        return false;
    }

    public String toString() {
        return id + " - " + nome + " (" + quantidadeEmEstoque + " em estoque)";
    }
}
