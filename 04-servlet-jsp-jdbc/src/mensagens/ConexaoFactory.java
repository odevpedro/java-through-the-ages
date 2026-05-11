package mensagens;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Fabrica de conexoes JDBC para o banco HSQLDB embutido.
 *
 * -----------------------------------------------------------------------
 * DRIVERMANAGER: O ACESSO JDBC "NA MAO"
 * -----------------------------------------------------------------------
 * DriverManager e a API de nivel mais baixo do JDBC para obter conexoes.
 * Cada chamada a getConnection() abre uma nova conexao TCP/socket com
 * o banco (ou aloca recursos internos, no caso de bancos embutidos).
 *
 * Em producao, abrir uma conexao por requisicao era catastrofico:
 * conexoes tem custo alto de setup (autenticacao, negociacao de protocolo,
 * alocacao de buffers). Um servidor com 100 requisicoes simultaneas
 * tentaria abrir 100 conexoes — geralmente o limite maximo do banco.
 *
 * A solucao era um connection pool: um conjunto de conexoes pre-abertas
 * e reutilizadas. Em J2EE 1.2/1.3, o pool era configurado via JNDI
 * no servidor de aplicacao (Tomcat, JBoss) e acessado via:
 *
 *   Context ctx = new InitialContext();
 *   DataSource ds = (DataSource) ctx.lookup("java:comp/env/jdbc/MensagensDS");
 *   Connection conn = ds.getConnection();
 *
 * Este modulo usa DriverManager diretamente (sem pool) para manter
 * fidelidade ao estilo mais simples da epoca e ao contexto de um
 * container Servlet simples sem configuracao JNDI completa.
 * Em producao real em 2001, o JNDI + DataSource era o padrao correto.
 *
 * -----------------------------------------------------------------------
 * HSQLDB EMBUTIDO
 * -----------------------------------------------------------------------
 * HSQLDB (HyperSQL DataBase) e um banco de dados relacional escrito
 * em Java puro, que pode rodar embutido dentro do processo da aplicacao.
 * Era amplamente usado em projetos de demonstracao e testes de integracao
 * antes do H2 (2005) se popularizar.
 *
 * A URL "jdbc:hsqldb:mem:mensagensdb" configura o banco em memoria:
 * os dados existem enquanto o processo estiver rodando e sao perdidos
 * ao parar. Para persistencia em arquivo, seria "jdbc:hsqldb:file:/caminho".
 *
 * -----------------------------------------------------------------------
 * INICIALIZACAO DO SCHEMA
 * -----------------------------------------------------------------------
 * Sem ORM, a criacao das tabelas e responsabilidade do desenvolvedor.
 * A pratica comum era executar o DDL na inicializacao da aplicacao,
 * protegido por "CREATE TABLE IF NOT EXISTS" (ou equivalente).
 *
 * Flyway (2010) e Liquibase (2006) surgiram para gerenciar esse processo
 * de forma versionada. Em 2001, scripts SQL manuais eram o estado da arte.
 */
public class ConexaoFactory {

    private static final String DRIVER_CLASS = "org.hsqldb.jdbcDriver";
    private static final String URL          = "jdbc:hsqldb:mem:mensagensdb";
    private static final String USUARIO      = "sa";
    private static final String SENHA        = "";

    private static boolean schemaInicializado = false;

    /**
     * Registra o driver JDBC e inicializa o schema na primeira chamada.
     *
     * Class.forName() e necessario em JDBC pre-4.0 (JDK < 1.6) para que
     * o DriverManager "conheça" o driver. O driver se auto-registra em
     * seu bloco estatico ao ser carregado. Sem esse passo, getConnection()
     * lancava "No suitable driver found for jdbc:hsqldb...".
     *
     * JDBC 4.0 (Java SE 6, 2006) introduziu Service Provider Interface (SPI):
     * o DriverManager detecta drivers automaticamente via META-INF/services.
     * Class.forName() deixou de ser necessario, mas permaneceu em muito
     * codigo legado por anos.
     */
    public static synchronized void inicializar() throws Exception {
        if (schemaInicializado) {
            return;
        }
        Class.forName(DRIVER_CLASS);
        criarTabelas();
        schemaInicializado = true;
        System.out.println("[ConexaoFactory] HSQLDB inicializado. Schema criado.");
    }

    /**
     * Retorna uma nova conexao com o banco.
     *
     * ATENCAO: o chamador e responsavel por fechar a conexao.
     * Nao fechar conexoes e um dos bugs mais comuns em codigo JDBC
     * da epoca — conexoes ficavam abertas até o GC coletar o objeto
     * (nao determinístico) ou ate o banco atingir o limite de conexoes.
     *
     * O padrao correto era sempre usar try/finally para garantir o close():
     *
     *   Connection conn = null;
     *   try {
     *       conn = ConexaoFactory.getConexao();
     *       // usar conn
     *   } finally {
     *       if (conn != null) conn.close();
     *   }
     *
     * try-with-resources (Java 7, 2011) eliminou o finally manual.
     */
    public static Connection getConexao() throws SQLException {
        return DriverManager.getConnection(URL, USUARIO, SENHA);
    }

    private static void criarTabelas() throws SQLException {
        Connection conn = null;
        Statement stmt = null;
        try {
            conn = DriverManager.getConnection(URL, USUARIO, SENHA);
            stmt = conn.createStatement();

            /*
             * DDL da tabela principal.
             * IDENTITY em HSQLDB e equivalente ao SERIAL do PostgreSQL
             * e ao AUTO_INCREMENT do MySQL — gera um inteiro unico
             * automaticamente a cada INSERT.
             *
             * VARCHAR(255) era o tamanho padrao conservador para strings.
             * Em 2001, aplicacoes web raramente precisavam de textos maiores
             * num unico campo da form. CLOB/TEXT existia mas era menos portavel.
             */
            stmt.executeUpdate(
                "CREATE TABLE IF NOT EXISTS mensagens (" +
                "  id          INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY, " +
                "  autor       VARCHAR(255) NOT NULL, " +
                "  conteudo    VARCHAR(2000) NOT NULL, " +
                "  data_criacao TIMESTAMP DEFAULT NOW() NOT NULL" +
                ")"
            );

        } finally {
            if (stmt != null) try { stmt.close(); } catch (SQLException e) { /* ignorado */ }
            if (conn != null) try { conn.close(); } catch (SQLException e) { /* ignorado */ }
        }
    }
}
