package br.edu.unicap.estoquesenior.service;

import br.edu.unicap.estoquesenior.dto.ProdutoAtualizacaoDTO;
import br.edu.unicap.estoquesenior.dto.ProdutoRequestDTO;
import br.edu.unicap.estoquesenior.exception.ProdutoNaoEncontradoException;
import br.edu.unicap.estoquesenior.model.Produto;
import br.edu.unicap.estoquesenior.repository.ProdutoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

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

    public Produto buscarPorId(Long id) {
        return produtoRepository.findById(id)
                .orElseThrow(() -> new ProdutoNaoEncontradoException(
                        "Produto não encontrado."
                ));
    }

    public Produto atualizar(Long id, ProdutoAtualizacaoDTO dto) {
        Produto produto = buscarPorId(id);

        produto.setNome(dto.nome());
        produto.setPrecoVenda(dto.precoVenda());
        produto.setEstoqueMinimo(dto.estoqueMinimo());

        return produtoRepository.save(produto);
    }
}
