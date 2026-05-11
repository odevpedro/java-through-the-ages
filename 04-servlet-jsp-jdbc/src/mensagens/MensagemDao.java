package mensagens;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object (DAO) para mensagens — JDBC puro.
 *
 * -----------------------------------------------------------------------
 * O PADRAO DAO
 * -----------------------------------------------------------------------
 * O padrao DAO (Data Access Object) foi formalizado pelo Core J2EE Patterns
 * (Sun, 2001) exatamente neste periodo. O objetivo e isolar o codigo de
 * acesso a dados do restante da aplicacao: o Servlet nao sabe que existe
 * um banco, apenas que existe um MensagemDao.
 *
 * Antes da formalizacao do padrao, era comum ver codigo SQL diretamente
 * nos Servlets — o equivalente ao SQL no PHP inline dos anos 1990.
 * O DAO era uma melhoria significativa de organizacao, mesmo que todo
 * o SQL ainda fosse manual.
 *
 * -----------------------------------------------------------------------
 * PREPAREDSTATEMENT vs STATEMENT
 * -----------------------------------------------------------------------
 * Statement.executeUpdate("INSERT ... '" + autor + "'") e vulneravel a
 * SQL Injection: se autor for "'; DROP TABLE mensagens; --", o banco
 * executa o DROP.
 *
 * PreparedStatement resolve isso: os parametros sao passados separadamente
 * e o banco os trata como dados literais, nunca como SQL.
 *
 * PreparedStatement tambem e mais eficiente: o banco compila o plano de
 * execucao uma vez e o reutiliza para valores diferentes. Em sistemas
 * com muitas insercoes repetitivas, a diferenca de performance era notavel.
 *
 * Em 2001, o uso de PreparedStatement ainda nao era universal: muito
 * codigo legado usava Statement com concatenacao de strings.
 *
 * -----------------------------------------------------------------------
 * GERENCIAMENTO MANUAL DE RECURSOS
 * -----------------------------------------------------------------------
 * Cada metodo aqui segue o padrao try/finally para garantir que
 * Connection, PreparedStatement e ResultSet sejam fechados.
 *
 * A hierarquia de fechamento e importante: fechar o PreparedStatement
 * fecha automaticamente o ResultSet aberto por ele. Fechar a Connection
 * fecha automaticamente todos os Statements. Mas fechar de forma explicita
 * e mais seguro e documentado.
 *
 * Em JDBC pre-Java 7, nao havia try-with-resources. Um metodo simples
 * de INSERT podia ter 15-20 linhas so de gerenciamento de recursos,
 * sem contar a logica real. Isso era parte do "custo" do JDBC puro.
 */
public class MensagemDao {

    /**
     * Insere uma nova mensagem no banco e retorna o ID gerado.
     *
     * Statement.RETURN_GENERATED_KEYS instrui o driver JDBC a capturar
     * o ID auto-gerado apos o INSERT. Introduzido no JDBC 3.0 (J2SE 1.4).
     * Antes disso, o padrao era executar um SELECT MAX(id) logo apos o
     * INSERT — uma janela de race condition em servidores concorrentes.
     */
    public int inserir(Mensagem mensagem) throws SQLException {
        String sql = "INSERT INTO mensagens (autor, conteudo, data_criacao) VALUES (?, ?, ?)";

        Connection conn        = null;
        PreparedStatement stmt = null;
        ResultSet keysGeradas  = null;

        try {
            conn = ConexaoFactory.getConexao();
            stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            /*
             * Parametros sao definidos por indice (1-based, nao 0-based).
             * Cada tipo tem seu proprio metodo setter: setString, setInt,
             * setTimestamp, setDate, setBigDecimal, etc.
             *
             * java.sql.Timestamp e o tipo correto para colunas TIMESTAMP.
             * A conversao de java.util.Date para java.sql.Timestamp
             * exigia: new Timestamp(date.getTime()) — boilerplate obrigatorio.
             */
            stmt.setString(1, mensagem.getAutor());
            stmt.setString(2, mensagem.getConteudo());
            stmt.setTimestamp(3, new Timestamp(mensagem.getDataCriacao().getTime()));

            stmt.executeUpdate();

            keysGeradas = stmt.getGeneratedKeys();
            if (keysGeradas.next()) {
                return keysGeradas.getInt(1);
            }
            return -1;

        } finally {
            if (keysGeradas != null) try { keysGeradas.close(); } catch (SQLException e) { /* ignorado */ }
            if (stmt != null)        try { stmt.close();        } catch (SQLException e) { /* ignorado */ }
            if (conn != null)        try { conn.close();        } catch (SQLException e) { /* ignorado */ }
        }
    }

