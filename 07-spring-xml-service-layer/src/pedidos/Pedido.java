package pedidos;

public class Pedido {
    private int id;
    private String cliente;
    private String produto;
    private int quantidade;
    private double valorTotal;
    private String status; // "PENDENTE", "PAGO", "CANCELADO"

    public Pedido() {}

    public Pedido(String cliente, String produto, int quantidade, double valorTotal) {
        this.cliente = cliente;
        this.produto = produto;
        this.quantidade = quantidade;
        this.valorTotal = valorTotal;
        this.status = "PENDENTE";
    }

    // getters and setters for all fields
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getCliente() { return cliente; }
    public void setCliente(String cliente) { this.cliente = cliente; }
    public String getProduto() { return produto; }
    public void setProduto(String produto) { this.produto = produto; }
    public int getQuantidade() { return quantidade; }
    public void setQuantidade(int quantidade) { this.quantidade = quantidade; }
    public double getValorTotal() { return valorTotal; }
    public void setValorTotal(double valorTotal) { this.valorTotal = valorTotal; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
