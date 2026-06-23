package mensagens;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ChamadoServlet extends HttpServlet {

    private ChamadoDao dao = new ChamadoDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            List<Chamado> chamados = dao.listar();
            req.setAttribute("chamados", chamados);
            RequestDispatcher dispatcher = req.getRequestDispatcher("/WEB-INF/views/listagem.jsp");
            dispatcher.forward(req, resp);
        } catch (Exception e) {
            throw new ServletException("Erro ao listar chamados", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String titulo = req.getParameter("titulo");
        String descricao = req.getParameter("descricao");
        String solicitante = req.getParameter("solicitante");

        if (titulo == null || titulo.trim().isEmpty() ||
            descricao == null || descricao.trim().isEmpty() ||
            solicitante == null || solicitante.trim().isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/WEB-INF/views/formulario.jsp?erro=campos+obrigatorios");
            return;
        }

        Chamado c = new Chamado();
        c.setTitulo(titulo.trim());
        c.setDescricao(descricao.trim());
        c.setSolicitante(solicitante.trim());
        c.setDataAbertura(new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date()));

        try {
            dao.inserir(c);
            resp.sendRedirect(req.getContextPath() + "/chamados");
        } catch (Exception e) {
            throw new ServletException("Erro ao inserir chamado", e);
        }
    }
}