    /**
     * Retorna todas as mensagens, ordenadas da mais recente para a mais antiga.
     *
     * O mapeamento ResultSet -> List<Mensagem> e inteiramente manual:
     * para cada linha do ResultSet, criamos um objeto Mensagem com os
     * valores de cada coluna. Com JPA/Hibernate (2002+), esse mapeamento
     * seria feito automaticamente por anotacoes (@Entity, @Column).
     *
     * ResultSet.next() avanca o cursor de linha. O padrao:
     *   while (rs.next()) { ... rs.getString("coluna") ... }
     * era tao ubiquo que era reconhecido instantaneamente por qualquer
     * desenvolvedor Java da epoca.
     */
    public List listar() throws SQLException {
        String sql = "SELECT id, autor, conteudo, data_criacao " +
                     "FROM mensagens ORDER BY data_criacao DESC";

        Connection        conn = null;
        PreparedStatement stmt = null;
        ResultSet         rs   = null;
        List              resultado = new ArrayList();

        try {
            conn = ConexaoFactory.getConexao();
            stmt = conn.prepareStatement(sql);
            rs   = stmt.executeQuery();

            while (rs.next()) {
                /*
                 * rs.getTimestamp("data_criacao") retorna java.sql.Timestamp.
                 * java.sql.Timestamp e subclasse de java.util.Date, entao
                 * pode ser passada diretamente para o construtor de Mensagem
                 * sem conversao explicita — detalhe util mas raramente
                 * documentado claramente na epoca.
                 */
                Mensagem m = new Mensagem(
                    rs.getInt("id"),
                    rs.getString("autor"),
                    rs.getString("conteudo"),
                    rs.getTimestamp("data_criacao")
                );
                resultado.add(m);
            }

            return resultado;

        } finally {
            if (rs   != null) try { rs.close();   } catch (SQLException e) { /* ignorado */ }
            if (stmt != null) try { stmt.close(); } catch (SQLException e) { /* ignorado */ }
            if (conn != null) try { conn.close(); } catch (SQLException e) { /* ignorado */ }
        }
    }

    /**
     * Remove uma mensagem pelo ID.
     *
     * @param id ID da mensagem a remover
     * @return true se uma linha foi afetada, false se o ID nao existia
     */
    public boolean remover(int id) throws SQLException {
        String sql = "DELETE FROM mensagens WHERE id = ?";

        Connection        conn = null;
        PreparedStatement stmt = null;

        try {
            conn = ConexaoFactory.getConexao();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);

            int linhasAfetadas = stmt.executeUpdate();
            return linhasAfetadas > 0;

        } finally {
            if (stmt != null) try { stmt.close(); } catch (SQLException e) { /* ignorado */ }
            if (conn != null) try { conn.close(); } catch (SQLException e) { /* ignorado */ }
        }
    }

    /**
     * Conta o total de mensagens no banco.
     * Evita transferir todas as linhas so para contar — boa pratica
     * independente de era.
     */
    public int contar() throws SQLException {
        String sql = "SELECT COUNT(*) FROM mensagens";

        Connection        conn = null;
        PreparedStatement stmt = null;
        ResultSet         rs   = null;

        try {
            conn = ConexaoFactory.getConexao();
            stmt = conn.prepareStatement(sql);
            rs   = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;

        } finally {
            if (rs   != null) try { rs.close();   } catch (SQLException e) { /* ignorado */ }
            if (stmt != null) try { stmt.close(); } catch (SQLException e) { /* ignorado */ }
            if (conn != null) try { conn.close(); } catch (SQLException e) { /* ignorado */ }
        }
    }
}
