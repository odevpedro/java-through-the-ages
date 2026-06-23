package contas.repository;

import contas.model.Conta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContaRepository extends JpaRepository<Conta, Long> {

    List<Conta> findByStatus(String status);

    List<Conta> findByTipo(String tipo);
}
