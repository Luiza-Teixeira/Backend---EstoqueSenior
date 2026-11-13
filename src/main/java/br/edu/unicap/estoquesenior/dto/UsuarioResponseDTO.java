package br.edu.unicap.estoquesenior.dto;

public record UsuarioResponseDTO(
        Long id,
        String nome,
        String email
) {
}