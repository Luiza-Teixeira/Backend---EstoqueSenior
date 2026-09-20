package br.edu.unicap.estoquesenior.repository;

import br.edu.unicap.estoquesenior.model.Produto;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Transactional
class ProdutoRepositoryTeste {

    @Autowired
    private ProdutoRepository produtoRepository;

    @Test
    void deveSalvarProduto() {
        Produto produto = new Produto();
        produto.setNome("Coca-Cola 2L");
        produto.setPrecoVenda(new BigDecimal("10.00"));
        produto.setQuantidadeEstoque(5);
        produto.setEstoqueMinimo(2);

        Produto salvo = produtoRepository.save(produto);

        Optional<Produto> encontrado =
                produtoRepository.findById(salvo.getId());

        assertTrue(encontrado.isPresent());
    }
}