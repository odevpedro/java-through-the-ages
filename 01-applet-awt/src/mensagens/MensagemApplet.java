package mensagens;

import java.applet.Applet;
import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Panel;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;
import java.util.Vector;

/**
 * Ponto de entrada da aplicacao: um Applet AWT para gerenciar mensagens.
 *
 * -----------------------------------------------------------------------
 * CONTEXTO HISTORICO
 * -----------------------------------------------------------------------
 * Em 1995-1996 a promessa do Java era executar codigo no browser sem
 * instalacao. O modelo Applet era a concretizacao disso: o browser
 * baixava o .class (ou .jar) e o executava numa JVM embutida no plugin.
 *
 * AWT (Abstract Window Toolkit) era o unico toolkit de UI disponivel.
 * Ele delegava a renderizacao para os componentes nativos do sistema
 * operacional (win32, Motif, Mac Toolbox), o que causava comportamento
 * inconsistente entre plataformas — problema que o Swing (JDK 1.2)
 * resolveu ao desenhar todos os componentes em Java puro.
 *
 * -----------------------------------------------------------------------
 * MODELO DE EVENTOS
 * -----------------------------------------------------------------------
 * JDK 1.0 usava o modelo de eventos por heranca: o applet sobrescrevia
 * handleEvent(Event e) ou action(Event e, Object o) e inspecionava
 * o campo e.target para saber qual componente disparou o evento.
 * Era frágil e nao escalava.
 *
 * JDK 1.1 (1997) introduziu o modelo de delegacao: interfaces Listener
 * (ActionListener, MouseListener, etc.) registradas diretamente nos
 * componentes. Este arquivo usa o modelo JDK 1.1, que se tornou padrao
 * e e reconhecivel ainda hoje no Swing moderno.
 *
 * -----------------------------------------------------------------------
 * CICLO DE VIDA
 * -----------------------------------------------------------------------
 * init()    -> chamado uma vez ao carregar o applet
 * start()   -> chamado ao tornar-se visivel (ex.: usuario volta a aba)
 * stop()    -> chamado ao sair de vista
 * destroy() -> chamado ao descarregar (fechar browser ou navegar para fora)
 */
public class MensagemApplet extends Applet implements ActionListener {

    /**
     * Vector era a unica colecao dinamica disponivel no JDK 1.0.
     * ArrayList so foi introduzido no JDK 1.2 junto com o Collections Framework.
     *
     * Vector e sincronizado em todos os metodos — custo de locking em
     * single-thread que passou a ser evitado com ArrayList (nao sincronizado).
     * Aqui nao ha generics: o tipo do elemento e Object, exigindo cast explicito
     * ao recuperar elementos — fonte comum de ClassCastException na epoca.
     */
    private Vector mensagens; // equivalente a Vector<Mensagem> em Java 5+

    // --- componentes AWT ---
    private TextField campoAutor;
    private TextArea  campoConteudo;
    private TextArea  areaExibicao;
    private Button    botaoAdicionar;
    private Button    botaoLimpar;
    private Label     rotuloStatus;

    // -----------------------------------------------------------------------
    // CICLO DE VIDA DO APPLET
    // -----------------------------------------------------------------------

    /**
     * init() e o equivalente ao main() para Applets.
     * Toda a construcao da UI e feita aqui, nao no construtor,
     * porque o contexto grafico (peer nativo) ainda nao existe
     * quando o construtor e chamado pelo ClassLoader do browser.
     */
    public void init() {
        mensagens = new Vector();

        /*
         * BorderLayout divide o container em cinco regioes:
         * NORTH, SOUTH, EAST, WEST e CENTER.
         * Era o layout mais usado para estruturar janelas em AWT.
         */
        setLayout(new BorderLayout(4, 4));
        setBackground(Color.lightGray);

        add(construirPainelFormulario(), BorderLayout.NORTH);
        add(construirPainelLista(),      BorderLayout.CENTER);
        add(construirBarraStatus(),      BorderLayout.SOUTH);
    }

    /** Chamado toda vez que o applet se torna visivel. */
    public void start() {
        atualizarStatus("Applet ativo. Mensagens em memoria: " + mensagens.size());
    }

    /**
     * Chamado quando o applet sai de vista.
     * Aqui seria o lugar para pausar threads de animacao, se existissem.
     * Nao ha threads neste exemplo.
     */
    public void stop() {
        // sem estado a pausar neste exemplo
    }

    /**
     * destroy() e chamado pelo browser ao descarregar o applet.
     *
     * LIMITACAO CRITICA DO MODELO APPLET:
     * O sandbox de seguranca impedia que o applet gravasse no sistema
     * de arquivos local. Isso significa que TODO o estado (lista de
     * mensagens) era perdido ao fechar o browser ou navegar para fora.
     * Persistencia exigia comunicacao com um servidor via URLConnection
     * ou sockets — complexidade significativa para a epoca.
     */
    public void destroy() {
        mensagens = null; // libera a referencia; o GC faz o resto
    }

    // -----------------------------------------------------------------------
    // CONSTRUCAO DA UI
    // -----------------------------------------------------------------------

