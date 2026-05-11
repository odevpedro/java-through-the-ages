package mensagens;

import java.io.Serializable;
import java.util.Date;

/**
 * Objeto de dominio do sistema de mensagens.
 *
 * Em uma aplicacao Servlet/JSP/JDBC do inicio dos anos 2000, esse tipo de
 * classe era normalmente chamado de JavaBean ou POJO: campos privados,
 * construtores simples e metodos get/set usados por Servlets, DAOs e JSPs.
 */
public class Mensagem implements Serializable {

    private int id;
    private String autor;
    private String conteudo;
    private Date dataCriacao;

    public Mensagem() {
        this.dataCriacao = new Date();
    }

    public Mensagem(String autor, String conteudo) {
        this();
        this.autor = autor;
        this.conteudo = conteudo;
    }

    public Mensagem(int id, String autor, String conteudo, Date dataCriacao) {
        this.id = id;
        this.autor = autor;
        this.conteudo = conteudo;
        this.dataCriacao = dataCriacao;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    public String getConteudo() {
        return conteudo;
    }

    public void setConteudo(String conteudo) {
        this.conteudo = conteudo;
    }

    public Date getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(Date dataCriacao) {
        this.dataCriacao = dataCriacao;
    }
}
