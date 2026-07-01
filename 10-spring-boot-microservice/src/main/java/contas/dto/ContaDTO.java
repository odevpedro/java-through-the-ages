package contas.dto;

import contas.model.Conta;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ContaDTO {

    private Long id;
    private String descricao;
    private BigDecimal valor;
    private String tipo;
    private String status;
    private LocalDate vencimento;

    public ContaDTO() {}

    public ContaDTO(Conta c) {
        this.id = c.getId();
        this.descricao = c.getDescricao();
        this.valor = c.getValor();
        this.tipo = c.getTipo();
        this.status = c.getStatus();
        this.vencimento = c.getVencimento();
    }

    public Conta toEntity() {
        Conta c = new Conta();
        c.setId(id);
        c.setDescricao(descricao);
        c.setValor(valor);
        c.setTipo(tipo);
        c.setStatus(status);
        c.setVencimento(vencimento);
        return c;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public BigDecimal getValor() { return valor; }
    public void setValor(BigDecimal valor) { this.valor = valor; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDate getVencimento() { return vencimento; }
    public void setVencimento(LocalDate vencimento) { this.vencimento = vencimento; }
}
