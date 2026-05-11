package mensagens;

import java.util.Vector;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.List;

/**
 * Tela de listagem de mensagens usando javax.microedition.lcdui.List.
 *
 * -----------------------------------------------------------------------
 * LCDUI: A API DE UI DO MIDP
 * -----------------------------------------------------------------------
 * LCDUI (LCD User Interface) e o toolkit de UI do MIDP. Ao contrario
 * do Swing (que desenhava pixels com liberdade total), o LCDUI usa um
 * modelo de "UI de alto nivel": o desenvolvedor declara o que quer
 * (um List, um Form, um TextBox) e o dispositivo renderiza usando
 * seus proprios widgets nativos.
 *
 * Isso garantia que a UI funcionasse em qualquer dispositivo, mas
 * tirava completamente o controle visual do desenvolvedor. A aparencia
 * de um "List" num Nokia 3310 era completamente diferente de um
 * Sony Ericsson T610 — e o desenvolvedor nao podia controlar isso.
 *
 * Para controle total de pixels (jogos, por exemplo), existia o Canvas
 * de "baixo nivel", onde o desenvolvedor pintava cada pixel manualmente.
 * Jogos como o primeiro Bomberman mobile usavam Canvas.
 *
 * -----------------------------------------------------------------------
 * COMPONENTES DE ALTO NIVEL DO LCDUI
 * -----------------------------------------------------------------------
 *   List       -> lista de itens selecionaveis (usada aqui)
 *   Form       -> container de Items (StringItem, TextField, ChoiceGroup...)
 *   TextBox    -> entrada de texto de multiplas linhas (tela inteira)
 *   Alert      -> caixa de mensagem/aviso com timeout opcional
 *   Ticker     -> texto rolante no topo da tela
 *
 * -----------------------------------------------------------------------
 * COMMANDS E COMMANDLISTENER
 * -----------------------------------------------------------------------
 * Em MIDP, botoes nao existem como componentes clicaveis na tela.
 * Acoes sao representadas por "Commands", que o dispositivo mapeia
 * para as teclas de funcao fisicas (softkeys). Em celulares Nokia da epoca,
 * as teclas [esquerda] e [direita] abaixo do display eram mapeadas
 * para os dois Commands principais.
 *
 * O desenvolvedor declarava Commands com tipos semanticos:
 *   Command.OK      -> acao principal positiva
 *   Command.BACK    -> voltar/cancelar
 *   Command.EXIT    -> sair da aplicacao
 *   Command.ITEM    -> acao sobre item selecionado
 *   Command.SCREEN  -> acao sobre a tela atual
 *
 * O dispositivo decidia como mapear esses tipos para as teclas fisicas.
 * Num dispositivo com apenas uma softkey, todos os Commands iam para um
 * menu. Num dispositivo com duas softkeys, os dois Commands mais prioritarios
 * ficavam visiveis diretamente. O resto ia para um menu "Options".
 */
public class TelaLista extends List implements CommandListener {

    /*
     * Commands declarados como constantes de instancia.
     * O construtor recebe: (label, tipo).
     * O label pode ser truncado pelo dispositivo se for longo demais
     * para caber na softkey — desenvolvedores usavam labels curtos por habito.
     */
    private final Command cmdAdicionar = new Command("Adicionar", Command.SCREEN, 1);
    private final Command cmdDetalhes  = new Command("Ver",       Command.ITEM,   1);
    private final Command cmdLimpar    = new Command("Limpar",    Command.SCREEN, 2);
    private final Command cmdSair      = new Command("Sair",      Command.EXIT,   3);

    private MensagemMidlet midlet;
    private Vector         mensagens; // Vector<Mensagem>

