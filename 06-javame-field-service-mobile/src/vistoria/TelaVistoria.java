package vistoria;

import javax.microedition.lcdui.*;

public class TelaVistoria extends Form implements CommandListener {

    private TextField tfCodigo;
    private ChoiceGroup cgStatus;
    private TextField tfObservacao;
    private Command cmdSalvar;
    private Command cmdVoltar;
    private VistoriaMidlet midlet;

    public TelaVistoria(VistoriaMidlet midlet) {
        super("Nova Vistoria");
        this.midlet = midlet;

        tfCodigo = new TextField("Codigo Cliente", "", 20, TextField.ANY);
        cgStatus = new ChoiceGroup("Status", ChoiceGroup.EXCLUSIVE);
        cgStatus.append("Realizada", null);
        cgStatus.append("Pendente", null);
        cgStatus.append("Cancelada", null);
        tfObservacao = new TextField("Observacao", "", 200, TextField.ANY);

        cmdSalvar = new Command("Salvar", Command.OK, 1);
        cmdVoltar = new Command("Voltar", Command.BACK, 2);

        append(tfCodigo);
        append(cgStatus);
        append(tfObservacao);
        addCommand(cmdSalvar);
        addCommand(cmdVoltar);
        setCommandListener(this);
    }

    public void commandAction(Command c, Displayable d) {
        if (c == cmdSalvar) {
            String codigo = tfCodigo.getString();
            if (codigo == null || codigo.length() == 0) {
                Alert alert = new Alert("Erro", "Informe o codigo do cliente", null, AlertType.ERROR);
                alert.setTimeout(Alert.FOREVER);
                midlet.getDisplay().setCurrent(alert);
                return;
            }
            String status = cgStatus.getString(cgStatus.getSelectedIndex());
            String obs = tfObservacao.getString();
            Vistoria v = new Vistoria(codigo, status, obs);
            try {
                RepositorioRMS repositorio = new RepositorioRMS();
                repositorio.salvar(v);
                Alert alert = new Alert("Sucesso", "Vistoria salva!", null, AlertType.CONFIRMATION);
                alert.setTimeout(2000);
                midlet.getDisplay().setCurrent(alert, midlet.getTelaLista());
            } catch (Exception e) {
                Alert alert = new Alert("Erro", "Falha ao salvar: " + e.getMessage(), null, AlertType.ERROR);
                alert.setTimeout(Alert.FOREVER);
                midlet.getDisplay().setCurrent(alert);
            }
        } else if (c == cmdVoltar) {
            midlet.mostrarLista();
        }
    }
}
