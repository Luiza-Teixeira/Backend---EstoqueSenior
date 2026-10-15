package br.edu.unicap.estoquesenior.service;

import br.edu.unicap.estoquesenior.exception.EstoqueInsuficienteException;
import br.edu.unicap.estoquesenior.exception.ProdutoNaoEncontradoException;
import br.edu.unicap.estoquesenior.exception.QuantidadeVendaInvalidaException;
import br.edu.unicap.estoquesenior.model.Produto;
import br.edu.unicap.estoquesenior.model.Venda;
import br.edu.unicap.estoquesenior.repository.ProdutoRepository;
import br.edu.unicap.estoquesenior.repository.VendaRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VendaServiceTest {

    @Mock
    private ProdutoRepository produtoRepository;

    @Mock
    private VendaRepository vendaRepository;

    @InjectMocks
    private VendaService vendaService;

    private Produto produto;

    @BeforeEach
    void configurar() {
        produto = new Produto();
        produto.setId(1L);
        produto.setNome("Coca-Cola 2L");
        produto.setPrecoVenda(new BigDecimal("10.00"));
        produto.setQuantidadeEstoque(10);
        produto.setEstoqueMinimo(2);
    }

    @Test
    void deveRegistrarVendaComSucesso() {

        when(produtoRepository.findById(1L))
                .thenReturn(Optional.of(produto));

        when(vendaRepository.save(any(Venda.class)))
                .thenAnswer(invocacao -> {
                    Venda venda = invocacao.getArgument(0);
                    venda.setId(100L);
                    return venda;
                });

        Venda venda = vendaService.registrarVenda(1L, 2);

        assertNotNull(venda);
        assertEquals(100L, venda.getId());
        assertEquals(2, venda.getQuantidade());
        assertEquals(new BigDecimal("10.00"), venda.getValorUnitario());
        assertEquals(8, produto.getQuantidadeEstoque());
        assertNotNull(venda.getDataHora());

        verify(produtoRepository).save(produto);
        verify(vendaRepository).save(any(Venda.class));
    }

    @Test
    void naoDeveRegistrarVendaComEstoqueInsuficiente() {

        produto.setQuantidadeEstoque(3);

        when(produtoRepository.findById(1L))
                .thenReturn(Optional.of(produto));

        EstoqueInsuficienteException erro = assertThrows(
                EstoqueInsuficienteException.class,
                () -> vendaService.registrarVenda(1L, 5)
        );

        assertEquals("Estoque insuficiente.", erro.getMessage());
        assertEquals(3, produto.getQuantidadeEstoque());

        verify(produtoRepository, never()).save(any(Produto.class));
        verify(vendaRepository, never()).save(any(Venda.class));
    }

    @Test
    void naoDeveRegistrarVendaComQuantidadeZero() {

        QuantidadeVendaInvalidaException erro = assertThrows(
                QuantidadeVendaInvalidaException.class,
                () -> vendaService.registrarVenda(1L, 0)
        );

        assertEquals(
                "A quantidade da venda deve ser maior que zero.",
                erro.getMessage()
        );

        verifyNoInteractions(produtoRepository, vendaRepository);
    }

    @Test
    void naoDeveRegistrarVendaComQuantidadeNegativa() {

        QuantidadeVendaInvalidaException erro = assertThrows(
                QuantidadeVendaInvalidaException.class,
                () -> vendaService.registrarVenda(1L, -1)
        );

        assertEquals(
                "A quantidade da venda deve ser maior que zero.",
                erro.getMessage()
        );

        verifyNoInteractions(produtoRepository, vendaRepository);
    }

    @Test
    void naoDeveRegistrarVendaDeProdutoInexistente() {

        when(produtoRepository.findById(999L))
                .thenReturn(Optional.empty());

        ProdutoNaoEncontradoException erro = assertThrows(
                ProdutoNaoEncontradoException.class,
                () -> vendaService.registrarVenda(999L, 2)
        );

        assertEquals("Produto não encontrado.", erro.getMessage());

        verify(produtoRepository).findById(999L);
        verify(vendaRepository, never()).save(any(Venda.class));
    }
}
