package br.edu.unicap.estoquesenior.service;

import br.edu.unicap.estoquesenior.dto.ProdutoRequestDTO;
import br.edu.unicap.estoquesenior.model.Produto;
import br.edu.unicap.estoquesenior.repository.ProdutoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProdutoService {

    private final ProdutoRepository produtoRepository;

    public Produto cadastrar(ProdutoRequestDTO dto) {
        Produto produto = new Produto();

        produto.setNome(dto.nome());
        produto.setPrecoVenda(dto.precoVenda());
        produto.setQuantidadeEstoque(dto.quantidadeEstoque());
        produto.setEstoqueMinimo(dto.estoqueMinimo());

        return produtoRepository.save(produto);
    }

    public List<Produto> listar() {
        return produtoRepository.findAll();
    }

    public Optional<Produto> buscarPorId(Long id) {
        return produtoRepository.findById(id);
    }
}