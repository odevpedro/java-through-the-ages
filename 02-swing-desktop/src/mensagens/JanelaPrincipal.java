package mensagens;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * Janela principal da aplicacao Swing de mensagens.
 *
 * -----------------------------------------------------------------------
 * SWING vs AWT
 * -----------------------------------------------------------------------
 * O Swing (JDK 1.2, 1998) substituiu o AWT como toolkit padrao de UI.
 * A diferenca fundamental: componentes Swing sao desenhados em Java puro,
 * sem delegar para componentes nativos do SO. Isso resolve o problema de
 * inconsistencia visual entre plataformas, mas introduz um novo requisito:
 * todo acesso a componentes Swing deve ocorrer na Event Dispatch Thread (EDT).
 *
 * -----------------------------------------------------------------------
 * EVENT DISPATCH THREAD (EDT)
 * -----------------------------------------------------------------------
 * Swing nao e thread-safe. Todos os acessos a componentes (leitura e
 * escrita de estado) devem ocorrer em uma unica thread: a EDT.
 *
 * Violar isso causa bugs sutis e intermitentes: telas que nao atualizam,
 * componentes que travam, deadlocks. Em 1998 essa restricao nao estava
 * bem documentada; muitos desenvolvedores a descobriam por trial and error.
 *
 * A forma correta de submeter trabalho a EDT e:
 *   SwingUtilities.invokeLater(Runnable)   -> agenda para execucao futura
 *   SwingUtilities.invokeAndWait(Runnable) -> bloqueia ate executar
 *
 * Neste arquivo, o main() usa invokeLater() para construir a UI na EDT,
 * que e o padrao correto segundo a documentacao do JDK 1.5+.
 * Em JDK 1.2/1.3, muitos exemplos oficiais da Sun construiam a UI
 * diretamente no main() (na main thread), o que era tecnicamente incorreto
 * mas raramente causava problemas em UIs simples e single-threaded.
 *
 * -----------------------------------------------------------------------
 * GRIDBAGLAOUT
 * -----------------------------------------------------------------------
 * GridBagLayout e o layout mais poderoso e verboso do AWT/Swing.
 * Permite que componentes ocupem multiplas celulas, tenham pesos
 * de expansao diferentes e insets (margens) customizados.
 *
 * Era o layout preferido para formularios complexos antes de
 * ferramentas visuais como o NetBeans GUI Builder (Matisse, 2005)
 * gerarem o codigo automaticamente. Escrever GridBagConstraints
 * manualmente era considerado uma das tarefas mais tediosas do
 * desenvolvimento Swing.
 */
public class JanelaPrincipal extends JFrame {

    private DefaultListModel modeloLista;
    private JList            listagemMensagens;
    private JTextField       campoAutor;
    private JTextArea        campoConteudo;
    private JButton          botaoAdicionar;
    private JButton          botaoRemover;
    private JLabel           rotuloContador;

    private List             mensagens;       // List<Mensagem> em pre-generics seria List
    private RepositorioMensagens repositorio;

    // -----------------------------------------------------------------------
    // CONSTRUCAO DA JANELA
    // -----------------------------------------------------------------------

    public JanelaPrincipal() {
        super("Sistema de Mensagens — Swing Desktop (~1998)");

        repositorio = new RepositorioMensagens();
        mensagens   = new ArrayList();

        carregarMensagensSalvas();
        configurarJanela();
        construirUI();
        atualizarListagem();
    }

