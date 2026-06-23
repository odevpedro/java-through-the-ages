package mensagens;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class InicializadorListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ChamadoDao dao = new ChamadoDao();
        try {
            dao.criarTabela();
            System.out.println("[InicializadorListener] Tabela chamados criada com sucesso.");
        } catch (Exception e) {
            throw new RuntimeException("Erro ao criar tabela chamados", e);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        // cleanup se necessario
    }
}
