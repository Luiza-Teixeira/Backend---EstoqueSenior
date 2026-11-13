package br.edu.unicap.estoquesenior.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RedefinirSenhaRequestDTO(

        @NotBlank
        String token,

        @NotBlank
        @Size(min = 6)
        String novaSenha,

        @NotBlank
        String confirmacaoSenha
) {
}