package mensagens;

import java.rmi.Naming;
import java.util.List;

public class ClienteFilial {

    public static void main(String[] args) {
        try {
            System.out.println("Conectando ao servidor de estoque...");
            Estoque servico = (Estoque) Naming.lookup("rmi://localhost:1099/EstoqueCentral");

            System.out.println("Conectado. Listando produtos...");
            System.out.println();

            List<Produto> produtos = servico.listarProdutos();
            for (Produto p : produtos) {
                System.out.println("  " + p);
            }

            System.out.println();
            System.out.println("Consultando disponibilidade do produto 3 (Monitor LED 24)...");
            int disp = servico.consultarDisponibilidade(3);
            System.out.println("  Disponivel: " + disp + " unidades.");

            System.out.println();
            int qtdReserva = 5;
            System.out.println("Reservando " + qtdReserva + " unidades do Monitor LED 24...");
            boolean reservado = servico.reservarProduto(3, qtdReserva);
            if (reservado) {
                System.out.println("  RESERVA CONFIRMADA.");
            } else {
                System.out.println("  RESERVA NEGADA (estoque insuficiente).");
            }

            System.out.println();
            disp = servico.consultarDisponibilidade(3);
            System.out.println("Estoque atualizado do Monitor LED 24: " + disp + " unidades.");

        } catch (Exception e) {
            System.err.println("Erro no cliente:");
            e.printStackTrace();
        }
    }
}
