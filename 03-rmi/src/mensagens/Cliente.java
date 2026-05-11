package mensagens;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

/**
 * Cliente do sistema de mensagens via RMI.
 *
 * -----------------------------------------------------------------------
 * O QUE O CLIENTE VE
 * -----------------------------------------------------------------------
 * Do ponto de vista do cliente, ServicoMensagens e apenas uma interface
 * Java local. O cliente nao sabe (e nao precisa saber) que a implementacao
 * esta em outro processo, possivelmente em outra maquina.
 *
 * O que acontece de verdade ao chamar servico.adicionarMensagem():
 *
 *   1. O cliente chama o metodo no STUB (proxy local gerado pelo rmic).
 *   2. O stub serializa os argumentos ("autor", "conteudo") em bytes.
 *   3. O stub abre uma conexao TCP para o servidor (ip:porta).
 *   4. O skeleton (receptor no servidor) desserializa os argumentos.
 *   5. O skeleton chama o metodo real em ServicoMensagensImpl.
 *   6. O resultado e serializado e enviado de volta.
 *   7. O stub desserializa o resultado e o retorna ao chamador.
 *
 * Toda essa comunicacao e sincrona: o cliente BLOQUEIA aguardando a
 * resposta do servidor. Nao havia chamadas assincronas nativas em RMI.
 *
 * -----------------------------------------------------------------------
 * NAMING.LOOKUP()
 * -----------------------------------------------------------------------
 * Naming.lookup() conecta ao rmiregistry no host/porta indicados,
 * busca o stub registrado sob o nome especificado e o retorna como Object.
 *
 * O cast para ServicoMensagens e necessario e seguro apenas porque
 * cliente e servidor concordam sobre o contrato da interface.
 * Se a interface no cliente e no servidor tiverem serialVersionUIDs
 * diferentes (por exemplo, apos uma refatoracao parcial), o lookup
 * pode ter sucesso mas os metodos falhariam com ClassCastException
 * ou erros de deserializacao em runtime.
 *
 * -----------------------------------------------------------------------
 * CENARIO DEMONSTRADO
 * -----------------------------------------------------------------------
 * Este cliente demonstra o ciclo completo:
 *   1. Conectar ao registry e obter o stub
 *   2. Verificar estado inicial (zero mensagens)
 *   3. Adicionar mensagens
 *   4. Listar mensagens recebidas do servidor
 *   5. Demonstrar tratamento de RemoteException
 */
public class Cliente {

    private static final String URL_SERVICO =
            "rmi://localhost:1099/ServicoMensagens";

