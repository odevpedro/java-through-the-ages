package mensagens;

import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;

/**
 * Ponto de entrada do lado servidor do sistema RMI.
 *
 * -----------------------------------------------------------------------
 * ARQUITETURA RMI: TRES PROCESSOS
 * -----------------------------------------------------------------------
 * Um sistema RMI classico envolvia tres processos separados:
 *
 *   1. rmiregistry  — diretorio de nomes. O servidor registra servicos
 *                     aqui; o cliente os localiza aqui. Processo separado,
 *                     porta padrao 1099.
 *
 *   2. Servidor     — processo que instancia a implementacao e a registra
 *                     no rmiregistry. Fica em execucao aguardando chamadas.
 *
 *   3. Cliente      — processo que consulta o rmiregistry, obtem o stub
 *                     e chama metodos remotos.
 *
 * Classicamente, o rmiregistry era iniciado como processo separado
 * via linha de comando antes do servidor:
 *
 *   $ rmiregistry 1099 &
 *   $ java mensagens.Servidor
 *   $ java mensagens.Cliente
 *
 * Neste modulo usamos LocateRegistry.createRegistry() para iniciar o
 * registry programaticamente dentro do processo do servidor, eliminando
 * um processo separado. O README documenta a abordagem classica tambem.
 *
 * -----------------------------------------------------------------------
 * NAMING.REBIND()
 * -----------------------------------------------------------------------
 * Naming e a API de alto nivel para acessar o rmiregistry.
 * O formato da URL e: rmi://host:porta/nome
 *
 *   rmi://localhost:1099/ServicoMensagens
 *
 * rebind() registra (ou sobrescreve) um objeto sob aquele nome.
 * O cliente usara Naming.lookup() com a mesma URL para obter o stub.
 *
 * -----------------------------------------------------------------------
 * SECURITY MANAGER
 * -----------------------------------------------------------------------
 * Em JDK 1.1/1.2, sistemas RMI frequentemente exigiam um SecurityManager
 * instalado para controlar permissoes de rede e carregamento de classes
 * remotas (codebase). Em sistemas simples sem codebase remoto, o
 * SecurityManager podia ser omitido — o que fazemos aqui para simplificar.
 *
 * A funcionalidade de codebase remoto (baixar stubs do servidor
 * automaticamente) foi uma das features mais problematicas do RMI:
 * exigia um servidor HTTP para servir os .class, um SecurityManager
 * permissivo e configuracao de java.rmi.server.codebase. Era a fonte
 * de 80% das perguntas sobre RMI em foruns da epoca.
 */
public class Servidor {

    private static final int    PORTA_REGISTRY = 1099;
    private static final String NOME_SERVICO   = "ServicoMensagens";

    public static void main(String[] args) {
        System.out.println("=================================================");
        System.out.println(" Servidor RMI - Sistema de Mensagens (~1997)     ");
        System.out.println("=================================================");

        try {
            /*
             * Passo 1: iniciar o rmiregistry programaticamente.
             *
             * LocateRegistry.createRegistry() inicia um registry local
             * na porta especificada, dentro do processo atual.
             * E equivalente a executar 'rmiregistry 1099' como processo
             * separado, com a diferenca que o registry e encerrado
             * automaticamente quando este processo termina.
             *
             * A referencia retornada pode ser guardada para operacoes
             * diretas no registry (bind, unbind, list), mas para uso
             * simples podemos ignorar o retorno e usar Naming depois.
             */
            LocateRegistry.createRegistry(PORTA_REGISTRY);
            System.out.println("[Servidor] rmiregistry iniciado na porta " + PORTA_REGISTRY);

            /*
             * Passo 2: instanciar a implementacao do servico.
             *
             * O construtor de ServicoMensagensImpl chama super()
             * (UnicastRemoteObject), que exporta o objeto e o torna
             * apto a receber chamadas remotas. A partir deste ponto,
             * o RMI Runtime mantem uma referencia forte ao objeto,
             * impedindo sua coleta pelo GC.
             */
            ServicoMensagens servico = new ServicoMensagensImpl();
            System.out.println("[Servidor] ServicoMensagensImpl criado.");

            /*
             * Passo 3: registrar o servico no registry com um nome.
             *
             * Naming.rebind() converte a URL em uma conexao ao registry
             * local (localhost:1099) e registra o stub do servico sob
             * o nome "ServicoMensagens".
             *
             * O que e registrado NAO e o objeto ServicoMensagensImpl em si,
             * mas um stub: um objeto proxy serializavel que, ao ser
             * transferido para o cliente e invocado, abre uma conexao
             * TCP de volta para este servidor e encaminha a chamada.
             */
            String url = "rmi://localhost:" + PORTA_REGISTRY + "/" + NOME_SERVICO;
            Naming.rebind(url, servico);

            System.out.println("[Servidor] Servico registrado em: " + url);
            System.out.println("[Servidor] Aguardando chamadas remotas...");
            System.out.println("[Servidor] Pressione Ctrl+C para encerrar.");
            System.out.println("");
            System.out.println(" AVISO: todas as mensagens sao armazenadas");
            System.out.println(" em memoria. Serao perdidas ao encerrar.");
            System.out.println("=================================================");

            /*
             * O servidor nao precisa de loop ativo.
             * O RMI Runtime mantem threads internas aguardando conexoes TCP.
             * O processo permanece vivo enquanto houver threads nao-daemon
             * ativas — o proprio Runtime garante isso apos o bind.
             *
             * Em servidores de producao da epoca era comum adicionar um
             * ShutdownHook para limpeza antes de encerrar:
             *
             *   Runtime.getRuntime().addShutdownHook(new Thread() {
             *       public void run() {
             *           try { Naming.unbind(url); } catch (Exception e) {}
             *       }
             *   });
             */

        } catch (Exception e) {
            System.err.println("[Servidor] ERRO FATAL ao iniciar:");
            System.err.println("  " + e.getMessage());
            System.err.println("");
            System.err.println("Causas comuns:");
            System.err.println("  - Porta 1099 ja em uso (outro rmiregistry rodando?)");
            System.err.println("  - Compilacao sem rmic (stubs ausentes) em JDK < 5");
            e.printStackTrace();
            System.exit(1);
        }
    }
}
