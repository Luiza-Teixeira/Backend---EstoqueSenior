package br.edu.unicap.estoquesenior.service;

import br.edu.unicap.estoquesenior.dto.EntradaMercadoriaRequestDTO;
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

        when(produtoRepository.save(any(Produto.class)))
                .thenAnswer(invocacao -> invocacao.getArgument(0));

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

        assertThrows(
                IllegalArgumentException.class,
                () -> entradaService.registrar(dto)
        );

        verifyNoInteractions(produtoRepository, entradaRepository);
    }

    @Test
    void naoDeveRegistrarEntradaComQuantidadeNegativa() {
        EntradaMercadoriaRequestDTO dto =
                new EntradaMercadoriaRequestDTO(1L, -3);

        assertThrows(
                IllegalArgumentException.class,
                () -> entradaService.registrar(dto)
        );

        verifyNoInteractions(produtoRepository, entradaRepository);
    }

    @Test
    void naoDeveRegistrarEntradaDeProdutoInexistente() {
        when(produtoRepository.findById(999L))
                .thenReturn(Optional.empty());

        EntradaMercadoriaRequestDTO dto =
                new EntradaMercadoriaRequestDTO(999L, 5);

        assertThrows(
                IllegalArgumentException.class,
                () -> entradaService.registrar(dto)
        );

        verify(entradaRepository, never())
                .save(any(EntradaMercadoria.class));
    
}
}