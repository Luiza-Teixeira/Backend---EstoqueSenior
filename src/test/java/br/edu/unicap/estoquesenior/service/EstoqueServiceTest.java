package br.edu.unicap.estoquesenior.service;

import br.edu.unicap.estoquesenior.dto.EstoqueResponseDTO;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EstoqueServiceTest {

    @Mock
    private ProdutoRepository produtoRepository;

    @InjectMocks
    private EstoqueService estoqueService;

    private Produto produtoNormal;
    private Produto produtoEstoqueBaixo;

    @BeforeEach
    void setup() {
        produtoNormal = new Produto();
        produtoNormal.setId(1L);
        produtoNormal.setNome("Arroz 5kg");
        produtoNormal.setPrecoVenda(new BigDecimal("25.00"));
        produtoNormal.setQuantidadeEstoque(20);
        produtoNormal.setEstoqueMinimo(5);

        produtoEstoqueBaixo = new Produto();
        produtoEstoqueBaixo.setId(2L);
        produtoEstoqueBaixo.setNome("Feijão 1kg");
        produtoEstoqueBaixo.setPrecoVenda(new BigDecimal("8.00"));
        produtoEstoqueBaixo.setQuantidadeEstoque(3);
        produtoEstoqueBaixo.setEstoqueMinimo(5);
    }

    @Test
    void deveConsultarEstoqueGeralComSucesso() {
        when(produtoRepository.findAll()).thenReturn(List.of(produtoNormal, produtoEstoqueBaixo));

        List<EstoqueResponseDTO> resultado = estoqueService.consultarEstoqueGeral();

        assertEquals(2, resultado.size());
        assertEquals("Arroz 5kg", resultado.get(0).nomeProduto());
        assertFalse(resultado.get(0).estoqueBaixo());

        assertEquals("Feijão 1kg", resultado.get(1).nomeProduto());
        assertTrue(resultado.get(1).estoqueBaixo());

        verify(produtoRepository, times(1)).findAll();
    }

    @Test
    void deveListarApenasProdutosComEstoqueBaixo() {
        when(produtoRepository.findAll()).thenReturn(List.of(produtoNormal, produtoEstoqueBaixo));

        List<EstoqueResponseDTO> resultado = estoqueService.listarProdutosComEstoqueBaixo();

        assertEquals(1, resultado.size());
        assertEquals("Feijão 1kg", resultado.get(0).nomeProduto());
        assertTrue(resultado.get(0).estoqueBaixo());

        verify(produtoRepository, times(1)).findAll();
    }

    @Test
    void deveRetornarListaVaziaQuandoNenhumProdutoEstiverComEstoqueBaixo() {
        when(produtoRepository.findAll()).thenReturn(List.of(produtoNormal));

        List<EstoqueResponseDTO> resultado = estoqueService.listarProdutosComEstoqueBaixo();

        assertTrue(resultado.isEmpty());

        verify(produtoRepository, times(1)).findAll();
    }
}