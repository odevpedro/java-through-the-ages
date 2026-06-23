package pedidos.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class CarregadorInicial implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(CarregadorInicial.class);

    @Override
    public void run(String... args) {
        log.info("Servico de processamento assincrono de pedidos iniciado");
        log.info("Endpoints disponiveis:");
        log.info("  POST /api/pedidos - Criar pedido");
        log.info("  GET  /api/pedidos - Listar pedidos");
        log.info("  GET  /api/pedidos/{id} - Buscar pedido");
        log.info("  GET  /actuator/health - Health check");
        log.info("  GET  /actuator/prometheus - Metricas Prometheus");
    }
}
