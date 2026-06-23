package mensagens;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class RepositorioCliente {
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
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    private void carregar() {
        File arquivo = new File(ARQUIVO);
        if (arquivo.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(arquivo))) {
                clientes = (List<Cliente>) ois.readObject();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}
