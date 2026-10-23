package br.edu.unicap.estoquesenior.dto;

public record EstoqueResponseDTO(
                Long produtoId,
                String nomeProduto,
                Integer quantidadeEstoque,
                Integer estoqueMinimo,
                boolean estoqueBaixo) {
}