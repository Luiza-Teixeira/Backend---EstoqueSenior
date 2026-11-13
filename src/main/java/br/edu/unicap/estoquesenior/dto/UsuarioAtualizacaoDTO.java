package br.edu.unicap.estoquesenior.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UsuarioAtualizacaoDTO(

        @NotBlank
        String nome,

        @NotBlank
        @Email
        String email
) {
}