package br.edu.unicap.estoquesenior.service;

import br.edu.unicap.estoquesenior.dto.HistoricoResponseDTO;
import br.edu.unicap.estoquesenior.model.EntradaMercadoria;
import br.edu.unicap.estoquesenior.model.Produto;
import br.edu.unicap.estoquesenior.model.Venda;
import br.edu.unicap.estoquesenior.repository.EntradaMercadoriaRepository;
import br.edu.unicap.estoquesenior.repository.VendaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HistoricoServiceTest {

    @Mock
    private VendaRepository vendaRepository;

    @Mock
    private EntradaMercadoriaRepository entradaMercadoriaRepository;

    @InjectMocks
    private HistoricoService historicoService;

    @Test
    void deveConverterEOrdenarEntradasEVendasDaMaisRecenteParaMaisAntiga() {
        Produto produto = new Produto();
        produto.setId(7L);
        produto.setNome("Fralda Geriatrica");

        EntradaMercadoria entrada = new EntradaMercadoria();
        entrada.setProduto(produto);
        entrada.setQuantidade(4);
        entrada.setCustoUnitario(new BigDecimal("12.50"));
        entrada.setDataHora(LocalDateTime.of(2026, 10, 17, 9, 0));

        Venda venda = new Venda();
        venda.setProduto(produto);
        venda.setQuantidade(2);
        venda.setValorUnitario(new BigDecimal("20.00"));
        venda.setDataHora(LocalDateTime.of(2026, 10, 18, 10, 0));

        when(entradaMercadoriaRepository.findAll()).thenReturn(List.of(entrada));
        when(vendaRepository.findAll()).thenReturn(List.of(venda));

        List<HistoricoResponseDTO> resultado = historicoService.listar();

        assertEquals(2, resultado.size());
        assertEquals("SAIDA", resultado.get(0).tipo());
        assertEquals(7L, resultado.get(0).produtoId());
        assertEquals("Fralda Geriatrica", resultado.get(0).nomeProduto());
        assertEquals(2, resultado.get(0).quantidade());
        assertEquals(new BigDecimal("20.00"), resultado.get(0).valorUnitario());
        assertEquals(venda.getDataHora(), resultado.get(0).dataHora());

        assertEquals("ENTRADA", resultado.get(1).tipo());
        assertEquals(4, resultado.get(1).quantidade());
        assertEquals(new BigDecimal("12.50"), resultado.get(1).valorUnitario());
        assertEquals(entrada.getDataHora(), resultado.get(1).dataHora());
        verify(entradaMercadoriaRepository).findAll();
        verify(vendaRepository).findAll();
    }

    @Test
    void deveRetornarListaVaziaQuandoNaoHouverMovimentacoes() {
        when(entradaMercadoriaRepository.findAll()).thenReturn(List.of());
        when(vendaRepository.findAll()).thenReturn(List.of());

        List<HistoricoResponseDTO> resultado = historicoService.listar();

        assertEquals(List.of(), resultado);
    }
}