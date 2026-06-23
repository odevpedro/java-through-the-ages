package catalogo;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class ProdutoRepository {
    private final Map<Integer, Produto> produtos = new ConcurrentHashMap<Integer, Produto>();
    private final AtomicInteger idGen = new AtomicInteger(1);

    public List<Produto> findAll() {
        return new ArrayList<Produto>(produtos.values());
    }

    public Produto findById(int id) {
        return produtos.get(id);
    }

    public Produto save(Produto p) {
        int id = idGen.getAndIncrement();
        p.setId(id);
        produtos.put(id, p);
        return p;
    }

    public Produto update(int id, Produto p) {
        p.setId(id);
        produtos.put(id, p);
        return p;
    }

    public Produto delete(int id) {
        return produtos.remove(id);
    }
}
