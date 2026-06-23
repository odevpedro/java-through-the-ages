package catalogo;

import org.springframework.beans.factory.InitializingBean;

public class Initializer implements InitializingBean {
    private ProdutoRepository repository;

    public void setRepository(ProdutoRepository repository) {
        this.repository = repository;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        repository.save(new Produto(0, "Notebook Dell", 4500.00, "Informatica"));
        repository.save(new Produto(0, "Monitor 27\"", 1500.00, "Informatica"));
        repository.save(new Produto(0, "Teclado Mecanico", 350.00, "Perifericos"));
        repository.save(new Produto(0, "Mouse Wireless", 120.00, "Perifericos"));
        repository.save(new Produto(0, "Webcam HD", 280.00, "Perifericos"));
        System.out.println(">>> Catalogo inicializado com 5 produtos");
    }
}
