package contas.config;

import contas.model.Conta;
import contas.repository.ContaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class CarregadorInicial implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(CarregadorInicial.class);

    private final ContaRepository repository;

    public CarregadorInicial(ContaRepository repository) {
        this.repository = repository;
    }

    @Override
    public void run(String... args) {
        log.info("Carregando dados iniciais...");

        Conta c1 = new Conta();
        c1.setDescricao("Aluguel");
        c1.setValor(2500.0);
        c1.setTipo("PAGAR");
        c1.setVencimento(LocalDate.of(2024, 6, 10));
        repository.save(c1);

        Conta c2 = new Conta();
        c2.setDescricao("Salario");
        c2.setValor(8000.0);
        c2.setTipo("RECEBER");
        c2.setVencimento(LocalDate.of(2024, 6, 5));
        repository.save(c2);

        Conta c3 = new Conta();
        c3.setDescricao("Conta de Luz");
        c3.setValor(320.0);
        c3.setTipo("PAGAR");
        c3.setVencimento(LocalDate.of(2024, 6, 15));
        repository.save(c3);

        Conta c4 = new Conta();
        c4.setDescricao("Freelance");
        c4.setValor(1500.0);
        c4.setTipo("RECEBER");
        c4.setVencimento(LocalDate.of(2024, 6, 20));
        repository.save(c4);

        log.info("Dados iniciais carregados com sucesso. Total: {}", repository.count());
    }
}
