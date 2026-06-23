package vistoria;

import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;

public class VistoriaMidlet extends MIDlet {

    private Display display;
    private TelaListaVistorias telaLista;
    private TelaVistoria telaVistoria;

    public void startApp() {
        display = Display.getDisplay(this);
        if (telaLista == null) {
            telaLista = new TelaListaVistorias(this);
        }
        if (telaVistoria == null) {
            telaVistoria = new TelaVistoria(this);
        }
        telaLista.atualizar();
        display.setCurrent(telaLista);
    }

    public void pauseApp() {
        // liberar recursos se necessario
    }

    public void destroyApp(boolean unconditional) {
        // cleanup
    }

    public Display getDisplay() {
        return display;
    }

    public TelaListaVistorias getTelaLista() {
        return telaLista;
    }

    public void mostrarLista() {
        telaLista.atualizar();
        display.setCurrent(telaLista);
    }

    public void mostrarFormulario() {
        display.setCurrent(telaVistoria);
    }
}
