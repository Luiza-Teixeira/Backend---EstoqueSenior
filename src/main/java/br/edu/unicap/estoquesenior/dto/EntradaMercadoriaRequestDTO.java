
package br.edu.unicap.estoquesenior.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

public record EntradaMercadoriaRequestDTO(

        @NotNull(message = "O ID do produto é obrigatório.")
        @Positive(message = "O ID do produto deve ser maior que zero.")
        Long produtoId,

        @NotNull(message = "A quantidade é obrigatória.")
        @Positive(message = "A quantidade deve ser maior que zero.")
        Integer quantidade,

        @NotNull(message = "O custo unitário é obrigatório")
        @Positive(message = "O custo unitário deve ser maior que zero")
        BigDecimal custoUnitario
) {
}
