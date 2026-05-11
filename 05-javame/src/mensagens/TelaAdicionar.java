package mensagens;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.TextField;

/**
 * Tela de adicao de mensagem usando Form com TextFields.
 *
 * -----------------------------------------------------------------------
 * FORM: O CONTAINER DE ITEMS DO LCDUI
 * -----------------------------------------------------------------------
 * Form e o componente LCDUI mais versatil: um container que empilha
 * Items verticalmente. Items disponiveis:
 *
 *   TextField   -> entrada de texto (usado aqui)
 *   StringItem  -> texto nao editavel (label + valor)
 *   ChoiceGroup -> grupo de radio buttons ou checkboxes
 *   Gauge       -> barra de progresso
 *   ImageItem   -> exibicao de imagem
 *   DateField   -> entrada de data/hora (MIDP 2.0)
 *   Spacer      -> espaco em branco entre items
 *
 * Em MIDP, nao havia layout managers como no AWT/Swing. Os items eram
 * sempre empilhados verticalmente, de cima para baixo. Posicionamento
 * horizontal era definido por flags de layout do item (LAYOUT_LEFT,
 * LAYOUT_CENTER, LAYOUT_NEWLINE_BEFORE) mas com suporte variavel entre
 * dispositivos.
 *
 * -----------------------------------------------------------------------
 * TEXTFIELD: ENTRADA DE TEXTO COM RESTRICOES
 * -----------------------------------------------------------------------
 * TextField(label, texto_inicial, tamanho_maximo, restricao)
 *
 * Restricoes de input disponíveis:
 *   TextField.ANY        -> qualquer texto
 *   TextField.EMAILADDR  -> endereço de e-mail (valida formato no dispositivo)
 *   TextField.NUMERIC    -> apenas digitos
 *   TextField.PHONENUMBER -> numero de telefone
 *   TextField.URL        -> URL
 *   TextField.DECIMAL    -> numero decimal
 *
 * A restricao era interpretada pelo firmware do dispositivo: em alguns
 * celulares, NUMERIC trocava o teclado para modo numerico automaticamente.
 * Nao havia TextWatcher, InputFilter nem expressoes regulares.
 *
 * Tamanho maximo real: a especificacao garantia que o campo aceitaria
 * pelo menos N caracteres, mas o dispositivo podia limitar mais.
 * Em celulares com teclado T9, o usuario precisava pressionar teclas
 * multiplas vezes para cada letra. "Ada Lovelace" requeria:
 *   A=2 d=33 a=2 ' '=0# L=555 o=666 v=888 e=33 l=555 a=2 c=222 e=33
 * Uma experiencia de usuario muito diferente de um teclado fisico.
 *
 * -----------------------------------------------------------------------
 * TECLADO T9
 * -----------------------------------------------------------------------
 * T9 (Text on 9 keys) era a tecnologia de predicao de texto para teclados
 * numericos de celulares. O usuario pressionava as teclas uma vez por letra
 * e o T9 sugeria palavras. Sem T9, "modo ABC" exigia multiplos toques:
 * 2=A, 22=B, 222=C, 2222=2, etc.
 *
 * Essa limitacao fisica moldava o design de toda interface Java ME:
 * textos curtos, menus em vez de formularios, confirmacoes em vez de
 * desfazer. A usabilidade era radicalmente diferente de telas touch.
 */
public class TelaAdicionar extends Form implements CommandListener {

    private final Command cmdSalvar  = new Command("Salvar",   Command.OK,   1);
    private final Command cmdCancelar = new Command("Cancelar", Command.BACK, 2);

    private TextField campoAutor;
    private TextField campoConteudo;

    private MensagemMidlet midlet;

    public TelaAdicionar(MensagemMidlet midlet) {
        super("Nova mensagem");

        this.midlet = midlet;

        /*
         * TextField(label, texto_inicial, max_chars, restricao)
         *
         * Tamanho maximo de 50 para autor e 200 para conteudo.
         * Em um RecordStore com limite de 8 KB, mensagens muito grandes
         * podiam causar RecordStoreFullException.
         *
         * ANY permite qualquer caractere. Em dispositivos com teclado T9,
         * o modo de entrada (T9, ABC, numerico) era controlado pelo usuario
         * ou pelo firmware, nao pelo desenvolvedor.
         */
        campoAutor    = new TextField("Autor:",    "", 50,  TextField.ANY);
        campoConteudo = new TextField("Mensagem:", "", 200, TextField.ANY);

        /*
         * append(Item) adiciona um item ao Form.
         * A ordem de append define a ordem de exibicao na tela.
         * Sem layout managers: sempre empilhado verticalmente.
         */
        append(campoAutor);
        append(campoConteudo);

        addCommand(cmdSalvar);
        addCommand(cmdCancelar);
        setCommandListener(this);
    }

    /**
     * Limpa os campos para reutilizacao da tela.
     * Reusar instancias de Displayable era boa pratica em Java ME:
     * criar objetos tinha custo de GC elevado em VMs com heap restrita.
     */
    public void limpar() {
        campoAutor.setString("");
        campoConteudo.setString("");
    }

    public void commandAction(Command comando, Displayable tela) {

        if (comando == cmdSalvar) {
            String autor    = campoAutor.getString().trim();
            String conteudo = campoConteudo.getString().trim();

            /*
             * Validacao manual — nenhum framework de validacao existia.
             * Em Java ME, mostrar uma mensagem de erro era feito com Alert:
             * uma tela temporaria que desaparecia apos um timeout
             * ou quando o usuario pressionava OK.
             */
            if (autor.length() == 0 || conteudo.length() == 0) {
                midlet.exibirAlerta("Preencha autor e mensagem.");
                return;
            }

            try {
                RepositorioRMS repo = new RepositorioRMS();
                Mensagem nova = new Mensagem(autor, conteudo);
                repo.salvar(nova);

                limpar();
                midlet.voltarParaLista(true); // true = recarregar a lista

            } catch (Exception e) {
                /*
                 * RecordStoreFullException seria o erro mais comum aqui.
                 * Em 2005, "armazenamento cheio" era uma mensagem familiar
                 * para usuarios de celulares — especialmente porque o RMS
                 * competia com espaço usado por contatos, MMS e ringtones.
                 */
                midlet.exibirAlerta("Erro ao salvar: " + e.getMessage());
            }

        } else if (comando == cmdCancelar) {
            limpar();
            midlet.voltarParaLista(false);
        }
    }
}
