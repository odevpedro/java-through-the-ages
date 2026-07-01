package banco;

import java.util.HashMap;
import java.util.Map;

public class TransferenciaServiceSimulado {
    private Map<Integer, Conta> contas = new HashMap<>();

    public TransferenciaServiceSimulado() {
        contas.put(1001, new Conta(1001, "Empresa A", 5000.00));
        contas.put(1002, new Conta(1002, "Empresa B", 3000.00));
        contas.put(1003, new Conta(1003, "Fornecedor C", 1000.00));
    }

    // Simula transacao: debita origem, credita destino
    // Se algo falha, rolleback (no throw = commit)
    public void transferir(int origem, int destino, double valor) {
        Conta contaOrigem = contas.get(origem);
        Conta contaDestino = contas.get(destino);

        if (contaOrigem == null || contaDestino == null) {
            throw new IllegalArgumentException("Conta inexistente");
        }

        // "inicio da transacao"
        double saldoOrigemBackup = contaOrigem.getSaldo();
        double saldoDestinoBackup = contaDestino.getSaldo();

        try {
            contaOrigem.debitar(valor);
            contaDestino.creditar(valor);
        } catch (Exception e) {
            contaOrigem.setSaldo(saldoOrigemBackup);
            contaDestino.setSaldo(saldoDestinoBackup);
            throw e;
        }
    }

    public void exibirContas() {
        for (Conta c : contas.values()) {
            System.out.printf("Conta %d (%s): R$ %.2f%n",
                c.getNumero(), c.getTitular(), c.getSaldo());
        }
    }
}
