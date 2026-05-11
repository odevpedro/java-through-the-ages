package mensagens;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

/**
 * Interface remota do servico de mensagens.
 *
 * -----------------------------------------------------------------------
 * O CONTRATO DO RMI
 * -----------------------------------------------------------------------
 * Esta interface e o contrato entre cliente e servidor num sistema RMI.
 * O cliente so conhece esta interface — nunca a implementacao concreta.
 * O servidor implementa esta interface. O RMI Runtime gera um "stub"
 * (proxy no cliente) e um "skeleton" (receptor no servidor) que fazem
 * a comunicacao transparente: do ponto de vista do cliente, chamar
 * um metodo nesta interface parece identico a uma chamada local.
 *
 * Essa "transparencia de localizacao" era a promessa central do RMI:
 * "objetos distribuidos que se comportam como objetos locais."
 * Na pratica, a transparencia era uma ilusao perigosa — ver secao
 * de limitacoes no README.
 *
 * -----------------------------------------------------------------------
 * REGRAS DE UMA INTERFACE REMOTA
 * -----------------------------------------------------------------------
 * 1. Deve estender java.rmi.Remote (interface marcadora, sem metodos).
 *
 * 2. Todo metodo DEVE declarar throws RemoteException.
 *    RemoteException e lancada pelo stub quando qualquer problema de
 *    rede ocorre: timeout, servidor indisponivel, falha de serializacao.
 *    Ela e checked (verificada em tempo de compilacao), forcando o cliente
 *    a tratar explicitamente a possibilidade de falha de rede — o que
 *    e correto do ponto de vista de design, embora verboso.
 *
 * 3. Todos os parametros e valores de retorno devem ser primitivos,
 *    Serializable, ou eles proprios Remote.
 *    - Primitivos: copiados por valor diretamente.
 *    - Serializable: copiados por valor via serializacao.
 *    - Remote: passados por referencia remota (stub).
 *
 * -----------------------------------------------------------------------
 * LISTA COMO VALOR DE RETORNO
 * -----------------------------------------------------------------------
 * listarMensagens() retorna List (sem generics — era JDK 1.1 quando RMI
 * surgiu; generics vieram no Java 5). A lista inteira e serializada e
 * copiada para o cliente a cada chamada. Com muitas mensagens, isso se
 * tornaria um gargalo de performance significativo — um problema que
 * Web Services e APIs REST resolveram com paginacao.
 */
public interface ServicoMensagens extends Remote {

    /**
     * Adiciona uma nova mensagem no servidor.
     *
     * @param autor    identificacao de quem envia a mensagem
     * @param conteudo texto da mensagem
     * @throws RemoteException se a comunicacao com o servidor falhar
     */
    void adicionarMensagem(String autor, String conteudo) throws RemoteException;

    /**
     * Retorna todas as mensagens armazenadas no servidor.
     * A lista inteira e serializada e transferida para o cliente.
     *
     * @return lista de Mensagem; never null
     * @throws RemoteException se a comunicacao com o servidor falhar
     */
    List listarMensagens() throws RemoteException;

    /**
     * Retorna o numero de mensagens armazenadas.
     * Metodo utilitario para evitar transferir a lista inteira apenas
     * para contar elementos — exemplo de otimizacao de trafego de rede.
     *
     * @return numero de mensagens no servidor
     * @throws RemoteException se a comunicacao com o servidor falhar
     */
    int contarMensagens() throws RemoteException;
}
