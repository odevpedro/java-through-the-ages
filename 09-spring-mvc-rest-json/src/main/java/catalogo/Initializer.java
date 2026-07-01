package catalogo;

import org.springframework.beans.factory.InitializingBean;

import java.math.BigDecimal;

public class Initializer implements InitializingBean {
    private ProdutoRepository repository;

    public void setRepository(ProdutoRepository repository) {
        this.repository = repository;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        repository.save(new Produto(0, "Notebook Dell", new BigDecimal("4500.00"), "Informatica"));
        repository.save(new Produto(0, "Monitor 27\"", new BigDecimal("1500.00"), "Informatica"));
        repository.save(new Produto(0, "Teclado Mecanico", new BigDecimal("350.00"), "Perifericos"));
        repository.save(new Produto(0, "Mouse Wireless", new BigDecimal("120.00"), "Perifericos"));
        repository.save(new Produto(0, "Webcam HD", new BigDecimal("280.00"), "Perifericos"));
        System.out.println(">>> Catalogo inicializado com 5 produtos");
    }
}
