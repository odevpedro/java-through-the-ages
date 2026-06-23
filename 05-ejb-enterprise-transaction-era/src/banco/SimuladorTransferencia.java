package banco;

public class SimuladorTransferencia {
    public static void main(String[] args) {
        TransferenciaServiceSimulado service = new TransferenciaServiceSimulado();

        System.out.println("=== SALDOS ANTES DA TRANSFERENCIA ===");
        service.exibirContas();

        System.out.println("\nTransferindo R$ 1.000,00 da Conta 1001 para Conta 1002...");
        try {
            service.transferir(1001, 1002, 1000.00);
            System.out.println("Transferencia realizada com sucesso!");
        } catch (Exception e) {
            System.out.println("ERRO: " + e.getMessage());
            System.out.println("Transacao estornada (rollback).");
        }

        System.out.println("\n=== SALDOS APOS TRANSFERENCIA ===");
        service.exibirContas();

        System.out.println("\nTentando transferir R$ 10.000,00 da Conta 1003 (saldo: 1000)...");
        try {
            service.transferir(1003, 1001, 10000.00);
            System.out.println("Transferencia realizada!");
        } catch (Exception e) {
            System.out.println("ERRO: " + e.getMessage());
            System.out.println("Transacao estornada (rollback).");
        }

        System.out.println("\n=== SALDOS FINAIS ===");
        service.exibirContas();
    }
}
