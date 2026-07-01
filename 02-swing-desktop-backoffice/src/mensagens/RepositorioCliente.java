package mensagens;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RepositorioCliente {

    private static final Logger LOG = Logger.getLogger(RepositorioCliente.class.getName());
    private static final String ARQUIVO = "clientes.dat";
    private List<Cliente> clientes;

    public RepositorioCliente() {
        this.clientes = new ArrayList<>();
        carregar();
    }

    public void adicionar(Cliente c) {
        clientes.add(c);
        salvar();
    }

    public void remover(int index) {
        if (index >= 0 && index < clientes.size()) {
            clientes.remove(index);
            salvar();
        }
    }

    public void atualizar(int index, Cliente c) {
        if (index >= 0 && index < clientes.size()) {
            clientes.set(index, c);
            salvar();
        }
    }

    public List<Cliente> listar() {
        return new ArrayList<>(clientes);
    }

    public int size() {
        return clientes.size();
    }

    private void salvar() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(ARQUIVO))) {
            oos.writeObject(clientes);
        } catch (IOException e) {
            LOG.log(Level.SEVERE, "Falha ao salvar clientes em " + ARQUIVO, e);
        }
    }

    @SuppressWarnings("unchecked")
    private void carregar() {
        File arquivo = new File(ARQUIVO);
        if (arquivo.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(arquivo))) {
                clientes = (List<Cliente>) ois.readObject();
            } catch (IOException | ClassNotFoundException e) {
                LOG.log(Level.SEVERE, "Falha ao carregar clientes de " + ARQUIVO, e);
            }
        }
    }
}
