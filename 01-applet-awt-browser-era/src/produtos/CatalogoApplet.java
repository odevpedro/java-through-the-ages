package produtos;

import java.applet.Applet;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;
import java.util.Vector;

public class CatalogoApplet extends Applet implements ActionListener {

    private Vector<Produto> catalogo;
    private Choice seletorProduto;
    private TextField campoQuantidade;
    private Button btnCalcular;
    private Button btnLimpar;
    private TextArea areaResultado;
    private Label labelStatus;

    public void init() {
        catalogo = new Vector<Produto>();
        catalogo.addElement(new Produto("Teclado Mecanico", 249.90));
        catalogo.addElement(new Produto("Mouse Optico", 89.50));
        catalogo.addElement(new Produto("Monitor LED 24", 899.00));
        catalogo.addElement(new Produto("Webcam HD", 159.00));
        catalogo.addElement(new Produto("Fone Bluetooth", 199.90));

        setLayout(new BorderLayout(10, 10));
        setBackground(Color.LIGHT_GRAY);

        Panel topo = new Panel(new GridLayout(3, 2, 5, 5));
        topo.add(new Label("Produto:"));
        seletorProduto = new Choice();
        Enumeration<Produto> e = catalogo.elements();
        while (e.hasMoreElements()) {
            Produto p = e.nextElement();
            seletorProduto.addItem(p.getNome());
        }
        topo.add(seletorProduto);

        topo.add(new Label("Quantidade:"));
        campoQuantidade = new TextField("1", 10);
        topo.add(campoQuantidade);

        btnCalcular = new Button("Calcular");
        btnCalcular.addActionListener(this);
        topo.add(btnCalcular);

        btnLimpar = new Button("Limpar");
        btnLimpar.addActionListener(this);
        topo.add(btnLimpar);

        add(topo, BorderLayout.NORTH);

        areaResultado = new TextArea(10, 50);
        areaResultado.setEditable(false);
        add(new ScrollPane() {{ add(areaResultado); }}, BorderLayout.CENTER);

        labelStatus = new Label("Selecione um produto e clique em Calcular.");
        add(labelStatus, BorderLayout.SOUTH);
    }

    public void actionPerformed(ActionEvent evt) {
        if (evt.getSource() == btnCalcular) {
            calcular();
        } else if (evt.getSource() == btnLimpar) {
            limpar();
        }
    }

    private void calcular() {
        String nomeSelecionado = seletorProduto.getSelectedItem();
        Produto produtoSelecionado = null;
        Enumeration<Produto> e = catalogo.elements();
        while (e.hasMoreElements()) {
            Produto p = e.nextElement();
            if (p.getNome().equals(nomeSelecionado)) {
                produtoSelecionado = p;
                break;
            }
        }

        if (produtoSelecionado == null) {
            labelStatus.setText("Erro: produto nao encontrado.");
            return;
        }

        int quantidade;
        try {
            quantidade = Integer.parseInt(campoQuantidade.getText().trim());
        } catch (NumberFormatException ex) {
            labelStatus.setText("Erro: digite um numero valido para quantidade.");
            return;
        }

        if (quantidade <= 0) {
            labelStatus.setText("Erro: a quantidade deve ser maior que zero.");
            return;
        }

        double total = quantidade * produtoSelecionado.getPrecoUnitario();
        String linha = produtoSelecionado.getNome()
            + " x " + quantidade
            + " = R$ " + String.format("%.2f", total);
        areaResultado.append(linha + "\n");
        labelStatus.setText("Orcamento calculado com sucesso.");
    }

    private void limpar() {
        campoQuantidade.setText("1");
        areaResultado.setText("");
        seletorProduto.select(0);
        labelStatus.setText("Formulario limpo.");
    }
}
