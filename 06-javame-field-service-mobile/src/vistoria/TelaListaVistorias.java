package vistoria;

import javax.microedition.lcdui.*;
import javax.microedition.rms.*;
import java.io.*;

public class TelaListaVistorias extends List implements CommandListener {

    private Command cmdNova;
    private Command cmdDetalhes;
    private Command cmdSair;
    private VistoriaMidlet midlet;

    public TelaListaVistorias(VistoriaMidlet midlet) {
        super("Vistorias", List.IMPLICIT);
        this.midlet = midlet;

        cmdNova = new Command("Nova vistoria", Command.OK, 1);
        cmdDetalhes = new Command("Detalhes", Command.ITEM, 2);
        cmdSair = new Command("Sair", Command.EXIT, 3);

        addCommand(cmdNova);
        addCommand(cmdDetalhes);
        addCommand(cmdSair);
        setCommandListener(this);
    }

    public void atualizar() {
        deleteAll();
        try {
            RepositorioRMS repositorio = new RepositorioRMS();
            RecordEnumeration re = repositorio.enumerar();
            while (re.hasNextElement()) {
                int recordId = re.nextRecordId();
                RecordStore rs = RecordStore.openRecordStore("VistoriasStore", false);
                byte[] dados = rs.getRecord(recordId);
                ByteArrayInputStream bais = new ByteArrayInputStream(dados);
                DataInputStream dis = new DataInputStream(bais);
                String codigo = dis.readUTF();
                String status = dis.readUTF();
                dis.close();
                bais.close();
                rs.closeRecordStore();
                append(codigo + " - " + status, null);
            }
            re.destroy();
        } catch (Exception e) {
            // sem registros ainda — lista vazia
        }
    }

    public void commandAction(Command c, Displayable d) {
        if (c == cmdNova) {
            midlet.mostrarFormulario();
        } else if (c == cmdDetalhes || c == List.SELECT_COMMAND) {
            int index = getSelectedIndex();
            if (index >= 0) {
                String item = getString(index);
                Alert alert = new Alert("Detalhes", item, null, AlertType.INFO);
                alert.setTimeout(3000);
                midlet.getDisplay().setCurrent(alert);
            }
        } else if (c == cmdSair) {
            midlet.notifyDestroyed();
        }
    }
}
