package mensagens;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

public class EstoqueImpl extends UnicastRemoteObject implements Estoque {

    private final List<Produto> estoque;

    public EstoqueImpl() throws RemoteException {
        super();
        estoque = new ArrayList<>();
        estoque.add(new Produto(1, "Teclado Mecanico", 50));
        estoque.add(new Produto(2, "Mouse Optico", 120));
        estoque.add(new Produto(3, "Monitor LED 24", 30));
        estoque.add(new Produto(4, "Webcam HD", 80));
        estoque.add(new Produto(5, "Fone Bluetooth", 65));
    }

    public List<Produto> listarProdutos() throws RemoteException {
        return new ArrayList<>(estoque);
    }

    public int consultarDisponibilidade(int idProduto) throws RemoteException {
        for (Produto p : estoque) {
            if (p.getId() == idProduto) {
                return p.getQuantidadeEmEstoque();
            }
        }
        return -1;
    }

    public boolean reservarProduto(int idProduto, int quantidade) throws RemoteException {
        for (Produto p : estoque) {
            if (p.getId() == idProduto) {
                return p.reservar(quantidade);
            }
        }
        return false;
    }
}