    public static void main(String[] args) {
        System.out.println("=================================================");
        System.out.println(" Cliente RMI - Sistema de Mensagens (~1997)      ");
        System.out.println("=================================================");

        ServicoMensagens servico = null;

        // --- PASSO 1: localizar o servico no registry ---
        System.out.println("\n[Cliente] Conectando ao registry em: " + URL_SERVICO);
        try {
            /*
             * Naming.lookup() retorna Object; o cast para a interface remota
             * e obrigatorio. Sem generics (JDK 1.1 nao os tinha), era
             * responsabilidade do desenvolvedor garantir a consistencia.
             *
             * A excecao NotBoundException indica que nenhum servico foi
             * registrado sob aquele nome — servidor nao iniciado ou nome errado.
             */
            servico = (ServicoMensagens) Naming.lookup(URL_SERVICO);
            System.out.println("[Cliente] Stub obtido com sucesso.");
        } catch (java.rmi.NotBoundException e) {
            System.err.println("[Cliente] ERRO: servico nao encontrado no registry.");
            System.err.println("  Certifique-se de que o Servidor esta rodando.");
            System.exit(1);
        } catch (Exception e) {
            System.err.println("[Cliente] ERRO ao conectar: " + e.getMessage());
            System.err.println("  O servidor esta rodando na porta 1099?");
            e.printStackTrace();
            System.exit(1);
        }

        // --- PASSO 2: verificar estado inicial ---
        try {
            System.out.println("\n[Cliente] Mensagens no servidor antes de adicionar: "
                    + servico.contarMensagens());
        } catch (RemoteException e) {
            System.err.println("[Cliente] Falha ao contar mensagens: " + e.getMessage());
        }

        // --- PASSO 3: adicionar mensagens ---
        System.out.println("\n[Cliente] Adicionando mensagens...");

        adicionarComTratamento(servico, "Ada Lovelace",
                "A maquina analitica tece resultados algebricos como "
                + "a Jacquard tece flores e folhas.");

        adicionarComTratamento(servico, "Alan Turing",
                "Uma maquina pode pensar. A questao e se alguem se importa.");

        adicionarComTratamento(servico, "Grace Hopper",
                "O navio mais perigoso e um navio encalhado. "
                + "Navegar e preciso.");

        // demonstrar validacao: autor vazio deve ser rejeitado pelo servidor
        System.out.println("\n[Cliente] Tentando adicionar mensagem com autor vazio...");
        adicionarComTratamento(servico, "", "Esta mensagem deve ser rejeitada.");

        // --- PASSO 4: listar mensagens ---
        System.out.println("\n[Cliente] Solicitando lista de mensagens ao servidor...");
        try {
            List mensagens = servico.listarMensagens();

            /*
             * A lista foi serializada no servidor, transmitida pela rede
             * e desserializada aqui no cliente. Do ponto de vista do codigo,
             * e uma List Java normal.
             *
             * Usamos Iterator (JDK 1.2) em vez de Enumeration (JDK 1.0)
             * porque o cliente pode ser mais recente que a API de 1997.
             * Em um cliente JDK 1.1 puro, seria Enumeration.
             */
            System.out.println("[Cliente] " + mensagens.size()
                    + " mensagem(ns) recebida(s) do servidor:\n");

            Iterator it = mensagens.iterator();
            int indice = 1;
            while (it.hasNext()) {
                Mensagem m = (Mensagem) it.next();
                System.out.println("  " + indice + ". " + m);
                indice++;
            }

        } catch (RemoteException e) {
            System.err.println("[Cliente] Falha ao listar mensagens: " + e.getMessage());
        }

        // --- PASSO 5: mostrar total final ---
        try {
            System.out.println("\n[Cliente] Total no servidor: "
                    + servico.contarMensagens() + " mensagem(ns).");
        } catch (RemoteException e) {
            System.err.println("[Cliente] Falha ao contar: " + e.getMessage());
        }

        System.out.println("\n[Cliente] Execucao concluida.");
        System.out.println("=================================================");
        System.out.println(" Nota: as mensagens existem apenas na memoria    ");
        System.out.println(" do servidor. Serao perdidas ao encerra-lo.      ");
        System.out.println("=================================================");
    }

    /**
     * Auxiliar: adiciona uma mensagem e trata excecoes de forma
     * didaticamente explicita, em vez de propagar com throws.
     *
     * RemoteException deve ser sempre tratada no cliente RMI.
     * Ela indica que a chamada de rede falhou — o servidor pode ter
     * caido, a rede pode ter oscilado, ou o servidor pode ter lancado
     * uma excecao propria (wrapped em RemoteException).
     */
    private static void adicionarComTratamento(
            ServicoMensagens servico,
            String autor,
            String conteudo) {
        try {
            servico.adicionarMensagem(autor, conteudo);
            System.out.println("  [OK] Mensagem de '" + autor + "' adicionada.");
        } catch (RemoteException e) {
            /*
             * RemoteException envelopa a causa real.
             * getCause() retorna a excecao original lancada no servidor
             * (ex: IllegalArgumentException para autor vazio).
             */
            Throwable causa = e.getCause() != null ? e.getCause() : e;
            System.out.println("  [ERRO] " + causa.getMessage());
        } catch (IllegalArgumentException e) {
            /*
             * Em runtimes RMI modernos, excecoes RuntimeException lancadas
             * pelo servidor podem chegar ao cliente preservadas, sem wrapper
             * RemoteException. O cliente precisa tratar os dois formatos.
             */
            System.out.println("  [ERRO] " + e.getMessage());
        }
    }
}
