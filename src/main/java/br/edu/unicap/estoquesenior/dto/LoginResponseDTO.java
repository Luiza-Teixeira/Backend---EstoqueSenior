package br.edu.unicap.estoquesenior.dto;

public record LoginResponseDTO(
        String token,
        String tipo,
        UsuarioResponseDTO usuario
) {
}