package estoque;

import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ServidorEstoque {

    private static final Logger LOG = Logger.getLogger(ServidorEstoque.class.getName());

    public static void main(String[] args) {
        try {
            LocateRegistry.createRegistry(1099);
            System.out.println("rmiregistry iniciado na porta 1099.");

            EstoqueImpl servico = new EstoqueImpl();
            Naming.rebind("rmi://localhost:1099/EstoqueCentral", servico);

            System.out.println("Servidor de estoque pronto.");
            System.out.println("Aguardando chamadas de filiais...");
            System.out.println();
            System.out.println("Produtos disponiveis:");
            for (Produto p : servico.listarProdutos()) {
                System.out.println("  " + p);
            }
            System.out.println();
            System.out.println("Pressione Ctrl+C para encerrar.");

        } catch (Exception e) {
            System.err.println("Erro no servidor: " + e.getMessage());
            LOG.log(Level.SEVERE, "Erro ao iniciar servidor de estoque", e);
            System.exit(1);
        }
    }
}
