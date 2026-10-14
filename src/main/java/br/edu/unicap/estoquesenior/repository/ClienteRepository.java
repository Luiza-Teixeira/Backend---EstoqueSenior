package br.edu.unicap.estoquesenior.repository;

import br.edu.unicap.estoquesenior.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClienteRepository
        extends JpaRepository<Cliente, Long> {
}