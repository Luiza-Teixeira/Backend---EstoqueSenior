package br.edu.unicap.estoquesenior.service;

import br.edu.unicap.estoquesenior.dto.EntradaMercadoriaRequestDTO;
import br.edu.unicap.estoquesenior.exception.LimiteEstoqueExcedidoException;
import br.edu.unicap.estoquesenior.exception.ProdutoNaoEncontradoException;
import br.edu.unicap.estoquesenior.exception.QuantidadeEntradaInvalidaException;
import br.edu.unicap.estoquesenior.model.EntradaMercadoria;
import br.edu.unicap.estoquesenior.model.Produto;
import br.edu.unicap.estoquesenior.repository.EntradaMercadoriaRepository;
import br.edu.unicap.estoquesenior.repository.ProdutoRepository;

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
class EntradaMercadoriaServiceTest {

    @Mock
    private EntradaMercadoriaRepository entradaRepository;

    @Mock
    private ProdutoRepository produtoRepository;

    @InjectMocks
    private EntradaMercadoriaService entradaService;

    private Produto produto;

    @BeforeEach
    void configurar() {
        produto = new Produto();
        produto.setId(1L);
        produto.setNome("Refrigerante");
        produto.setPrecoVenda(new BigDecimal("5.00"));
        produto.setQuantidadeEstoque(10);
        produto.setEstoqueMinimo(2);
    }

    @Test
    void deveRegistrarEntradaEAtualizarEstoque() {

        when(produtoRepository.findById(1L))
                .thenReturn(Optional.of(produto));

        when(entradaRepository.save(any(EntradaMercadoria.class)))
                .thenAnswer(invocacao -> {
                    EntradaMercadoria entrada = invocacao.getArgument(0);
                    entrada.setId(100L);
                    return entrada;
                });

        EntradaMercadoriaRequestDTO dto =
                new EntradaMercadoriaRequestDTO(1L, 5);

        EntradaMercadoria resultado = entradaService.registrar(dto);

        assertEquals(15, produto.getQuantidadeEstoque());
        assertEquals(100L, resultado.getId());
        assertEquals(5, resultado.getQuantidade());
        assertEquals(1L, resultado.getProduto().getId());
        assertNotNull(resultado.getDataHora());

        verify(produtoRepository).save(produto);
        verify(entradaRepository).save(any(EntradaMercadoria.class));
    }

    @Test
    void naoDeveRegistrarEntradaComQuantidadeZero() {

        EntradaMercadoriaRequestDTO dto =
                new EntradaMercadoriaRequestDTO(1L, 0);

        QuantidadeEntradaInvalidaException erro = assertThrows(
                QuantidadeEntradaInvalidaException.class,
                () -> entradaService.registrar(dto)
        );

        assertEquals(
                "A quantidade da entrada deve ser maior que zero.",
                erro.getMessage()
        );

        verifyNoInteractions(produtoRepository, entradaRepository);
    }

    @Test
    void naoDeveRegistrarEntradaComQuantidadeNegativa() {

        EntradaMercadoriaRequestDTO dto =
                new EntradaMercadoriaRequestDTO(1L, -3);

        QuantidadeEntradaInvalidaException erro = assertThrows(
                QuantidadeEntradaInvalidaException.class,
                () -> entradaService.registrar(dto)
        );

        assertEquals(
                "A quantidade da entrada deve ser maior que zero.",
                erro.getMessage()
        );

        verifyNoInteractions(produtoRepository, entradaRepository);
    }

    @Test
    void naoDeveRegistrarEntradaDeProdutoInexistente() {

        when(produtoRepository.findById(999L))
                .thenReturn(Optional.empty());

        EntradaMercadoriaRequestDTO dto =
                new EntradaMercadoriaRequestDTO(999L, 5);

        ProdutoNaoEncontradoException erro = assertThrows(
                ProdutoNaoEncontradoException.class,
                () -> entradaService.registrar(dto)
        );

        assertEquals("Produto não encontrado.", erro.getMessage());

        verify(produtoRepository).findById(999L);
        verify(entradaRepository, never())
                .save(any(EntradaMercadoria.class));
    }

    @Test
    void naoDeveRegistrarEntradaQuandoUltrapassarLimiteDoEstoque() {

        produto.setQuantidadeEstoque(Integer.MAX_VALUE);

        when(produtoRepository.findById(1L))
                .thenReturn(Optional.of(produto));

        EntradaMercadoriaRequestDTO dto =
                new EntradaMercadoriaRequestDTO(1L, 1);

        LimiteEstoqueExcedidoException erro = assertThrows(
                LimiteEstoqueExcedidoException.class,
                () -> entradaService.registrar(dto)
        );

        assertEquals(
                "A quantidade total excede o limite permitido.",
                erro.getMessage()
        );

        assertEquals(
                Integer.MAX_VALUE,
                produto.getQuantidadeEstoque()
        );

        verify(produtoRepository, never()).save(any(Produto.class));
        verify(entradaRepository, never())
                .save(any(EntradaMercadoria.class));
    }
}
