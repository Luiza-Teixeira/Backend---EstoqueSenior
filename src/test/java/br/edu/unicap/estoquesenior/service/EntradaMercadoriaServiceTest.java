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
import org.mockito.ArgumentCaptor;
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
    private EntradaMercadoriaService entradaMercadoriaService;

    private Produto produto;

    @BeforeEach
    void setUp() {
        produto = new Produto();
        produto.setId(1L);
        produto.setQuantidadeEstoque(10);
    }

    @Test
    void deveRegistrarEntradaMercadoriaComSucessoEAtribuirCustoUnitario() {
        // Arrange
        BigDecimal custoUnitarioEsperado = new BigDecimal("25.50");
        EntradaMercadoriaRequestDTO dto = new EntradaMercadoriaRequestDTO(1L, 5, custoUnitarioEsperado);

        when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));
        when(entradaRepository.save(any(EntradaMercadoria.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        EntradaMercadoria resultado = entradaMercadoriaService.registrar(dto);

        // Assert
        assertNotNull(resultado);
        assertEquals(15, produto.getQuantidadeEstoque()); // Estoque atualizado de 10 para 15
        assertEquals(custoUnitarioEsperado, resultado.getCustoUnitario()); // Valida o custo unitário

        // Captura do objeto salvo para dupla verificação
        ArgumentCaptor<EntradaMercadoria> captor = ArgumentCaptor.forClass(EntradaMercadoria.class);
        verify(entradaRepository).save(captor.capture());
        EntradaMercadoria entradaSalva = captor.getValue();

        assertEquals(custoUnitarioEsperado, entradaSalva.getCustoUnitario());
        assertEquals(5, entradaSalva.getQuantidade());
        assertEquals(produto, entradaSalva.getProduto());

        verify(produtoRepository, times(1)).save(produto);
    }

    @Test
    void deveLancarExcecaoQuandoQuantidadeForInvalida() {
        // Arrange
        EntradaMercadoriaRequestDTO dto = new EntradaMercadoriaRequestDTO(1L, 0, new BigDecimal("10.00"));

        // Act & Assert
        QuantidadeEntradaInvalidaException exception = assertThrows(
                QuantidadeEntradaInvalidaException.class,
                () -> entradaMercadoriaService.registrar(dto)
        );

        assertEquals("A quantidade da entrada deve ser maior que zero.", exception.getMessage());
        verifyNoInteractions(produtoRepository);
        verifyNoInteractions(entradaRepository);
    }

    @Test
    void deveLancarExcecaoQuandoProdutoNaoForEncontrado() {
        // Arrange
        EntradaMercadoriaRequestDTO dto = new EntradaMercadoriaRequestDTO(99L, 5, new BigDecimal("10.00"));
        when(produtoRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        ProdutoNaoEncontradoException exception = assertThrows(
                ProdutoNaoEncontradoException.class,
                () -> entradaMercadoriaService.registrar(dto)
        );

        assertEquals("Produto não encontrado.", exception.getMessage());
        verify(entradaRepository, never()).save(any());
    }

    @Test
    void deveLancarExcecaoQuandoLimiteEstoqueExcedido() {
        // Arrange
        produto.setQuantidadeEstoque(Integer.MAX_VALUE);
        EntradaMercadoriaRequestDTO dto = new EntradaMercadoriaRequestDTO(1L, 1, new BigDecimal("10.00"));

        when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));

        // Act & Assert
        LimiteEstoqueExcedidoException exception = assertThrows(
                LimiteEstoqueExcedidoException.class,
                () -> entradaMercadoriaService.registrar(dto)
        );

        assertEquals("A quantidade total excede o limite permitido.", exception.getMessage());
        verify(entradaRepository, never()).save(any());
    }
}