package mensagens;

import java.util.Date;

/**
 * Representa uma mensagem no sistema.
 *
 * Em JDK 1.0/1.1 nao havia anotacoes, records, nem construtores
 * compactos. Uma classe de dominio simples como esta era escrita
 * exatamente desta forma: campo privado, construtor, getters.
 *
 * java.util.Date era a unica opcao para representar instantes de
 * tempo no JDK 1.0. A classe e mutavel e nao possui suporte a
 * fusos horarios — problemas que so foram resolvidos com a
 * introducao de java.time no Java SE 8 (2014).
 */
public class Mensagem {

    private String autor;
    private String conteudo;
    private Date dataCriacao;

    /**
     * Construtor principal. A data de criacao e capturada automaticamente
     * no momento da instanciacao usando new Date(), que internamente chama
     * System.currentTimeMillis(). Nao havia Clock injetavel nesta era.
     */
    public Mensagem(String autor, String conteudo) {
        this.autor     = autor;
        this.conteudo  = conteudo;
        this.dataCriacao = new Date(); // captura o instante atual; sem suporte a TimeZone
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
     * toString() era o mecanismo padrao para representacao textual antes
     * de frameworks de serializacao e templates existirem.
     *
     * Concatenacao de String com + gera instancias intermediarias de String
     * na heap — custo que na epoca era ignorado em classes simples.
     * StringBuffer existia desde JDK 1.0 mas era reservado para loops.
     */
    public String toString() {
        return "[" + dataCriacao + "]  " + autor + ":\n" + conteudo;
    }
}
