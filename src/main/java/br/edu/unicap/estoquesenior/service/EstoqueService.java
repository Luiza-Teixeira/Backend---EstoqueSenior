package br.edu.unicap.estoquesenior.service;

import br.edu.unicap.estoquesenior.dto.EstoqueResponseDTO;
import br.edu.unicap.estoquesenior.model.Produto;
import br.edu.unicap.estoquesenior.repository.ProdutoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EstoqueService {

    private final ProdutoRepository produtoRepository; 

    @Transactional(readOnly = true)
    public List<EstoqueResponseDTO> consultarEstoqueGeral() {
        return produtoRepository.findAll().stream()
                .map(this::mapToDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<EstoqueResponseDTO> listarProdutosComEstoqueBaixo() {
        return produtoRepository.findAll().stream()
                .filter(p -> p.getQuantidadeEstoque() <= p.getEstoqueMinimo())
                .map(this::mapToDTO)
                .toList();
    }

    private EstoqueResponseDTO mapToDTO(Produto produto) {
        boolean estoqueBaixo = produto.getQuantidadeEstoque() <= produto.getEstoqueMinimo();
        return new EstoqueResponseDTO(
                produto.getId(),
                produto.getNome(),
                produto.getQuantidadeEstoque(),
                produto.getEstoqueMinimo(),
                estoqueBaixo);
    }
}
