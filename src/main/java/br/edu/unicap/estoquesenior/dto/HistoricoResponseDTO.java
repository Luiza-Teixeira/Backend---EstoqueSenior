package br.edu.unicap.estoquesenior.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record HistoricoResponseDTO(
        String tipo,
        Long produtoId,
        String nomeProduto,
        Integer quantidade,
        BigDecimal valorUnitario,
        LocalDateTime dataHora
) {
}