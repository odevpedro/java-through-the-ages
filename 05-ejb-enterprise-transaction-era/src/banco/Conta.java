package banco;

import java.io.Serializable;

public class Conta implements Serializable {
    private static final long serialVersionUID = 1L;
    private int numero;
    private String titular;
    private double saldo;

    public Conta(int numero, String titular, double saldoInicial) {
        this.numero = numero;
        this.titular = titular;
        this.saldo = saldoInicial;
    }

    public int getNumero() { return numero; }
    public String getTitular() { return titular; }
    public double getSaldo() { return saldo; }

    public void debitar(double valor) {
        if (valor > saldo) throw new IllegalArgumentException("Saldo insuficiente");
        saldo -= valor;
    }

    public void creditar(double valor) {
        saldo += valor;
    }
}
