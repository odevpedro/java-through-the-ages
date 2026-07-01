package catalogo;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/produtos")
public class ProdutoController {
    private ProdutoRepository repository;

    public void setRepository(ProdutoRepository repository) {
        this.repository = repository;
    }

    @RequestMapping(method = RequestMethod.GET)
    public List<ProdutoDTO> listar() {
        return repository.findAll().stream()
                .map(ProdutoDTO::new)
                .collect(Collectors.toList());
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<ProdutoDTO> obter(@PathVariable int id) {
        Produto p = repository.findById(id);
        if (p == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(new ProdutoDTO(p));
    }

    @RequestMapping(method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public ProdutoDTO criar(@RequestBody ProdutoDTO dto) {
        Produto salvo = repository.save(dto.toEntity());
        return new ProdutoDTO(salvo);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public ResponseEntity<ProdutoDTO> atualizar(@PathVariable int id, @RequestBody ProdutoDTO dto) {
        if (repository.findById(id) == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(new ProdutoDTO(repository.update(id, dto.toEntity())));
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Map<String, String>> deletar(@PathVariable int id) {
        Produto removido = repository.delete(id);
        Map<String, String> resposta = new HashMap<>();
        if (removido != null) {
            resposta.put("message", "Produto removido com sucesso");
            return ResponseEntity.ok(resposta);
        }
        resposta.put("message", "Produto nao encontrado");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(resposta);
    }
}
