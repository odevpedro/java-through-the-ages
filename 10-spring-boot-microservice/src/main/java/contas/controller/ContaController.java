package contas.controller;

import contas.dto.ContaDTO;
import contas.model.Conta;
import contas.service.ContaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/contas")
public class ContaController {

    private final ContaService service;

    public ContaController(ContaService service) {
        this.service = service;
    }

    @GetMapping
    public List<ContaDTO> listar() {
        return service.listarTodas().stream()
                .map(ContaDTO::new)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ContaDTO> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(new ContaDTO(service.buscarPorId(id)));
    }

    @PostMapping
    public ResponseEntity<ContaDTO> criar(@RequestBody ContaDTO dto) {
        Conta criada = service.criar(dto.toEntity());
        return ResponseEntity.status(HttpStatus.CREATED).body(new ContaDTO(criada));
    }

    @PutMapping("/{id}/pagar")
    public ResponseEntity<ContaDTO> pagar(@PathVariable Long id) {
        return ResponseEntity.ok(new ContaDTO(service.pagar(id)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
