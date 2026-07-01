package pedidos.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pedidos.dto.PedidoDTO;
import pedidos.model.Pedido;
import pedidos.service.PedidoService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    private final PedidoService service;

    public PedidoController(PedidoService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<PedidoDTO> criar(@RequestBody PedidoDTO dto) {
        Pedido criado = service.criarPedido(dto.toEntity());
        return ResponseEntity.status(HttpStatus.CREATED).body(new PedidoDTO(criado));
    }

    @GetMapping
    public List<PedidoDTO> listar() {
        return service.listarTodos().stream()
                .map(PedidoDTO::new)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PedidoDTO> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(new PedidoDTO(service.buscarPorId(id)));
    }
}
