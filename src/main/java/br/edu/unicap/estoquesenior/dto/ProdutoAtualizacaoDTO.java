package br.edu.unicap.estoquesenior.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public record ProdutoAtualizacaoDTO(

        @NotBlank
        String nome,

        @NotNull
        @Positive
        BigDecimal precoVenda,

        @NotNull
        @PositiveOrZero
        Integer estoqueMinimo

) {
}
