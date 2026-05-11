package mensagens;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.text.SimpleDateFormat;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;

/**
 * Renderizador customizado de celulas para o JList de mensagens.
 *
 * -----------------------------------------------------------------------
 * PADRÃO LISTICELLRENDERER
 * -----------------------------------------------------------------------
 * Por padrao, JList exibe o resultado de toString() de cada elemento
 * numa JLabel simples. Para mostrar multiplas linhas de informacao
 * por item (autor em destaque + conteudo + data), e necessario
 * implementar ListCellRenderer e retornar um Component composto.
 *
 * O metodo getListCellRendererComponent() e chamado pelo JList para
 * CADA celula visivel, a cada repaint(). O renderer deve ser stateless:
 * o mesmo objeto JPanel e reusado para todas as celulas — apenas seus
 * campos sao atualizados a cada chamada. Criar um novo JPanel por celula
 * seria um vazamento de objetos e causaria lentidao visivel em listas longas.
 *
 * Esse padrao de "celula como componente reusado" foi mantido no Swing
 * moderno e e um dos primeiros conceitos que desenvolvedores Java desktop
 * precisavam aprender ao trabalhar com JTable e JTree tambem.
 *
 * -----------------------------------------------------------------------
 * SIMPLEDATEFORMAT
 * -----------------------------------------------------------------------
 * SimpleDateFormat formata java.util.Date em String.
 * E instanciada como campo da classe (uma instancia por renderer) para
 * evitar recriacoes a cada chamada, mas isso cria um problema oculto:
 * SimpleDateFormat NAO e thread-safe. Se dois threads chamarem
 * getListCellRendererComponent() simultaneamente (ex.: repaint em thread
 * separada enquanto dados chegam), o resultado da formatacao pode ser
 * corrompido silenciosamente.
 *
 * Em Swing isso e parcialmente mitigado porque todo repaint ocorre na
 * Event Dispatch Thread (EDT). Mas o bug existe latente se a classe
 * for reusada fora desse contexto.
 *
 * A solucao definitiva veio com java.time.format.DateTimeFormatter
 * no Java 8 (2014) — imutavel e thread-safe por design.
 */
public class MensagemListRenderer extends JPanel implements ListCellRenderer {

    private static final Color COR_SELECIONADO    = new Color(184, 207, 229);
    private static final Color COR_NORMAL         = Color.WHITE;
    private static final Color COR_ALTERNADO      = new Color(240, 240, 245);
    private static final Color COR_TEXTO_AUTOR    = new Color(30, 80, 160);
    private static final Color COR_TEXTO_CONTEUDO = new Color(40, 40, 40);
    private static final Color COR_TEXTO_DATA     = new Color(120, 120, 120);

    private JLabel rotuloAutor;
    private JLabel rotuloConteudo;
    private JLabel rotuloData;

    /*
     * SimpleDateFormat nao e thread-safe — ver comentario na Javadoc da classe.
     * Em JDK < 8 nao havia alternativa na biblioteca padrao para formatacao
     * de datas que fosse thread-safe sem sincronizacao manual.
     */
    private SimpleDateFormat formatadorData;

    public MensagemListRenderer() {
        /*
         * BoxLayout empilha componentes em um eixo (X ou Y).
         * E o layout mais simples para construir cards verticais.
         * Introduzido no JDK 1.2 junto com o Swing.
         */
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));

        rotuloAutor = new JLabel();
        rotuloAutor.setFont(new Font("SansSerif", Font.BOLD, 13));
        rotuloAutor.setForeground(COR_TEXTO_AUTOR);

        rotuloConteudo = new JLabel();
        rotuloConteudo.setFont(new Font("SansSerif", Font.PLAIN, 12));
        rotuloConteudo.setForeground(COR_TEXTO_CONTEUDO);

        rotuloData = new JLabel();
        rotuloData.setFont(new Font("SansSerif", Font.PLAIN, 10));
        rotuloData.setForeground(COR_TEXTO_DATA);

        add(rotuloAutor);
        add(rotuloConteudo);
        add(rotuloData);

        formatadorData = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    }

    /**
     * Chamado pelo JList para cada celula visivel a cada repaint.
     *
     * @param lista    o JList que esta renderizando
     * @param valor    o objeto a renderizar (Mensagem)
     * @param indice   posicao na lista
     * @param selecionado  true se a celula esta selecionada
     * @param comFoco  true se a celula tem o foco do teclado
     */
    public Component getListCellRendererComponent(
            JList lista,
            Object valor,
            int    indice,
            boolean selecionado,
            boolean comFoco) {

        Mensagem mensagem = (Mensagem) valor;  // cast necessario: sem generics em JDK 1.2

        rotuloAutor.setText(mensagem.getAutor());
        rotuloConteudo.setText(mensagem.getConteudo());
        rotuloData.setText(formatadorData.format(mensagem.getDataCriacao()));

        /*
         * Linhas alternadas (zebra striping) sao implementadas manualmente.
         * Nao havia suporte nativo a isso no Swing do JDK 1.2.
         * O JDK 1.6 introduziu JTable.setShowGrid() e melhorias de estilo,
         * mas JList continuou dependendo de renderer customizado.
         */
        if (selecionado) {
            setBackground(COR_SELECIONADO);
        } else if (indice % 2 == 0) {
            setBackground(COR_NORMAL);
        } else {
            setBackground(COR_ALTERNADO);
        }

        /*
         * setOpaque(true) e obrigatorio para que o fundo definido por
         * setBackground() seja efetivamente pintado.
         * Componentes Swing sao transparentes por padrao (opaque = false),
         * o que significa que o fundo do pai aparece atraves deles.
         * Esquecer esse detalhe era um bug classico em UIs Swing customizadas.
         */
        setOpaque(true);
        rotuloAutor.setOpaque(false);
        rotuloConteudo.setOpaque(false);
        rotuloData.setOpaque(false);

        return this;
    }
}
