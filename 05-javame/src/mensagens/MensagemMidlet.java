package mensagens;

import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;

/**
 * Ponto de entrada da aplicacao Java ME: o MIDlet principal.
 *
 * -----------------------------------------------------------------------
 * O QUE E UM MIDLET
 * -----------------------------------------------------------------------
 * MIDlet e a unidade de aplicacao do MIDP — o equivalente a uma
 * atividade Android, ou a um Applet no contexto do browser.
 * O AMS (Application Management Software) do dispositivo instancia
 * o MIDlet e chama seus metodos de ciclo de vida.
 *
 * Um JAR pode conter multiplos MIDlets (uma "MIDlet suite"). Cada MIDlet
 * e listado no arquivo .jad e no MANIFEST.MF, identificado por:
 *   MIDlet-1: Nome, icone, classe
 *   MIDlet-2: Nome, icone, classe
 *
 * -----------------------------------------------------------------------
 * CICLO DE VIDA DO MIDLET
 * -----------------------------------------------------------------------
 * Tres estados: Paused, Active, Destroyed.
 *
 *   startApp()    -> Paused -> Active
 *                   Chamado quando o AMS traz o MIDlet para primeiro plano.
 *                   Pode ser chamado multiplas vezes (ex: ligacao recebida
 *                   interrompe o MIDlet; ao terminar a ligacao, startApp()
 *                   e chamado novamente).
 *                   IMPORTANTE: nao confundir com "iniciado pela primeira vez".
 *                   Para inicializacao unica, use o construtor.
 *
 *   pauseApp()    -> Active -> Paused
 *                   Chamado quando outra aplicacao toma o primeiro plano
 *                   (ligacao entrante, SMS, outra app). O MIDlet deve
 *                   liberar recursos que nao precisa em background
 *                   (conexoes de rede, acesso ao microfone).
 *                   Em dispositivos simples de 2005, raramente havia
 *                   multitarefa real — pauseApp() significava "voce sumiu".
 *
 *   destroyApp(unconditional) -> Active/Paused -> Destroyed
 *                   Chamado ao encerrar. Se unconditional=false, o MIDlet
 *                   pode lancar MIDletStateChangeException para recusar
 *                   o encerramento (ex: dados nao salvos). O AMS pode
 *                   entao chamar novamente com unconditional=true, que
 *                   nao pode ser recusado.
 *
 * -----------------------------------------------------------------------
 * DISPLAY E NAVEGACAO ENTRE TELAS
 * -----------------------------------------------------------------------
 * Display.getDisplay(midlet) retorna o objeto Display associado ao MIDlet.
 * Existe exatamente um Display por MIDlet.
 *
 * Display.setCurrent(displayable) troca a tela visivel.
 * Nao ha "stack de telas" nativo — o desenvolvedor gerenciava a navegacao
 * guardando referencias para as telas e chamando setCurrent() manualmente.
 * Era responsabilidade do codigo manter o controle de "onde estou" e
 * "para onde voltar" — identico ao gerenciamento manual de back stack
 * que o Android Activity Manager automatizaria em 2008.
 *
 * -----------------------------------------------------------------------
 * GERENCIAMENTO DE MEMORIA
 * -----------------------------------------------------------------------
 * Com 128 KB de heap, cada objeto importava. Praticas comuns:
 * - Reusar instancias de Displayable em vez de recriar
 * - Usar null para liberar objetos grandes quando nao necessarios
 * - Evitar String concatenation em loops (usar StringBuffer)
 * - Evitar arrays multidimensionais
 * - Preferir tipos primitivos a wrappers (int vs Integer)
 *
 * O GC dos dispositivos era menos sofisticado que o da JVM desktop.
 * Pauses de GC eram visiveis ao usuario (tela congelada por 200-500ms).
 */
public class MensagemMidlet extends MIDlet {

    /*
     * Referências para as telas — mantidas vivas para reutilizacao.
     * Criar new TelaLista() toda vez seria caro em termos de GC.
     *
     * TelaAdicionar e instanciada lazily (apenas quando necessaria)
     * para economizar memoria na inicializacao — pratica comum em ME.
     */
    private TelaLista    telaLista;
    private TelaAdicionar telaAdicionar;
    private Display       display;

    /**
     * Construtor: inicializacao de campos simples apenas.
     * Nao inicializar Display aqui — o Display so e acessivel
     * apos o construtor retornar (durante o startApp()).
     */
    public MensagemMidlet() {
        // construtor intencionalmente vazio
        // inicializacao no startApp()
    }

    /**
     * startApp(): inicializacao da UI e apresentacao da primeira tela.
     *
     * Pode ser chamado multiplas vezes (pauseApp -> startApp).
     * Verificamos se jah inicializamos para nao recriar as telas.
     */
    protected void startApp() throws MIDletStateChangeException {
        if (display == null) {
            display   = Display.getDisplay(this);
            telaLista = new TelaLista(this);
        }
        display.setCurrent(telaLista);
    }

