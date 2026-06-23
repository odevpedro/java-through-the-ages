package catalogo;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/api/produtos")
public class ProdutoController {
    private ProdutoRepository repository;

    public void setRepository(ProdutoRepository repository) {
        this.repository = repository;
    }

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public List<Produto> listar() {
        return repository.findAll();
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ResponseBody
    public Produto obter(@PathVariable int id) {
        return repository.findById(id);
    }

    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public Produto criar(@RequestBody Produto produto) {
        return repository.save(produto);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    @ResponseBody
    public Produto atualizar(@PathVariable int id, @RequestBody Produto produto) {
        return repository.update(id, produto);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    public String deletar(@PathVariable int id) {
        Produto removido = repository.delete(id);
        return (removido != null) ? "{\"message\":\"Produto removido com sucesso\"}"
                                 : "{\"message\":\"Produto nao encontrado\"}";
    }
}
