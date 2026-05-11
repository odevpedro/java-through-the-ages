package mensagens;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet controlador do sistema de mensagens.
 *
 * -----------------------------------------------------------------------
 * SERVLET: A UNIDADE CENTRAL DO JAVA WEB
 * -----------------------------------------------------------------------
 * Um HttpServlet recebe requisicoes HTTP e produz respostas. E o componente
 * mais fundamental do Java web — tudo acima dele (JSF, Spring MVC, Struts)
 * e construido sobre esta abstracao.
 *
 * Ciclo de vida gerenciado pelo container (Tomcat):
 *   1. init()    -> chamado uma vez ao carregar o Servlet (lazy ou eager,
 *                   conforme <load-on-startup> no web.xml)
 *   2. service() -> chamado a cada requisicao; delega para doGet/doPost/etc.
 *   3. destroy() -> chamado ao descarregar o Servlet (undeploy ou shutdown)
 *
 * O container garante que init() e destroy() sao chamados uma vez.
 * service() pode ser chamado por multiplas threads simultaneamente —
 * o Servlet deve ser thread-safe. Campos de instancia mutaveis sao
 * uma fonte classica de bugs de concorrencia em Servlets.
 *
 * -----------------------------------------------------------------------
 * SEM ANOTACOES
 * -----------------------------------------------------------------------
 * @WebServlet so existe a partir da Servlet API 3.0 (2009).
 * Em Servlet 2.3 (2001), todo mapeamento de URL era feito no web.xml:
 *
 *   <servlet>
 *     <servlet-name>MensagemServlet</servlet-name>
 *     <servlet-class>mensagens.MensagemServlet</servlet-class>
 *   </servlet>
 *   <servlet-mapping>
 *     <servlet-name>MensagemServlet</servlet-name>
 *     <url-pattern>/mensagens</url-pattern>
 *   </servlet-mapping>
 *
 * Adicionar um Servlet exigia editar o web.xml — mudanca em arquivo
 * de configuracao, nao apenas no codigo Java. Em times grandes, isso
 * criava conflitos de merge frequentes.
 *
 * -----------------------------------------------------------------------
 * PADRAO POST-REDIRECT-GET (PRG)
 * -----------------------------------------------------------------------
 * Apos um POST bem-sucedido, fazemos um redirect (response.sendRedirect)
 * em vez de um forward para a JSP. Isso evita que o usuario, ao pressionar
 * F5 no browser, reenvie o formulario e insira mensagem duplicada.
 *
 * O padrao PRG foi descrito formalmente em 2004 mas era pratica comum
 * antes disso. Muitos sistemas antigos que nao usavam PRG tinham bugs
 * de duplicacao bem conhecidos pelos usuarios ("clique apenas uma vez").
 *
 * -----------------------------------------------------------------------
 * REQUEST ATTRIBUTES COMO MECANISMO DE COMUNICACAO VIEW-CONTROLLER
 * -----------------------------------------------------------------------
 * O Servlet coloca objetos no request com request.setAttribute() e
 * faz forward para a JSP. A JSP recupera com request.getAttribute()
 * (ou pela Expression Language ${} em JSP 2.0+).
 *
 * Este era o mecanismo de passagem de dados Controller -> View em
 * qualquer framework MVC Java da epoca: Struts, Spring MVC, JSF internamente
 * todos usam o mesmo mecanismo de request/session attributes.
 */
public class MensagemServlet extends HttpServlet {

    private MensagemDao dao;

    /**
     * init() e chamado pelo container uma vez, apos instanciar o Servlet.
     * E o lugar correto para inicializar recursos que duram toda a vida
     * do Servlet (ex.: DAO, referencias a servicos).
     *
     * Instanciar o DAO aqui e uma simplificacao didatica: sem injecao
     * de dependencia, o Servlet cria seu proprio DAO. Frameworks como
     * Spring eliminaram esse acoplamento com IoC.
     */
    public void init() throws ServletException {
        dao = new MensagemDao();
        log("[MensagemServlet] Inicializado.");
    }

