package br.edu.unicap.estoquesenior.service;

import br.edu.unicap.estoquesenior.dto.ProdutoAtualizacaoDTO;
import br.edu.unicap.estoquesenior.dto.ProdutoRequestDTO;
import br.edu.unicap.estoquesenior.exception.ProdutoNaoEncontradoException;
import br.edu.unicap.estoquesenior.model.Produto;
import br.edu.unicap.estoquesenior.repository.ProdutoRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProdutoServiceTest {

    @Mock
    private ProdutoRepository produtoRepository;

    @InjectMocks
    private ProdutoService produtoService;

    private Produto produto;

    @BeforeEach
    void configurar() {
        produto = new Produto();
        produto.setId(1L);
        produto.setNome("Refrigerante");
        produto.setPrecoVenda(new BigDecimal("8.00"));
        produto.setQuantidadeEstoque(10);
        produto.setEstoqueMinimo(2);
    }

    @Test
    void deveCadastrarProduto() {

        ProdutoRequestDTO dto = new ProdutoRequestDTO(
                "Chocolate",
                new BigDecimal("5.00"),
                20,
                5
        );

        when(produtoRepository.save(any(Produto.class)))
                .thenAnswer(invocacao -> {
                    Produto produtoSalvo = invocacao.getArgument(0);
                    produtoSalvo.setId(10L);
                    return produtoSalvo;
                });

        Produto resultado = produtoService.cadastrar(dto);

        assertNotNull(resultado);
        assertEquals(10L, resultado.getId());
        assertEquals("Chocolate", resultado.getNome());
        assertEquals(new BigDecimal("5.00"), resultado.getPrecoVenda());
        assertEquals(20, resultado.getQuantidadeEstoque());
        assertEquals(5, resultado.getEstoqueMinimo());

        verify(produtoRepository).save(any(Produto.class));
    }

    @Test
    void deveListarProdutos() {

        Produto outroProduto = new Produto();
        outroProduto.setId(2L);
        outroProduto.setNome("Biscoito");

        when(produtoRepository.findAll())
                .thenReturn(List.of(produto, outroProduto));

        List<Produto> resultado = produtoService.listar();

        assertEquals(2, resultado.size());
        assertEquals("Refrigerante", resultado.get(0).getNome());
        assertEquals("Biscoito", resultado.get(1).getNome());

        verify(produtoRepository).findAll();
    }

    @Test
    void deveBuscarProdutoPorId() {

        when(produtoRepository.findById(1L))
                .thenReturn(Optional.of(produto));

        Produto resultado = produtoService.buscarPorId(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("Refrigerante", resultado.getNome());

        verify(produtoRepository).findById(1L);
    }

    @Test
    void deveLancarExcecaoAoBuscarProdutoInexistente() {

        when(produtoRepository.findById(999L))
                .thenReturn(Optional.empty());

        ProdutoNaoEncontradoException erro = assertThrows(
                ProdutoNaoEncontradoException.class,
                () -> produtoService.buscarPorId(999L)
        );

        assertEquals("Produto não encontrado.", erro.getMessage());

        verify(produtoRepository).findById(999L);
    }

    @Test
    void deveAtualizarProdutoSemAlterarQuantidadeEstoque() {

        when(produtoRepository.findById(1L))
                .thenReturn(Optional.of(produto));

        when(produtoRepository.save(any(Produto.class)))
                .thenAnswer(invocacao -> invocacao.getArgument(0));

        ProdutoAtualizacaoDTO dto = new ProdutoAtualizacaoDTO(
                "Refrigerante Zero",
                new BigDecimal("9.50"),
                3
        );

        Produto resultado = produtoService.atualizar(1L, dto);

        assertEquals("Refrigerante Zero", resultado.getNome());
        assertEquals(new BigDecimal("9.50"), resultado.getPrecoVenda());
        assertEquals(3, resultado.getEstoqueMinimo());

        assertEquals(10, resultado.getQuantidadeEstoque());

        verify(produtoRepository).findById(1L);
        verify(produtoRepository).save(produto);
    }

    @Test
    void naoDeveAtualizarProdutoInexistente() {

        when(produtoRepository.findById(999L))
                .thenReturn(Optional.empty());

        ProdutoAtualizacaoDTO dto = new ProdutoAtualizacaoDTO(
                "Produto",
                new BigDecimal("10.00"),
                2
        );

        ProdutoNaoEncontradoException erro = assertThrows(
                ProdutoNaoEncontradoException.class,
                () -> produtoService.atualizar(999L, dto)
        );

        assertEquals("Produto não encontrado.", erro.getMessage());

        verify(produtoRepository).findById(999L);
        verify(produtoRepository, never()).save(any(Produto.class));
    }
}
