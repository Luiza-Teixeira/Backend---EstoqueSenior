package br.edu.unicap.estoquesenior.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public record ProdutoRequestDTO(

        @NotBlank
        String nome,

        @NotNull
        @Positive
        BigDecimal precoVenda,

        @NotNull
        @PositiveOrZero
        Integer quantidadeEstoque,

        @NotNull
        @PositiveOrZero
        Integer estoqueMinimo

) {
}