    public TelaLista(MensagemMidlet midlet) {
        /*
         * super(titulo, tipo) — List.IMPLICIT significa que selecionar
         * um item com o botao de navegacao dispara automaticamente o
         * Command de tipo OK (se existir). Era o tipo mais comum para
         * menus de navegacao.
         *
         * Outros tipos:
         *   List.EXCLUSIVE -> radio button (uma selecao)
         *   List.MULTIPLE  -> checkbox (multiplas selecoes)
         *   List.IMPLICIT  -> selecao dispara Command imediatamente
         */
        super("Mensagens", List.IMPLICIT);

        this.midlet = midlet;

        addCommand(cmdAdicionar);
        addCommand(cmdDetalhes);
        addCommand(cmdLimpar);
        addCommand(cmdSair);

        /*
         * O Displayable tem exatamente um CommandListener.
         * Definir um novo substitui o anterior.
         * Nao havia listeners multiplos — modelo mais simples que o
         * ActionListener do Swing (que podia ter varios registrados).
         */
        setCommandListener(this);

        carregarMensagens();
    }

    /**
     * Recarrega a lista de mensagens do RMS e atualiza os itens exibidos.
     * Chamado na criacao e apos adicionar/limpar mensagens.
     */
    public void carregarMensagens() {
        /*
         * deleteAll() remove todos os itens da List.
         * Nao havia setItems() ou databinding: a unica forma de
         * atualizar a lista era apagar e reinserir todos os itens.
         */
        deleteAll();
        mensagens = new Vector();

        try {
            RepositorioRMS repo = new RepositorioRMS();
            mensagens = repo.carregar();

            if (mensagens.isEmpty()) {
                /*
                 * Em listas vazias, era comum mostrar um item nao-selecionavel
                 * com uma mensagem. Nao havia componente "EmptyView" no LCDUI.
                 * Append com null como imagem era o padrao para item sem icone.
                 */
                append("(nenhuma mensagem)", null);
            } else {
                for (int i = 0; i < mensagens.size(); i++) {
                    Mensagem m = (Mensagem) mensagens.elementAt(i);
                    /*
                     * append(string, image) adiciona um item a lista.
                     * O segundo parametro e um Image para exibir ao lado
                     * do texto — null significa sem icone.
                     *
                     * O texto e truncado pelo dispositivo se for mais largo
                     * que a tela. Em celulares com telas de 96x65 pixels,
                     * cabiam no maximo 12-15 caracteres por linha.
                     * Desenvolvedores encurtavam strings manualmente.
                     */
                    append(m.toString(), null);
                }
            }

        } catch (Exception e) {
            deleteAll();
            append("Erro ao carregar", null);
        }
    }

    /**
     * Unico metodo da interface CommandListener.
     * Chamado na Event Dispatch Thread interna do MIDP quando o usuario
     * pressiona uma softkey ou o botao de selecao.
     *
     * @param comando  o Command ativado
     * @param tela     o Displayable que gerou o evento (esta instancia)
     */
    public void commandAction(Command comando, Displayable tela) {

        if (comando == List.SELECT_COMMAND || comando == cmdDetalhes) {
            /*
             * List.SELECT_COMMAND e o Command implicito disparado ao
             * selecionar um item em List.IMPLICIT. Verificamos se ha
             * itens reais (nao o item placeholder "(nenhuma mensagem)").
             */
            int indice = getSelectedIndex();
            if (indice >= 0 && !mensagens.isEmpty()) {
                Mensagem selecionada = (Mensagem) mensagens.elementAt(indice);
                midlet.exibirDetalhe(selecionada);
            }

        } else if (comando == cmdAdicionar) {
            midlet.exibirFormularioAdicionar();

        } else if (comando == cmdLimpar) {
            midlet.confirmarLimpar();

        } else if (comando == cmdSair) {
            /*
             * MIDlet.notifyDestroyed() sinaliza ao Application Management
             * Software (AMS) do dispositivo que o MIDlet deseja encerrar.
             * Nao chame System.exit() diretamente — o comportamento era
             * indefinido em muitos dispositivos e podia travar o telefone.
             */
            midlet.notifyDestroyed();
        }
    }
}
