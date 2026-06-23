package pedidos;

import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public class PedidoService {
    private PedidoRepository repository;

    public void setRepository(PedidoRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public Pedido criarPedido(String cliente, String produto, int quantidade, double valorTotal) {
        Pedido pedido = new Pedido(cliente, produto, quantidade, valorTotal);
        int id = repository.salvar(pedido);
        pedido.setId(id);
        System.out.println("Pedido #" + id + " criado para " + cliente);
        return pedido;
    }

    @Transactional
    public void confirmarPagamento(int id) {
        repository.atualizarStatus(id, "PAGO");
        System.out.println("Pedido #" + id + " confirmado como PAGO");
    }

    public List<Pedido> listarPedidos() {
        return repository.listar();
    }
}
