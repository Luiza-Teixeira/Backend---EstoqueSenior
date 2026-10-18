package br.edu.unicap.estoquesenior.service;

import br.edu.unicap.estoquesenior.dto.DashboardResponseDTO;
import br.edu.unicap.estoquesenior.model.Produto;
import br.edu.unicap.estoquesenior.model.Venda;
import br.edu.unicap.estoquesenior.repository.ProdutoRepository;
import br.edu.unicap.estoquesenior.repository.VendaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DashboardServiceTest {

    @Mock
    private ProdutoRepository produtoRepository;

    @Mock
    private VendaRepository vendaRepository;

    @InjectMocks
    private DashboardService dashboardService;

    @Test
    @DisplayName("Deve calcular o resumo do dashboard corretamente para vendas do dia de hoje")
    void deveCalcularResumoComSucesso() {
        // Arrange
        Produto p1 = new Produto();
        p1.setQuantidadeEstoque(5);
        p1.setEstoqueMinimo(10); // Estoque baixo

        Produto p2 = new Produto();
        p2.setQuantidadeEstoque(20);
        p2.setEstoqueMinimo(10); // OK

        when(produtoRepository.count()).thenReturn(2L);
        when(produtoRepository.findAll()).thenReturn(List.of(p1, p2));

        Venda v1 = new Venda();
        v1.setDataHora(LocalDateTime.now());
        v1.setQuantidade(2);
        v1.setValorUnitario(new BigDecimal("50.00"));

        when(vendaRepository.findAll()).thenReturn(List.of(v1));

        // Act
        DashboardResponseDTO dto = dashboardService.obterResumo();

        // Assert
        assertEquals(2L, dto.getTotalProdutosCadastrados());
        assertEquals(1L, dto.getQuantidadeProdutosEstoqueBaixo());
        assertEquals(1L, dto.getQuantidadeVendasDia());
        assertEquals(new BigDecimal("100.00"), dto.getValorTotalVendidoDia());
    }

    @Test
    @DisplayName("Não deve incluir vendas de outros dias no resumo de hoje")
    void naoDeveIncluirVendasDeOutrosDiasNoResumo() {
        // Arrange
        when(produtoRepository.count()).thenReturn(0L);
        when(produtoRepository.findAll()).thenReturn(List.of());

        // Venda de ontem
        Venda vendaOntem = new Venda();
        vendaOntem.setDataHora(LocalDateTime.now().minusDays(1));
        vendaOntem.setQuantidade(5);
        vendaOntem.setValorUnitario(new BigDecimal("100.00"));

        // Venda de hoje
        Venda vendaHoje = new Venda();
        vendaHoje.setDataHora(LocalDateTime.now());
        vendaHoje.setQuantidade(1);
        vendaHoje.setValorUnitario(new BigDecimal("30.00"));

        when(vendaRepository.findAll()).thenReturn(List.of(vendaOntem, vendaHoje));

        // Act
        DashboardResponseDTO dto = dashboardService.obterResumo();

        // Assert
        assertEquals(1L, dto.getQuantidadeVendasDia(), "Deveria contabilizar apenas 1 venda (a de hoje)");
        assertEquals(new BigDecimal("30.00"), dto.getValorTotalVendidoDia(), "Valor total do dia deve ignorar a venda de ontem");
    }
}