    private void configurarJanela() {
        /*
         * setDefaultCloseOperation instrui o JFrame sobre o que fazer
         * ao clicar no botao de fechar (X) da barra de titulo.
         * DO_NOTHING_ON_CLOSE permite interceptar o evento via WindowListener
         * para salvar dados antes de encerrar — padrao necessario em
         * qualquer aplicacao com estado persistivel.
         */
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setPreferredSize(new Dimension(780, 520));

        /*
         * WindowAdapter e uma classe abstrata que implementa todos os
         * metodos de WindowListener com implementacoes vazias.
         * Permite sobrescrever apenas o metodo de interesse
         * (windowClosing) sem precisar implementar todos os outros.
         * Foi o precursor das classes anonimas e lambdas para listeners.
         */
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent evento) {
                encerrarAplicacao();
            }
        });

        /*
         * Tentar aplicar o Look and Feel nativo do SO.
         * Se falhar (classe nao encontrada), o Swing usa o Metal L&F padrao.
         * O Metal L&F era o L&F padrao do JDK 1.2 — visual cinza caracteristico
         * que ficou associado a "Java desktop" nos anos 1990-2000.
         *
         * L&F nativo no Windows resultava em componentes que pareciam
         * Windows 95/98. No Mac OS X, o Apple desenvolveu seu proprio
         * L&F integrado ao JDK do Mac (Aqua L&F), nao acessivel diretamente.
         */
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // Metal L&F sera usado como fallback
        }
    }

    private void construirUI() {
        /*
         * JSplitPane divide o espaco em dois paineis com um divisor
         * arrastavel pelo usuario. Amplamente usado em aplicacoes desktop
         * com painel de navegacao (esquerda) e painel de detalhes (direita)
         * ou formulario (direita) — padrao "master-detail".
         */
        JSplitPane divisor = new JSplitPane(
            JSplitPane.HORIZONTAL_SPLIT,
            construirPainelListagem(),
            construirPainelFormulario()
        );
        divisor.setDividerLocation(380);
        divisor.setResizeWeight(0.5);

        rotuloContador = new JLabel("0 mensagens");
        rotuloContador.setBorder(BorderFactory.createEmptyBorder(3, 8, 3, 8));

        add(divisor, BorderLayout.CENTER);
        add(rotuloContador, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(null); // centralizar na tela
    }

    private JPanel construirPainelListagem() {
        JPanel painel = new JPanel(new BorderLayout(0, 6));
        painel.setBorder(new TitledBorder("Mensagens registradas"));

        modeloLista       = new DefaultListModel();
        listagemMensagens = new JList(modeloLista);

        /*
         * DefaultListModel e o modelo de dados padrao para JList.
         * Segue o padrao MVC do Swing: o modelo armazena os dados,
         * a JList e a view, e o codigo do usuario e o controller.
         *
         * Qualquer modificacao no modelo (addElement, removeElement)
         * notifica automaticamente a JList, que se repinta.
         * Esse desacoplamento era uma das inovacoes do Swing sobre o AWT.
         */
        listagemMensagens.setCellRenderer(new MensagemListRenderer());
        listagemMensagens.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listagemMensagens.setFixedCellHeight(62);

        /*
         * ListSelectionListener e notificado quando o usuario seleciona
         * um item na lista. Usado aqui para habilitar o botao "Remover"
         * apenas quando ha um item selecionado.
         */
        listagemMensagens.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent evento) {
                if (!evento.getValueIsAdjusting()) {
                    botaoRemover.setEnabled(
                        listagemMensagens.getSelectedIndex() >= 0
                    );
                }
            }
        });

        JScrollPane scroll = new JScrollPane(listagemMensagens);
        scroll.setPreferredSize(new Dimension(360, 400));

        botaoRemover = new JButton("Remover selecionada");
        botaoRemover.setEnabled(false);
        botaoRemover.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                removerMensagemSelecionada();
            }
        });

        JPanel painelBotao = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        painelBotao.add(botaoRemover);

        painel.add(scroll,       BorderLayout.CENTER);
        painel.add(painelBotao,  BorderLayout.SOUTH);
        return painel;
    }

    private JPanel construirPainelFormulario() {
        JPanel painel = new JPanel(new BorderLayout(0, 8));
        painel.setBorder(new TitledBorder("Nova mensagem"));

        /*
         * GridBagLayout: o layout mais flexivel e verboso do Swing/AWT.
         * GridBagConstraints define como cada componente se encaixa na grade:
         *   gridx / gridy   -> posicao na grade
         *   gridwidth       -> quantas colunas o componente ocupa
         *   fill            -> como expande (HORIZONTAL, VERTICAL, BOTH)
         *   weightx/weighty -> proporcao de espaco extra distribuido
         *   insets          -> margens externas
         *   anchor          -> alinhamento quando menor que o espaco disponivel
         */
        JPanel grade = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        // rotulo "Autor:"
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(4, 4, 2, 8);
        grade.add(new JLabel("Autor:"), gbc);

        // campo de texto do autor
        campoAutor = new JTextField();
        campoAutor.setFont(new Font("SansSerif", Font.PLAIN, 13));
        gbc.gridx = 1; gbc.gridy = 0;
        gbc.fill    = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.insets  = new Insets(4, 0, 2, 4);
        grade.add(campoAutor, gbc);

        // rotulo "Conteudo:"
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.fill    = GridBagConstraints.NONE;
        gbc.weightx = 0;
        gbc.anchor  = GridBagConstraints.NORTHWEST;
        gbc.insets  = new Insets(6, 4, 2, 8);
        grade.add(new JLabel("Conteudo:"), gbc);

        // area de texto do conteudo
        campoConteudo = new JTextArea(6, 25);
        campoConteudo.setFont(new Font("SansSerif", Font.PLAIN, 13));
        campoConteudo.setLineWrap(true);
        campoConteudo.setWrapStyleWord(true);

        /*
         * JTextArea nao tem barra de rolagem nativa.
         * E necessario envolve-la em um JScrollPane para que o usuario
         * possa rolar o conteudo quando o texto ultrapassa o tamanho
         * visivel — diferente de AWT TextArea, que tinha scroll embutido.
         * Essa separacao (componente + scroll separados) e o padrao Swing.
         */
        JScrollPane scrollConteudo = new JScrollPane(campoConteudo);
        gbc.gridx = 1; gbc.gridy = 1;
        gbc.fill    = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.insets  = new Insets(6, 0, 2, 4);
        grade.add(scrollConteudo, gbc);

        // botao adicionar
        botaoAdicionar = new JButton("Adicionar mensagem");
        botaoAdicionar.setFont(new Font("SansSerif", Font.BOLD, 12));

        /*
         * Atalho de teclado via KeyStroke.
         * registerKeyboardAction (metodo legado) associa uma tecla
         * a uma acao no componente ou em seu ancestral.
         *
         * O Swing moderno prefere a API Action com InputMap/ActionMap
         * (introduzida no JDK 1.3), mas o padrao acima era comum em
         * codigo JDK 1.2.
         */
        botaoAdicionar.registerKeyboardAction(
            new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    adicionarMensagem();
                }
            },
            KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, ActionEvent.CTRL_MASK),
            JButton.WHEN_IN_FOCUSED_WINDOW
        );

        botaoAdicionar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                adicionarMensagem();
            }
        });

        JPanel painelBotao = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        painelBotao.add(botaoAdicionar);

        painel.add(grade,       BorderLayout.CENTER);
        painel.add(painelBotao, BorderLayout.SOUTH);
        return painel;
    }

    // -----------------------------------------------------------------------
    // LOGICA DE NEGOCIO / HANDLERS
    // -----------------------------------------------------------------------

    private void adicionarMensagem() {
        String autor    = campoAutor.getText().trim();
        String conteudo = campoConteudo.getText().trim();

        /*
         * Validacao manual antes de qualquer framework de validacao existir.
         * JOptionPane.showMessageDialog() exibe uma caixa de dialogo modal —
         * o equivalente a um alert() do JavaScript, mas sincrono com a EDT.
         */
        if (autor.length() == 0 || conteudo.length() == 0) {
            JOptionPane.showMessageDialog(
                this,
                "Os campos 'Autor' e 'Conteudo' sao obrigatorios.",
                "Campos obrigatorios",
                JOptionPane.WARNING_MESSAGE
            );
            if (autor.length() == 0) {
                campoAutor.requestFocusInWindow();
            } else {
                campoConteudo.requestFocusInWindow();
            }
            return;
        }

        Mensagem nova = new Mensagem(autor, conteudo);
        mensagens.add(nova);
        salvarMensagens();
        atualizarListagem();

        campoAutor.setText("");
        campoConteudo.setText("");
        campoAutor.requestFocusInWindow();

        // rolar para o ultimo item adicionado
        int ultimoIndice = modeloLista.getSize() - 1;
        if (ultimoIndice >= 0) {
            listagemMensagens.ensureIndexIsVisible(ultimoIndice);
        }
    }

    private void removerMensagemSelecionada() {
        int indice = listagemMensagens.getSelectedIndex();
        if (indice < 0) return;

        /*
         * JOptionPane.showConfirmDialog retorna uma constante int:
         * YES_OPTION, NO_OPTION, CANCEL_OPTION ou CLOSED_OPTION.
         * Era o mecanismo padrao para confirmacoes modais.
         */
        int confirmacao = JOptionPane.showConfirmDialog(
            this,
            "Deseja remover a mensagem selecionada?",
            "Confirmar remocao",
            JOptionPane.YES_NO_OPTION
        );

        if (confirmacao == JOptionPane.YES_OPTION) {
            mensagens.remove(indice);
            salvarMensagens();
            atualizarListagem();
        }
    }

    private void atualizarListagem() {
        /*
         * DefaultListModel.clear() + addElement() em loop e o padrao
         * classico para atualizar um JList inteiramente.
         * Nao havia databinding automatico; toda sincronizacao entre
         * modelo de dados e modelo de UI era manual.
         */
        modeloLista.clear();

        for (int i = 0; i < mensagens.size(); i++) {
            modeloLista.addElement(mensagens.get(i));
        }

        int total = mensagens.size();
        rotuloContador.setText(total + (total == 1 ? " mensagem" : " mensagens"));
        botaoRemover.setEnabled(false);
    }

    // -----------------------------------------------------------------------
    // PERSISTENCIA
    // -----------------------------------------------------------------------

    private void salvarMensagens() {
        try {
            repositorio.salvar(mensagens);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                this,
                "Nao foi possivel salvar as mensagens:\n" + e.getMessage(),
                "Erro de persistencia",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void carregarMensagensSalvas() {
        try {
            List carregadas = repositorio.carregar();
            mensagens.addAll(carregadas);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                null,
                "Nao foi possivel carregar mensagens salvas:\n" + e.getMessage()
                + "\n\nA aplicacao iniciara sem dados previos.",
                "Aviso de carregamento",
                JOptionPane.WARNING_MESSAGE
            );
        }
    }

    private void encerrarAplicacao() {
        salvarMensagens();
        dispose();
        System.exit(0);
    }

    // -----------------------------------------------------------------------
    // PONTO DE ENTRADA
    // -----------------------------------------------------------------------

    /**
     * Ponto de entrada da aplicacao.
     *
     * SwingUtilities.invokeLater() submete a construcao da UI para
     * execucao na Event Dispatch Thread. Todo acesso a componentes Swing
     * deve ocorrer na EDT — construir a janela no main() (thread principal)
     * e tecnicamente incorreto, embora raramente cause problemas em
     * aplicacoes simples single-threaded.
     *
     * Runnable anonimo era o unico mecanismo disponivel em JDK 1.2/1.4
     * para passar blocos de codigo como argumento. Lambdas (Java 8, 2014)
     * simplificaram isso para:
     *   SwingUtilities.invokeLater(() -> new JanelaPrincipal().setVisible(true));
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                JanelaPrincipal janela = new JanelaPrincipal();
                janela.setVisible(true);
            }
        });
    }
}
