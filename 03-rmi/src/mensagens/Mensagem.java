package mensagens;

import java.io.Serializable;
import java.util.Date;

/**
 * Representa uma mensagem trafegando entre cliente e servidor via RMI.
 *
 * -----------------------------------------------------------------------
 * SERIALIZABLE EM RMI
 * -----------------------------------------------------------------------
 * Em RMI, todo objeto passado como argumento ou retornado por um metodo
 * remoto precisa implementar Serializable. O motivo e simples: o objeto
 * precisa ser convertido em bytes para cruzar a rede entre duas JVMs,
 * e a serializacao Java e o mecanismo padrao para isso.
 *
 * Isso contrasta com chamadas locais, onde objetos sao passados por
 * referencia (apenas o ponteiro e copiado). Em RMI, o objeto inteiro
 * e copiado (pass-by-value via serializacao) — uma diferenca semantica
 * critica que causava bugs sutis quando desenvolvedores assumiam
 * que modificacoes no objeto retornado seriam visiveis no servidor.
 *
 * -----------------------------------------------------------------------
 * SERIALVERSIONUID
 * -----------------------------------------------------------------------
 * Em um sistema distribuido, cliente e servidor podem estar rodando
 * versoes diferentes da classe Mensagem. Se o serialVersionUID diferir,
 * a deserializacao lanca InvalidClassException. Em producao, gerenciar
 * compatibilidade de versoes de classes serializadas era um dos maiores
 * desafios de manutencao de sistemas RMI.
 */
public class Mensagem implements Serializable {

    private static final long serialVersionUID = 1L;

    private String autor;
    private String conteudo;
    private Date   dataCriacao;

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

    public String toString() {
        return "[" + dataCriacao + "]  " + autor + ": " + conteudo;
    }
}
