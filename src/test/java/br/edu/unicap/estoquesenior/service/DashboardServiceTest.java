package br.edu.unicap.estoquesenior.service;

import br.edu.unicap.estoquesenior.dto.DashboardResponseDTO;
import br.edu.unicap.estoquesenior.model.Produto;
import br.edu.unicap.estoquesenior.model.Venda;
import br.edu.unicap.estoquesenior.repository.ProdutoRepository;
import br.edu.unicap.estoquesenior.repository.VendaRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class DashboardServiceTest {

    @Mock
    private ProdutoRepository produtoRepository;

    @Mock
    private VendaRepository vendaRepository;

    @InjectMocks
    private DashboardService dashboardService;

    @Test
    void deveRetornarResumoDoDashboardComSucesso() {
        // 1. Criar dados simulados (Mocks) para os produtos
        Produto produto1 = new Produto();
        produto1.setQuantidadeEstoque(2); // Estoque baixo (menor ou igual ao mínimo)
        produto1.setEstoqueMinimo(5);

        Produto produto2 = new Produto();
        produto2.setQuantidadeEstoque(10); // Estoque normal
        produto2.setEstoqueMinimo(5);

        // 2. Criar dados simulados para a venda de hoje
        Venda vendaHoje = new Venda();
        vendaHoje.setDataHora(LocalDateTime.now());
        vendaHoje.setQuantidade(2);
        vendaHoje.setValorUnitario(new BigDecimal("50.00"));

        // 3. Configurar o comportamento dos repositórios simulados
        Mockito.when(produtoRepository.count()).thenReturn(2L);
        Mockito.when(produtoRepository.findAll()).thenReturn(List.of(produto1, produto2));
        Mockito.when(vendaRepository.findAll()).thenReturn(List.of(vendaHoje));

        // 4. Executar o método do service que estamos a testar
        DashboardResponseDTO resumo = dashboardService.obterResumo();

        // 5. Verificar se os resultados batem certo com o esperado
        Assertions.assertEquals(2L, resumo.getTotalProdutosCadastrados());
        Assertions.assertEquals(1L, resumo.getQuantidadeProdutosEstoqueBaixo()); // Apenas o produto1 tem estoque baixo
        Assertions.assertEquals(1L, resumo.getQuantidadeVendasDia());
        Assertions.assertEquals(new BigDecimal("100.00"), resumo.getValorTotalVendidoDia()); // 2 * 50.00
    }
}