    /**
     * GET: exibe a lista de mensagens e o formulario de adicao.
     *
     * O parametro "acao=remover&id=X" permite remover uma mensagem
     * via link GET — tecnicamente incorreto pelo principio REST
     * (modificacoes de estado deveriam ser POST/DELETE), mas era
     * pratica comum em aplicacoes web da epoca para simplicidade.
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String acao = request.getParameter("acao");

        if ("remover".equals(acao)) {
            processarRemocao(request, response);
            return;
        }

        exibirLista(request, response);
    }

    /**
     * POST: recebe o formulario de adicao de mensagem.
     *
     * Os dados do formulario chegam como parametros da requisicao via
     * request.getParameter(). Nao havia binding automatico de parametros
     * para objetos Java (como @RequestParam ou @ModelAttribute do Spring MVC).
     * Cada campo precisava ser lido individualmente e validado manualmente.
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        /*
         * Em Servlet 2.3, o encoding da requisicao precisava ser definido
         * explicitamente antes da primeira chamada a getParameter().
         * Sem isso, caracteres acentuados (UTF-8) eram lidos incorretamente
         * — causa de muito "lixo de encoding" em sistemas legados.
         *
         * request.setCharacterEncoding() foi introduzido na Servlet 2.3.
         * Antes disso, desenvolvedores faziam:
         *   String autor = new String(raw.getBytes("ISO-8859-1"), "UTF-8");
         * — um hack doloroso que deixava marcas em todo o codigo.
         */
        request.setCharacterEncoding("UTF-8");

        String autor    = request.getParameter("autor");
        String conteudo = request.getParameter("conteudo");

        // --- validacao ---
        if (autor == null || autor.trim().length() == 0 ||
            conteudo == null || conteudo.trim().length() == 0) {

            request.setAttribute("erro", "Os campos 'autor' e 'conteudo' sao obrigatorios.");
            exibirLista(request, response);
            return;
        }

        if (autor.trim().length() > 255) {
            request.setAttribute("erro", "O campo 'autor' deve ter no maximo 255 caracteres.");
            exibirLista(request, response);
            return;
        }

        // --- persistir ---
        try {
            Mensagem nova = new Mensagem(autor.trim(), conteudo.trim());
            dao.inserir(nova);

            /*
             * Post-Redirect-Get: apos INSERT bem-sucedido, redirecionamos
             * para o GET. O browser substitui o historico; F5 nao reinsere.
             *
             * response.sendRedirect() envia HTTP 302. O browser faz nova
             * requisicao GET para a URL indicada. O Servlet e chamado de
             * novo via doGet(), que busca a lista atualizada do banco.
             */
            response.sendRedirect(request.getContextPath() + "/mensagens?ok=1");

        } catch (SQLException e) {
            log("[MensagemServlet] Erro ao inserir mensagem: " + e.getMessage(), e);
            request.setAttribute("erro", "Erro ao salvar mensagem. Tente novamente.");
            exibirLista(request, response);
        }
    }

    // -----------------------------------------------------------------------
    // METODOS AUXILIARES
    // -----------------------------------------------------------------------

    private void exibirLista(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            List mensagens = dao.listar();
            int  total     = dao.contar();

            /*
             * setAttribute coloca objetos no escopo da requisicao.
             * A JSP acessa via ${mensagens} (EL, JSP 2.0) ou
             * via <%=(List)request.getAttribute("mensagens")%> (scriptlet).
             *
             * O escopo de request dura apenas a duracao de um ciclo
             * request-response. Para dados que precisam sobreviver entre
             * requisicoes, usava-se HttpSession (sessao do usuario) ou
             * ServletContext (escopo de aplicacao — compartilhado por todos).
             */
            request.setAttribute("mensagens", mensagens);
            request.setAttribute("total",     Integer.valueOf(total));

            // propagar mensagem de sucesso da query string
            if ("1".equals(request.getParameter("ok"))) {
                request.setAttribute("sucesso", "Mensagem adicionada com sucesso!");
            }

        } catch (SQLException e) {
            log("[MensagemServlet] Erro ao listar mensagens: " + e.getMessage(), e);
            request.setAttribute("erro", "Erro ao carregar mensagens do banco.");
        }

        /*
         * RequestDispatcher.forward() transfere o controle para a JSP.
         * A JSP e processada no servidor e o HTML resultante e enviado
         * ao cliente. O cliente nao sabe que houve um forward — a URL
         * no browser nao muda (diferente do sendRedirect).
         *
         * O caminho /WEB-INF/views/ e proposital: arquivos dentro de
         * WEB-INF nao sao acessiveis diretamente pelo browser — apenas
         * via forward do servidor. Isso impede acesso direto as JSPs
         * sem passar pelo Servlet controlador.
         */
        RequestDispatcher dispatcher =
            request.getRequestDispatcher("/WEB-INF/views/listar.jsp");
        dispatcher.forward(request, response);
    }

    private void processarRemocao(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        String idParam = request.getParameter("id");
        if (idParam != null) {
            try {
                int id = Integer.parseInt(idParam);
                dao.remover(id);
            } catch (NumberFormatException e) {
                // id invalido: ignorar silenciosamente e redirecionar
            } catch (SQLException e) {
                log("[MensagemServlet] Erro ao remover mensagem id=" + idParam + ": " + e.getMessage());
            }
        }

        response.sendRedirect(request.getContextPath() + "/mensagens");
    }

    public void destroy() {
        log("[MensagemServlet] Destruido.");
        dao = null;
    }
}
