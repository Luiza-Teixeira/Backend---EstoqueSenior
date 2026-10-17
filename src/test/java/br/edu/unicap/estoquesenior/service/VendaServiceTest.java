package br.edu.unicap.estoquesenior.service;

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
import java.util.List;
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
        produto.setNome("Fralda Geriátrica");
        produto.setPrecoVenda(new BigDecimal("45.00"));
        produto.setQuantidadeEstoque(10);
        produto.setEstoqueMinimo(2);
    }

    @Test
    void deveRegistrarVendaComSucesso() {
        when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));
        when(vendaRepository.save(any(Venda.class))).thenAnswer(invocacao -> {
            Venda venda = invocacao.getArgument(0);
            venda.setId(100L);
            return venda;
        });

        Venda venda = vendaService.registrarVenda(1L, 2);

        assertNotNull(venda);
        assertEquals(100L, venda.getId());
        assertEquals(2, venda.getQuantidade());
        assertEquals(new BigDecimal("45.00"), venda.getValorUnitario());
        assertEquals(8, produto.getQuantidadeEstoque());
        assertNotNull(venda.getDataHora());

        verify(produtoRepository).save(produto);
        verify(vendaRepository).save(any(Venda.class));
    }

    @Test
    void naoDeveRegistrarVendaComEstoqueInsuficiente() {
        produto.setQuantidadeEstoque(3);
        when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));

        IllegalArgumentException erro = assertThrows(
                IllegalArgumentException.class,
                () -> vendaService.registrarVenda(1L, 5)
        );

        assertEquals("Estoque insuficiente.", erro.getMessage());
        assertEquals(3, produto.getQuantidadeEstoque());

        verify(produtoRepository, never()).save(any(Produto.class));
        verify(vendaRepository, never()).save(any(Venda.class));
    }

    @Test
    void naoDeveRegistrarVendaComQuantidadeZero() {
        IllegalArgumentException erro = assertThrows(
                IllegalArgumentException.class,
                () -> vendaService.registrarVenda(1L, 0)
        );

        assertEquals("A quantidade da venda deve ser maior que zero.", erro.getMessage());
        verifyNoInteractions(produtoRepository, vendaRepository);
    }

    @Test
    void naoDeveRegistrarVendaComQuantidadeNegativa() {
        IllegalArgumentException erro = assertThrows(
                IllegalArgumentException.class,
                () -> vendaService.registrarVenda(1L, -1)
        );

        assertEquals("A quantidade da venda deve ser maior que zero.", erro.getMessage());
        verifyNoInteractions(produtoRepository, vendaRepository);
    }

    @Test
    void naoDeveRegistrarVendaDeProdutoInexistente() {
        when(produtoRepository.findById(999L)).thenReturn(Optional.empty());

        IllegalArgumentException erro = assertThrows(
                IllegalArgumentException.class,
                () -> vendaService.registrarVenda(999L, 2)
        );

        assertEquals("Produto não encontrado.", erro.getMessage());
        verify(produtoRepository).findById(999L);
        verify(vendaRepository, never()).save(any(Venda.class));
    }


    @Test
    void deveListarTodasAsVendas() {
        Venda venda1 = new Venda();
        Venda venda2 = new Venda();
        when(vendaRepository.findAll()).thenReturn(List.of(venda1, venda2));

        List<Venda> resultado = vendaService.listar();

        assertEquals(2, resultado.size());
        verify(vendaRepository, times(1)).findAll();
    }

    @Test
    void deveBuscarVendaPorIdComSucesso() {
        Venda venda = new Venda();
        venda.setId(1L);
        when(vendaRepository.findById(1L)).thenReturn(Optional.of(venda));

        Optional<Venda> resultado = vendaService.buscarPorId(1L);

        assertTrue(resultado.isPresent());
        assertEquals(1L, resultado.get().getId());
        verify(vendaRepository, times(1)).findById(1L);
    }
}
