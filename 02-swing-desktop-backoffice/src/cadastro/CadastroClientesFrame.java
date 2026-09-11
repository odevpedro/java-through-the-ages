package cadastro;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class CadastroClientesFrame extends JFrame {

    private static final String[] COLUNAS = {"Nome", "Email", "Telefone"};

    private final RepositorioCliente repositorio = new RepositorioCliente();
    private final DefaultTableModel tableModel = new DefaultTableModel(COLUNAS, 0);
    private final JTable tabela = new JTable(tableModel);

    private final JTextField campoNome = new JTextField(20);
    private final JTextField campoEmail = new JTextField(20);
    private final JTextField campoTelefone = new JTextField(15);
    private final JLabel labelStatus = new JLabel("Pronto.");

    public CadastroClientesFrame() {
        super("Cadastro de Clientes - Swing Backoffice");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setSize(700, 500);
        setLocationRelativeTo(null);

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                int resp = JOptionPane.showConfirmDialog(
                    CadastroClientesFrame.this,
                    "Deseja realmente sair?",
                    "Confirmar saida",
                    JOptionPane.YES_NO_OPTION
                );
                if (resp == JOptionPane.YES_OPTION) {
                    dispose();
                }
            }
        });

        criarMenu();
        criarFormulario();
        criarTabela();
        criarBotoesAcao();
        carregarDados();

        setVisible(true);
    }

    private void criarMenu() {
        JMenuBar barra = new JMenuBar();

        JMenu menuArquivo = new JMenu("Arquivo");
        JMenuItem itemSair = new JMenuItem("Sair");
        itemSair.addActionListener(e -> {
            int resp = JOptionPane.showConfirmDialog(
                this, "Deseja realmente sair?", "Sair",
                JOptionPane.YES_NO_OPTION
            );
            if (resp == JOptionPane.YES_OPTION) {
                dispose();
            }
        });
        menuArquivo.add(itemSair);
        barra.add(menuArquivo);

        JMenu menuAjuda = new JMenu("Ajuda");
        JMenuItem itemSobre = new JMenuItem("Sobre");
        itemSobre.addActionListener(e ->
            JOptionPane.showMessageDialog(
                this,
                "Modulo 02 — Swing Desktop Backoffice\n"
                + "Era: 1998-2002 | JDK 1.2+\n"
                + "Cadastro de clientes com persistencia serializada.",
                "Sobre",
                JOptionPane.INFORMATION_MESSAGE
            )
        );
        menuAjuda.add(itemSobre);
        barra.add(menuAjuda);

        setJMenuBar(barra);
    }

    private void criarFormulario() {
        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0;
        form.add(new JLabel("Nome:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1;
        form.add(campoNome, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0; gbc.fill = GridBagConstraints.NONE;
        form.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1;
        form.add(campoEmail, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0; gbc.fill = GridBagConstraints.NONE;
        form.add(new JLabel("Telefone:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1;
        form.add(campoTelefone, gbc);

        JPanel botoesForm = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnSalvar = new JButton("Salvar");
        btnSalvar.addActionListener(this::salvarCliente);
        botoesForm.add(btnSalvar);

        JButton btnLimpar = new JButton("Limpar");
        btnLimpar.addActionListener(e -> limparFormulario());
        botoesForm.add(btnLimpar);

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        form.add(botoesForm, gbc);

        add(form, BorderLayout.NORTH);
    }

    private void criarTabela() {
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scroll = new JScrollPane(tabela);
        add(scroll, BorderLayout.CENTER);
    }

    private void criarBotoesAcao() {
        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));

        JButton btnEditar = new JButton("Editar");
        btnEditar.addActionListener(this::editarCliente);
        botoes.add(btnEditar);

        JButton btnExcluir = new JButton("Excluir");
        btnExcluir.addActionListener(this::excluirCliente);
        botoes.add(btnExcluir);

        add(botoes, BorderLayout.SOUTH);

        JPanel rodape = new JPanel(new BorderLayout());
        rodape.add(botoes, BorderLayout.CENTER);
        rodape.add(labelStatus, BorderLayout.SOUTH);
        add(rodape, BorderLayout.SOUTH);
    }

    private void carregarDados() {
        tableModel.setRowCount(0);
        for (Cliente c : repositorio.listar()) {
            tableModel.addRow(new Object[]{c.getNome(), c.getEmail(), c.getTelefone()});
        }
        labelStatus.setText("Registros carregados: " + repositorio.size());
    }

    private void salvarCliente(ActionEvent e) {
        String nome = campoNome.getText().trim();
        String email = campoEmail.getText().trim();
        String telefone = campoTelefone.getText().trim();

        if (nome.isEmpty()) {
            JOptionPane.showMessageDialog(this, "O campo Nome e obrigatorio.", "Validacao", JOptionPane.WARNING_MESSAGE);
            campoNome.requestFocus();
            return;
        }

        Cliente cliente = new Cliente(nome, email, telefone);
        repositorio.adicionar(cliente);
        tableModel.addRow(new Object[]{nome, email, telefone});
        limparFormulario();
        labelStatus.setText("Cliente cadastrado: " + nome);
    }

    private void editarCliente(ActionEvent e) {
        int linha = tabela.getSelectedRow();
        if (linha < 0) {
            JOptionPane.showMessageDialog(this, "Selecione um cliente na tabela para editar.", "Aviso", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String nome = campoNome.getText().trim();
        String email = campoEmail.getText().trim();
        String telefone = campoTelefone.getText().trim();

        if (nome.isEmpty()) {
            JOptionPane.showMessageDialog(this, "O campo Nome e obrigatorio.", "Validacao", JOptionPane.WARNING_MESSAGE);
            campoNome.requestFocus();
            return;
        }

        repositorio.atualizar(linha, new Cliente(nome, email, telefone));
        tableModel.setValueAt(nome, linha, 0);
        tableModel.setValueAt(email, linha, 1);
        tableModel.setValueAt(telefone, linha, 2);
        limparFormulario();
        labelStatus.setText("Cliente atualizado: " + nome);
    }

    private void excluirCliente(ActionEvent e) {
        int linha = tabela.getSelectedRow();
        if (linha < 0) {
            JOptionPane.showMessageDialog(this, "Selecione um cliente na tabela para excluir.", "Aviso", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String nome = (String) tableModel.getValueAt(linha, 0);
        int resp = JOptionPane.showConfirmDialog(
            this,
            "Excluir cliente \"" + nome + "\"?",
            "Confirmar exclusao",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );
        if (resp != JOptionPane.YES_OPTION) {
            return;
        }

        repositorio.remover(linha);
        tableModel.removeRow(linha);
        limparFormulario();
        labelStatus.setText("Cliente excluido: " + nome);
    }

    private void limparFormulario() {
        campoNome.setText("");
        campoEmail.setText("");
        campoTelefone.setText("");
        campoNome.requestFocus();
    }
}
