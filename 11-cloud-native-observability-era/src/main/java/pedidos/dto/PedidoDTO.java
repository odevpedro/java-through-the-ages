package pedidos.dto;

import pedidos.model.Pedido;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PedidoDTO {

    private Long id;
    private String cliente;
    private String descricao;
    private BigDecimal valor;
    private String status;
    private LocalDateTime criadoEm;
    private int tentativas;

    public PedidoDTO() {}

    public PedidoDTO(Pedido p) {
        this.id = p.getId();
        this.cliente = p.getCliente();
        this.descricao = p.getDescricao();
        this.valor = p.getValor();
        this.status = p.getStatus();
        this.criadoEm = p.getCriadoEm();
        this.tentativas = p.getTentativas();
    }

    public Pedido toEntity() {
        Pedido p = new Pedido();
        p.setId(id);
        p.setCliente(cliente);
        p.setDescricao(descricao);
        p.setValor(valor);
        p.setStatus(status);
        p.setCriadoEm(criadoEm);
        p.setTentativas(tentativas);
        return p;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getCliente() { return cliente; }
    public void setCliente(String cliente) { this.cliente = cliente; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public BigDecimal getValor() { return valor; }
    public void setValor(BigDecimal valor) { this.valor = valor; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getCriadoEm() { return criadoEm; }
    public void setCriadoEm(LocalDateTime criadoEm) { this.criadoEm = criadoEm; }
    public int getTentativas() { return tentativas; }
    public void setTentativas(int tentativas) { this.tentativas = tentativas; }
}
