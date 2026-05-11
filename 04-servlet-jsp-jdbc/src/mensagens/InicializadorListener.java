package mensagens;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * Listener de contexto: inicializa o banco na inicializacao da aplicacao.
 *
 * -----------------------------------------------------------------------
 * SERVLETCONTEXTLISTENER
 * -----------------------------------------------------------------------
 * Introduzido na Servlet API 2.3 (J2EE 1.3, 2001), o ServletContextListener
 * permite executar codigo quando a aplicacao web e iniciada (contextInitialized)
 * e quando e encerrada (contextDestroyed).
 *
 * Era o mecanismo correto para inicializacao de recursos compartilhados:
 * conexoes de banco, thread pools, caches. Antes da Servlet 2.3, esse
 * tipo de inicializacao era feito no metodo init() do Servlet principal
 * ou num Servlet marcado com <load-on-startup> no web.xml, o que misturava
 * responsabilidades de infraestrutura com logica de negocio.
 *
 * O Listener e registrado no web.xml com a tag <listener>. Nao havia
 * anotacoes (@WebListener so viria na Servlet 3.0 / Servlet-2009).
 *
 * -----------------------------------------------------------------------
 * CONTEXTDESTROYED E O HSQLDB
 * -----------------------------------------------------------------------
 * O HSQLDB em modo in-memory e encerrado automaticamente quando a JVM
 * termina. O SHUTDOWN COMPACT no contextDestroyed e uma boa pratica
 * para bancos em modo arquivo (compacta o arquivo antes de fechar),
 * incluido aqui para demonstrar o padrao.
 */
public class InicializadorListener implements ServletContextListener {

    public void contextInitialized(ServletContextEvent evento) {
        ServletContext contexto = evento.getServletContext();
        contexto.log("[InicializadorListener] Inicializando aplicacao...");

        try {
            ConexaoFactory.inicializar();
            contexto.log("[InicializadorListener] Banco de dados inicializado com sucesso.");
        } catch (Exception e) {
            /*
             * Lancamos RuntimeException para sinalizar ao container
             * que a inicializacao falhou. Em producao, uma aplicacao
             * sem banco nao deve subir. O container (Tomcat) registra
             * o erro e marca a aplicacao como com falha de deploy.
             *
             * Engolir a excecao silenciosamente seria pior: a aplicacao
             * subiria parcialmente e as primeiras requisicoes falhariam
             * com NullPointerException ao tentar usar o banco nao
             * inicializado — bug muito mais dificil de diagnosticar.
             */
            contexto.log("[InicializadorListener] FALHA ao inicializar banco: " + e.getMessage());
            throw new RuntimeException("Falha critica na inicializacao do banco.", e);
        }
    }

    public void contextDestroyed(ServletContextEvent evento) {
        evento.getServletContext().log("[InicializadorListener] Encerrando aplicacao.");
        /*
         * Para bancos em modo arquivo, emitiriamos SHUTDOWN aqui:
         *   java.sql.Connection conn = ConexaoFactory.getConexao();
         *   conn.createStatement().execute("SHUTDOWN COMPACT");
         * Para modo in-memory, o banco e descartado automaticamente
         * com o processo — nenhuma acao necessaria.
         */
    }
}
