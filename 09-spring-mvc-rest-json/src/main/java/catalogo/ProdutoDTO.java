package catalogo;

import java.math.BigDecimal;

public class ProdutoDTO {
    private int id;
    private String nome;
    private BigDecimal preco;
    private String categoria;

    public ProdutoDTO() {}

    public ProdutoDTO(Produto p) {
        this.id = p.getId();
        this.nome = p.getNome();
        this.preco = p.getPreco();
        this.categoria = p.getCategoria();
    }

    public Produto toEntity() {
        return new Produto(id, nome, preco, categoria);
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public BigDecimal getPreco() { return preco; }
    public void setPreco(BigDecimal preco) { this.preco = preco; }
    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }
}
