package mensagens;

import java.io.Serializable;
import java.util.Date;

/**
 * Entidade de dominio: representa uma mensagem no sistema.
 *
 * -----------------------------------------------------------------------
 * SERIALIZABLE
 * -----------------------------------------------------------------------
 * A interface Serializable foi introduzida no JDK 1.1 e permite que
 * objetos Java sejam convertidos em fluxos de bytes para persistencia
 * em arquivo ou transmissao em rede.
 *
 * Neste modulo ela e usada para salvar e recuperar a lista de mensagens
 * em disco — algo impossivel no modelo Applet (modulo 01) por restricao
 * do sandbox.
 *
 * serialVersionUID e um campo estatico que identifica a versao da classe
 * para fins de deserializacao. Se a classe mudar e o serialVersionUID nao
 * for atualizado, a leitura de arquivos antigos lancara InvalidClassException.
 * Omitir o campo era (e ainda e) um erro comum: o compilador gera um aviso
 * e o valor e calculado automaticamente a partir do hash da estrutura da
 * classe — qualquer mudanca no codigo invalida arquivos salvos anteriormente.
 *
 * -----------------------------------------------------------------------
 * DATA
 * -----------------------------------------------------------------------
 * java.util.Date continua sendo usado aqui.
 * java.time.LocalDateTime so existiria a partir do Java 8 (2014).
 * A classe Date e Serializable nativamente, o que facilita a persistencia.
 */
public class Mensagem implements Serializable {

    private static final long serialVersionUID = 1L;

    private String autor;
    private String conteudo;
    private Date   dataCriacao;

    /**
     * Construtor principal.
     * Captura a data/hora atual no momento da criacao da mensagem.
     */
    public Mensagem(String autor, String conteudo) {
        this.autor       = autor;
        this.conteudo    = conteudo;
        this.dataCriacao = new Date();
    }

    public String getAutor() {
        return autor;
    }

    public String getConteudo() {
        return conteudo;
    }

    public Date getDataCriacao() {
        return dataCriacao;
    }

    /**
     * Representacao textual usada pela JList via DefaultListModel.
     * Em Swing, o componente JList chama toString() de cada elemento
     * para obter o texto exibido quando um ListCellRenderer customizado
     * nao e fornecido.
     */
    public String toString() {
        return autor + "  [" + dataCriacao + "]";
    }
}
