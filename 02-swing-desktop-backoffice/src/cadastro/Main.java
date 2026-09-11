package cadastro;

import javax.swing.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {

    private static final Logger LOG = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                LOG.log(Level.WARNING, "Nao foi possivel definir o Look and Feel padrao", e);
            }
            new CadastroClientesFrame();
        });
    }
}
