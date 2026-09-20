package br.edu.unicap.estoquesenior.repository;

import br.edu.unicap.estoquesenior.model.Produto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProdutoRepository extends JpaRepository<Produto, Long> {

}
