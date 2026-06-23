package pedidos.config;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class HealthConfig implements HealthIndicator {

    private long errorCount = 0;

    public void registrarErro() {
        errorCount++;
    }

    @Override
    public Health health() {
        if (errorCount > 10) {
            return Health.down()
                    .withDetail("PEDIDOS_STATUS", "EXCESSO_ERROS")
                    .withDetail("erros_recentes", errorCount)
                    .build();
        }
        return Health.up()
                .withDetail("PEDIDOS_STATUS", "OPERACIONAL")
                .withDetail("erros_recentes", errorCount)
                .build();
    }
}
