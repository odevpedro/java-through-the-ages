package mensagens;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementacao do servico remoto de mensagens.
 *
 * -----------------------------------------------------------------------
 * UNICASTREMOTEOBJECT
 * -----------------------------------------------------------------------
 * Estender UnicastRemoteObject e a forma mais simples de tornar um objeto
 * acessivel via RMI. UnicastRemoteObject faz duas coisas ao ser
 * instanciado:
 *
 *   1. Exporta o objeto: registra-o no RMI Runtime para receber chamadas
 *      remotas. A partir desse momento, o RMI Runtime mantem uma referencia
 *      ao objeto, impedindo que o Garbage Collector o colete enquanto o
 *      servidor estiver ativo — comportamento importante e nao-obvio.
 *
 *   2. Associa o objeto a uma porta TCP: por padrao uma porta anonima
 *      (randomica). O cliente se conecta a esta porta para invocar metodos.
 *
 * O construtor de UnicastRemoteObject lanca RemoteException, por isso
 * o construtor da subclasse tambem precisa declarar throws RemoteException —
 * uma peculiaridade que surpreendia desenvolvedores novos em RMI.
 *
 * -----------------------------------------------------------------------
 * ESTADO EM MEMORIA
 * -----------------------------------------------------------------------
 * As mensagens sao armazenadas em um ArrayList na memoria do servidor.
 * Todo o estado e perdido quando o processo do servidor e encerrado.
 *
 * Isso era aceitavel para prototipacao e sistemas de curta duracao,
 * mas em producao exigia persistencia real (banco de dados, arquivo).
 * A integracao de RMI com JDBC era possivel mas adicionava camadas
 * significativas de complexidade.
 *
 * -----------------------------------------------------------------------
 * SINCRONIZACAO
 * -----------------------------------------------------------------------
 * RMI pode receber chamadas concorrentes de multiplos clientes em
 * threads distintas. O ArrayList nao e thread-safe. Em producao,
 * o correto seria sincronizar os metodos ou usar Collections.synchronizedList().
 *
 * Este codigo omite a sincronizacao intencionalmente para manter
 * a fidelidade historica: em exemplos da documentacao da Sun de 1997,
 * thread-safety em servidores RMI simples frequentemente era ignorado.
 */
public class ServicoMensagensImpl
        extends UnicastRemoteObject
        implements ServicoMensagens {

    private static final long serialVersionUID = 1L;

    /*
     * Estado do servidor: lista de mensagens em memoria.
     * Sem generics (fidelidade ao JDK 1.1 onde RMI surgiu).
     * Em producao, seria substituido por acesso a banco via JDBC.
     */
    private List mensagens;

    /**
     * O construtor deve declarar throws RemoteException porque
     * UnicastRemoteObject o exige. Nao e possivel suprimir essa
     * declaracao mesmo que o construtor em si nunca lance a excecao —
     * era uma das "verrugas" do design do RMI que desenvolvedores
     * precisavam simplesmente aceitar.
     */
    public ServicoMensagensImpl() throws RemoteException {
        super(); // chama UnicastRemoteObject(), que exporta este objeto
        mensagens = new ArrayList();
        System.out.println("[Servidor] ServicoMensagensImpl instanciado e exportado.");
    }

    /**
     * Adiciona uma mensagem ao repositorio em memoria.
     *
     * Chamadas remotas chegam em threads gerenciadas pelo RMI Runtime.
     * O System.out.println aqui imprime no console do SERVIDOR,
     * nao no console do cliente — um detalhe confuso para iniciantes
     * em computacao distribuida que esperavam ver o log do lado que
     * fez a chamada.
     */
    public void adicionarMensagem(String autor, String conteudo)
            throws RemoteException {

        if (autor == null || autor.trim().length() == 0) {
            /*
             * Lancar IllegalArgumentException atraves da rede RMI:
             * excecoes que nao sao RemoteException sao encapsuladas
             * em RemoteException no stub e relancadas no cliente.
             * Na pratica, o cliente recebe a excecao original envelopada.
             */
            throw new IllegalArgumentException("Autor nao pode ser vazio.");
        }
        if (conteudo == null || conteudo.trim().length() == 0) {
            throw new IllegalArgumentException("Conteudo nao pode ser vazio.");
        }

        Mensagem nova = new Mensagem(autor.trim(), conteudo.trim());
        mensagens.add(nova);

        System.out.println("[Servidor] Mensagem adicionada de: " + autor
                + " (total: " + mensagens.size() + ")");
    }

    /**
     * Retorna uma copia da lista de mensagens para o cliente.
     *
     * IMPORTANTE: retornamos uma nova ArrayList (copia defensiva).
     * Se retornassemos a lista interna diretamente, o RMI a serializaria
     * e enviaria ao cliente — o cliente receberia uma copia de qualquer
     * forma (serializacao copia por valor). Mas retornar a referencia
     * interna diretamente seria um problema se o metodo fosse chamado
     * localmente (sem RMI), pois o cliente poderia mutar o estado interno.
     * A copia defensiva e uma boa pratica independente de RMI.
     */
    public List listarMensagens() throws RemoteException {
        System.out.println("[Servidor] listarMensagens() chamado. Enviando "
                + mensagens.size() + " mensagem(ns).");
        return new ArrayList(mensagens);
    }

    public int contarMensagens() throws RemoteException {
        return mensagens.size();
    }
}
