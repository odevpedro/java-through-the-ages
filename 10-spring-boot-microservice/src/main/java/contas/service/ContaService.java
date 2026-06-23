package contas.service;

import contas.model.Conta;
import contas.repository.ContaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ContaService {

    private final ContaRepository repository;

    public ContaService(ContaRepository repository) {
        this.repository = repository;
    }

    public List<Conta> listarTodas() {
        return repository.findAll();
    }

    public Conta buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Conta nao encontrada: " + id));
    }

    @Transactional
    public Conta criar(Conta conta) {
        conta.setStatus("PENDENTE");
        return repository.save(conta);
    }

    @Transactional
    public Conta pagar(Long id) {
        Conta conta = buscarPorId(id);
        conta.setStatus("PAGO");
        return repository.save(conta);
    }

    @Transactional
    public void deletar(Long id) {
        Conta conta = buscarPorId(id);
        repository.delete(conta);
    }
}
