package pedidos;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.math.BigDecimal;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        ApplicationContext ctx = new ClassPathXmlApplicationContext("beans.xml");
        PedidoService service = (PedidoService) ctx.getBean("pedidoService");

        // Criar alguns pedidos
        service.criarPedido("Empresa X", "Notebook", 2, new BigDecimal("8000.00"));
        service.criarPedido("Empresa Y", "Monitor 27\"", 5, new BigDecimal("7500.00"));

        System.out.println("\n=== LISTA DE PEDIDOS ===");
        for (Pedido p : service.listarPedidos()) {
            System.out.println("#" + p.getId() + " | " + p.getCliente()
                + " | " + p.getProduto() + " | R$" + p.getValorTotal()
                + " | " + p.getStatus());
        }

        // Confirmar pagamento
        System.out.println("\nConfirmando pagamento do pedido #1...");
        service.confirmarPagamento(1);

        System.out.println("\n=== PEDIDOS APOS PAGAMENTO ===");
        for (Pedido p : service.listarPedidos()) {
            System.out.println("#" + p.getId() + " | " + p.getCliente()
                + " | " + p.getProduto() + " | " + p.getStatus());
        }
    }
}
