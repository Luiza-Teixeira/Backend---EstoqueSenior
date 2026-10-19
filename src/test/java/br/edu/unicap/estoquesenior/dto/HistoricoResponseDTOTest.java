package br.edu.unicap.estoquesenior.dto;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HistoricoResponseDTOTest {

    @Test
    void deveArmazenarEDisponibilizarOsDadosDaMovimentacao() {
        LocalDateTime dataHora = LocalDateTime.of(2026, 10, 18, 10, 30);
        HistoricoResponseDTO dto = new HistoricoResponseDTO(
                "ENTRADA", 7L, "Fralda Geriatrica", 4,
                new BigDecimal("12.50"), dataHora
        );

        assertEquals("ENTRADA", dto.tipo());
        assertEquals(7L, dto.produtoId());
        assertEquals("Fralda Geriatrica", dto.nomeProduto());
        assertEquals(4, dto.quantidade());
        assertEquals(new BigDecimal("12.50"), dto.valorUnitario());
        assertEquals(dataHora, dto.dataHora());
    }
}