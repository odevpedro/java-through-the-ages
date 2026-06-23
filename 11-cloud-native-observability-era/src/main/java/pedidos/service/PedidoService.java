package pedidos.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pedidos.model.Pedido;
import pedidos.repository.PedidoRepository;

import java.util.List;

@Service
public class PedidoService {

    private static final Logger log = LoggerFactory.getLogger(PedidoService.class);

    private final PedidoRepository repository;

    public PedidoService(PedidoRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public Pedido criarPedido(Pedido pedido) {
        pedido.setStatus("CRIADO");
        pedido.setCriadoEm(java.time.LocalDateTime.now());
        pedido.setTentativas(0);
        Pedido saved = repository.save(pedido);

        MDC.put("pedidoId", saved.getId().toString());
        log.info("Pedido criado | cliente={} | valor={} | status={}",
                saved.getCliente(), saved.getValor(), saved.getStatus());
        MDC.clear();

        processarPedido(saved);
        return saved;
    }

    @Async
    public void processarPedido(Pedido pedido) {
        MDC.put("pedidoId", pedido.getId().toString());
        log.info("Iniciando processamento | status={} | tentativa={}",
                pedido.getStatus(), pedido.getTentativas() + 1);

        pedido.setStatus("PROCESSANDO");
        repository.save(pedido);
        log.info("Status alterado para PROCESSANDO");

        try {
            Thread.sleep(2000);
            double risco = Math.random();
            if (risco < 0.2) {
                throw new RuntimeException("Erro simulado no processamento");
            }
            pedido.setStatus("CONCLUIDO");
            repository.save(pedido);
            log.info("Pedido processado com sucesso | status=CONCLUIDO");
        } catch (Exception e) {
            pedido.setTentativas(pedido.getTentativas() + 1);
            if (pedido.getTentativas() >= 3) {
                pedido.setStatus("ERRO");
                log.error("Pedido falhou apos {} tentativas | erro={}",
                        pedido.getTentativas(), e.getMessage());
            } else {
                pedido.setStatus("CRIADO");
                log.warn("Pedido falhou na tentativa {} | erro={} | sera reprocessado",
                        pedido.getTentativas(), e.getMessage());
                processarPedido(pedido);
            }
            repository.save(pedido);
        } finally {
            MDC.clear();
        }
    }

    public List<Pedido> listarTodos() {
        return repository.findAll();
    }

    public Pedido buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pedido nao encontrado: " + id));
    }
}
