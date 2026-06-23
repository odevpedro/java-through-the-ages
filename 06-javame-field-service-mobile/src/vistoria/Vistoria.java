package vistoria;

import java.util.Calendar;

public class Vistoria {
    private String codigoCliente;
    private String status; // "Realizada", "Pendente", "Cancelada"
    private String observacao;
    private long data;

    public Vistoria(String codigoCliente, String status, String observacao) {
        this.codigoCliente = codigoCliente;
        this.status = status;
        this.observacao = observacao;
        this.data = System.currentTimeMillis();
    }

    public String getCodigoCliente() { return codigoCliente; }
    public String getStatus() { return status; }
    public String getObservacao() { return observacao; }
    public long getData() { return data; }

    public String toString() {
        return codigoCliente + " - " + status;
    }
}
