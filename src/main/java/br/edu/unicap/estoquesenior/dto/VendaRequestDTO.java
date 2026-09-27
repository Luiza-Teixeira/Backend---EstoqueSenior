package br.edu.unicap.estoquesenior.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record VendaRequestDTO(

        @NotNull
        @Positive
        Long produtoId,

        @NotNull
        @Positive
        Integer quantidade

) {
}