    /**
     * pauseApp(): MIDlet vai para background.
     * Neste caso simples, nao ha recursos a liberar.
     * Em MIDlets com conexao de rede ou audio, fecharíamos esses recursos aqui.
     */
    protected void pauseApp() {
        // sem recursos externos a liberar
    }

    /**
     * destroyApp(): encerramento da aplicacao.
     *
     * unconditional=true: encerramento forcado pelo AMS — nao pode ser recusado.
     * unconditional=false: encerramento solicitado — poderia ser recusado com
     *   throw new MIDletStateChangeException(), mas aqui sempre aceitamos.
     *
     * O RMS e gerenciado externamente (os dados persistem apos o encerramento),
     * entao nao ha estado a salvar aqui.
     */
    protected void destroyApp(boolean unconditional) throws MIDletStateChangeException {
        // dados jah persistidos no RMS apos cada operacao
        // nada a salvar aqui
    }

    // -----------------------------------------------------------------------
    // NAVEGACAO ENTRE TELAS
    // -----------------------------------------------------------------------

    /**
     * Exibe a tela de adicao de mensagem.
     * Instancia TelaAdicionar lazily — apenas na primeira chamada.
     */
    public void exibirFormularioAdicionar() {
        if (telaAdicionar == null) {
            telaAdicionar = new TelaAdicionar(this);
        } else {
            telaAdicionar.limpar();
        }
        display.setCurrent(telaAdicionar);
    }

    /**
     * Exibe os detalhes de uma mensagem selecionada usando Alert.
     *
     * Alert e o componente ideal para exibir informacoes de forma
     * modal: mostra um titulo, texto e opcionalmente um icone,
     * e fecha apos um timeout (FOREVER = aguarda input do usuario).
     */
    public void exibirDetalhe(Mensagem mensagem) {
        /*
         * Alert(titulo, texto, imagem, tipo)
         *
         * AlertType define o icone e o som:
         *   ALARM, CONFIRMATION, ERROR, INFO, WARNING
         *
         * O som era reproduzido pelo dispositivo automaticamente ao exibir
         * o Alert — som diferente por tipo. Desenvolvedor sem controle sobre isso.
         */
        String texto =
            "De: " + mensagem.getAutor() + "\n" +
            "Em: " + mensagem.getDataFormatada() + "\n\n" +
            mensagem.getConteudo();

        Alert alerta = new Alert(
            "Mensagem",
            texto,
            null,           // sem imagem
            AlertType.INFO
        );

        /*
         * Alert.FOREVER = nao fecha automaticamente.
         * Sem isso, o Alert fecharia apos um timeout do dispositivo
         * (geralmente 2-5 segundos), retornando a tela anterior.
         */
        alerta.setTimeout(Alert.FOREVER);

        /*
         * setCurrent(alert, tela_seguinte): exibe o Alert e define
         * para qual tela ir apos o usuario dispensar o Alert.
         * Isso evita a necessidade de adicionar um Command "Voltar"
         * no Alert manualmente.
         */
        display.setCurrent(alerta, telaLista);
    }

    /**
     * Solicita confirmacao para limpar todas as mensagens.
     * Alert com tipo CONFIRMATION e Commands de Sim/Nao.
     */
    public void confirmarLimpar() {
        final Command cmdSim = new Command("Sim", Command.OK,   1);
        final Command cmdNao = new Command("Nao", Command.BACK, 2);

        Alert confirmacao = new Alert(
            "Limpar tudo",
            "Remover todas as mensagens?",
            null,
            AlertType.WARNING
        );
        confirmacao.setTimeout(Alert.FOREVER);
        confirmacao.addCommand(cmdSim);
        confirmacao.addCommand(cmdNao);

        /*
         * Classe anonima como CommandListener — exatamente como no modulo 02
         * (Swing), mas em contexto MIDP. Lambdas nao existem no CLDC 1.1.
         *
         * A classe anonima captura 'this' (o MIDlet) e 'telaLista' do
         * escopo envolvente — equivalente a um lambda com closure.
         */
        confirmacao.setCommandListener(new CommandListener() {
            public void commandAction(Command cmd, Displayable d) {
                if (cmd == cmdSim) {
                    try {
                        new RepositorioRMS().limparTudo();
                        telaLista.carregarMensagens();
                    } catch (Exception e) {
                        exibirAlerta("Erro ao limpar: " + e.getMessage());
                        return;
                    }
                }
                display.setCurrent(telaLista);
            }
        });

        display.setCurrent(confirmacao);
    }

    /**
     * Volta para a tela de lista.
     * @param recarregar true se a lista deve ser recarregada do RMS
     */
    public void voltarParaLista(boolean recarregar) {
        if (recarregar) {
            telaLista.carregarMensagens();
        }
        display.setCurrent(telaLista);
    }

    /**
     * Exibe um Alert de erro simples com timeout curto.
     */
    public void exibirAlerta(String mensagem) {
        Alert alerta = new Alert("Aviso", mensagem, null, AlertType.ERROR);
        alerta.setTimeout(3000); // 3 segundos
        display.setCurrent(alerta, display.getCurrent());
    }
}
