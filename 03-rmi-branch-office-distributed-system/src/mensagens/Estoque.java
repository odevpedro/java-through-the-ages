package mensagens;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface Estoque extends Remote {
    List<Produto> listarProdutos() throws RemoteException;
    int consultarDisponibilidade(int idProduto) throws RemoteException;
    boolean reservarProduto(int idProduto, int quantidade) throws RemoteException;
}