    private Panel construirPainelFormulario() {
        /*
         * GridLayout divide o container em uma grade de linhas x colunas.
         * Todos os celulas tem o mesmo tamanho — limitacao que forcava
         * layouts rigidos ou o aninhamento de multiplos Panels.
         * O aninhamento excessivo de Panels para composicao de layout
         * era a queixa mais frequente de desenvolvedores AWT.
         */
        Panel painel = new Panel(new GridLayout(3, 2, 4, 4));
        painel.setBackground(Color.lightGray);

        painel.add(new Label("Autor:"));
        campoAutor = new TextField(30);
        painel.add(campoAutor);

        painel.add(new Label("Conteudo:"));
        /*
         * TextArea com 3 linhas x 50 colunas.
         * O tamanho em colunas era uma aproximacao baseada em caracteres
         * em fonte monoespaco — impreciso com fontes proporcionais,
         * mas era o unico mecanismo de dimensionamento disponivel.
         */
        campoConteudo = new TextArea(3, 50);
        painel.add(campoConteudo);

        Panel painelBotoes = new Panel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        botaoAdicionar = new Button("Adicionar");
        botaoLimpar    = new Button("Limpar");

        /*
         * O modelo de delegacao do JDK 1.1: registrar um ActionListener
         * diretamente no componente, em vez de sobrescrever handleEvent()
         * no container pai (modelo JDK 1.0).
         */
        botaoAdicionar.addActionListener(this);
        botaoLimpar.addActionListener(this);

        painelBotoes.add(botaoAdicionar);
        painelBotoes.add(botaoLimpar);

        painel.add(new Label(""));  // celula vazia para alinhar os botoes
        painel.add(painelBotoes);

        return painel;
    }

    private Panel construirPainelLista() {
        Panel painel = new Panel(new BorderLayout(0, 2));
        painel.add(new Label("Mensagens registradas:"), BorderLayout.NORTH);

        areaExibicao = new TextArea(10, 60);
        areaExibicao.setEditable(false);  // somente leitura
        areaExibicao.setBackground(Color.white);
        painel.add(areaExibicao, BorderLayout.CENTER);

        return painel;
    }

    private Label construirBarraStatus() {
        rotuloStatus = new Label("Pronto. Nenhuma mensagem registrada.");
        rotuloStatus.setBackground(Color.gray);
        rotuloStatus.setForeground(Color.white);
        return rotuloStatus;
    }

    // -----------------------------------------------------------------------
    // TRATAMENTO DE EVENTOS
    // -----------------------------------------------------------------------

    /**
     * Metodo unico da interface ActionListener.
     * Em aplicacoes com muitos botoes era comum usar um if-else-if
     * verificando a referencia da fonte (getSource()) ou o texto do
     * comando (getActionCommand()). Nao havia @FXML, bindings nem lambdas.
     */
    public void actionPerformed(ActionEvent evento) {
        if (evento.getSource() == botaoAdicionar) {
            adicionarMensagem();
        } else if (evento.getSource() == botaoLimpar) {
            limparFormulario();
        }
    }

    // -----------------------------------------------------------------------
    // LOGICA DE NEGOCIO
    // -----------------------------------------------------------------------

    private void adicionarMensagem() {
        String autor    = campoAutor.getText().trim();
        String conteudo = campoConteudo.getText().trim();

        /*
         * Validacao manual de campos obrigatorios.
         * Nao havia Bean Validation (JSR-303), anotacoes @NotNull
         * nem frameworks de validacao em 1996.
         *
         * String.isEmpty() nao existia em JDK 1.0/1.1; a verificacao
         * era feita com length() == 0 ou equals("").
         */
        if (autor.length() == 0) {
            atualizarStatus("ERRO: o campo 'autor' e obrigatorio.");
            campoAutor.requestFocus();
            return;
        }
        if (conteudo.length() == 0) {
            atualizarStatus("ERRO: o campo 'conteudo' e obrigatorio.");
            campoConteudo.requestFocus();
            return;
        }

        Mensagem nova = new Mensagem(autor, conteudo);

        /*
         * addElement() e o metodo de insercao do Vector.
         * Em JDK 1.2+ o Collections Framework unificou a API com add(),
         * mas addElement() permaneceu por compatibilidade retroativa —
         * filosofia que o Java manteve por decadas.
         */
        mensagens.addElement(nova);

        atualizarAreaExibicao();
        atualizarStatus("Mensagem de '" + autor + "' registrada. Total: " + mensagens.size());
        limparFormulario();
    }

    private void atualizarAreaExibicao() {
        /*
         * StringBuffer desde JDK 1.0 — sincronizado em todos os metodos.
         * StringBuilder (nao sincronizado, mais rapido em single-thread)
         * so foi introduzido no Java SE 5 (2004).
         *
         * Em 1996, a diferenca de performance entre StringBuffer e StringBuilder
         * era irrelevante — nao havia CPUs multi-core e os apps eram simples.
         */
        StringBuffer sb = new StringBuffer();

        /*
         * Enumeration e a interface de iteracao do JDK 1.0/1.1.
         * Iterator so existiria a partir do JDK 1.2 (Collections Framework).
         *
         * Dois metodos: hasMoreElements() e nextElement().
         * nextElement() retorna Object — cast explicito obrigatorio;
         * sem generics, um cast errado resultava em ClassCastException em runtime.
         */
        Enumeration elementos = mensagens.elements();
        int indice = 1;
        while (elementos.hasMoreElements()) {
            Mensagem m = (Mensagem) elementos.nextElement();
            sb.append(indice + ". " + m.toString());
            sb.append("\n----------------------------------------\n");
            indice++;
        }

        if (sb.length() == 0) {
            sb.append("(nenhuma mensagem registrada)");
        }

        areaExibicao.setText(sb.toString());
        /*
         * Nao havia metodo scrollToBottom() ou similar em TextArea.
         * Desenvolvedores usavam setCaretPosition(text.length()) no Swing,
         * mas em AWT a alternativa era append() em vez de setText(),
         * que mantinha o scroll no fim automaticamente.
         */
    }

    private void limparFormulario() {
        campoAutor.setText("");
        campoConteudo.setText("");
        campoAutor.requestFocus();
    }

    private void atualizarStatus(String mensagem) {
        rotuloStatus.setText(mensagem);
    }
}
