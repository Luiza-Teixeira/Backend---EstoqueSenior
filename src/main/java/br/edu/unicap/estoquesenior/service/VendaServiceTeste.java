package br.edu.unicap.estoquesenior.service;

import br.edu.unicap.estoquesenior.model.Produto;
import br.edu.unicap.estoquesenior.model.Venda;
import br.edu.unicap.estoquesenior.repository.ProdutoRepository;
import br.edu.unicap.estoquesenior.repository.VendaRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class VendaServiceTeste {

    @Autowired
    private VendaService vendaService;

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private VendaRepository vendaRepository;

    @Test
    void deveRegistrarVendaComSucesso() {

        // Arrange
        Produto produto = new Produto();
        produto.setNome("Coca-Cola 2L");
        produto.setPrecoVenda(new BigDecimal("10.00"));
        produto.setQuantidadeEstoque(10);
        produto.setEstoqueMinimo(2);

        produto = produtoRepository.save(produto);

        // Act
        Venda venda = vendaService.registrarVenda(produto.getId(), 2);

        // Assert
        Produto produtoAtualizado = produtoRepository.findById(produto.getId()).get();

        assertNotNull(venda);
        assertNotNull(venda.getId());
        assertEquals(2, venda.getQuantidade());
        assertEquals(new BigDecimal("10.00"), venda.getValorUnitario());
        assertEquals(8, produtoAtualizado.getQuantidadeEstoque());
        assertNotNull(venda.getDataHora());
    }

    @Test
    void naoDeveRegistrarVendaComEstoqueInsuficiente() {

        Produto produto = new Produto();
        produto.setNome("Chocolate");
        produto.setPrecoVenda(new BigDecimal("5.00"));
        produto.setQuantidadeEstoque(3);
        produto.setEstoqueMinimo(1);

        produto = produtoRepository.save(produto);

        IllegalArgumentException erro = assertThrows(
                IllegalArgumentException.class,
                () -> vendaService.registrarVenda(produto.getId(), 5)
        );

        assertEquals("Estoque insuficiente.", erro.getMessage());

        Produto produtoAtualizado = produtoRepository.findById(produto.getId()).get();
        assertEquals(3, produtoAtualizado.getQuantidadeEstoque());
    }

    @Test
    void naoDeveRegistrarVendaComQuantidadeZero() {

        Produto produto = new Produto();
        produto.setNome("Biscoito");
        produto.setPrecoVenda(new BigDecimal("3.00"));
        produto.setQuantidadeEstoque(10);
        produto.setEstoqueMinimo(2);

        produto = produtoRepository.save(produto);

        IllegalArgumentException erro = assertThrows(
                IllegalArgumentException.class,
                () -> vendaService.registrarVenda(produto.getId(), 0)
        );

        assertEquals(
                "A quantidade da venda deve ser maior que zero.",
                erro.getMessage()
        );
    }

    @Test
    void naoDeveRegistrarVendaComQuantidadeNegativa() {

        Produto produto = new Produto();
        produto.setNome("Refrigerante");
        produto.setPrecoVenda(new BigDecimal("8.00"));
        produto.setQuantidadeEstoque(10);
        produto.setEstoqueMinimo(2);

        produto = produtoRepository.save(produto);

        IllegalArgumentException erro = assertThrows(
                IllegalArgumentException.class,
                () -> vendaService.registrarVenda(produto.getId(), -1)
        );

        assertEquals(
                "A quantidade da venda deve ser maior que zero.",
                erro.getMessage()
        );
    }

    @Test
    void naoDeveRegistrarVendaDeProdutoInexistente() {

        IllegalArgumentException erro = assertThrows(
                IllegalArgumentException.class,
                () -> vendaService.registrarVenda(999L, 2)
        );

        assertEquals("Produto não encontrado.", erro.getMessage());
    }

	public VendaService getVendaService() {
		return vendaService;
	}

	public void setVendaService(VendaService vendaService) {
		this.vendaService = vendaService;
	}

	public ProdutoRepository getProdutoRepository() {
		return produtoRepository;
	}

	public void setProdutoRepository(ProdutoRepository produtoRepository) {
		this.produtoRepository = produtoRepository;
	}

	public VendaRepository getVendaRepository() {
		return vendaRepository;
	}

	public void setVendaRepository(VendaRepository vendaRepository) {
		this.vendaRepository = vendaRepository;
	}
}