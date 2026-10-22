package br.edu.unicap.estoquesenior.service;
import java.util.List;
import java.util.Optional;
import br.edu.unicap.estoquesenior.exception.EstoqueInsuficienteException;
import br.edu.unicap.estoquesenior.exception.ProdutoNaoEncontradoException;
import br.edu.unicap.estoquesenior.exception.QuantidadeVendaInvalidaException;
import br.edu.unicap.estoquesenior.model.Produto;
import br.edu.unicap.estoquesenior.model.Venda;
import br.edu.unicap.estoquesenior.repository.ProdutoRepository;
import br.edu.unicap.estoquesenior.repository.VendaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
public class VendaService {

    private final ProdutoRepository produtoRepository;
    private final VendaRepository vendaRepository;

    public VendaService(
            ProdutoRepository produtoRepository,
            VendaRepository vendaRepository) {

        this.produtoRepository = produtoRepository;
        this.vendaRepository = vendaRepository;
    }

    public Venda registrarVenda(Long produtoId, Integer quantidade) {

        if (quantidade == null || quantidade <= 0) {
            throw new QuantidadeVendaInvalidaException(
                    "A quantidade da venda deve ser maior que zero."
            );
        }

        Produto produto = produtoRepository.findById(produtoId)
                .orElseThrow(() -> new ProdutoNaoEncontradoException(
                        "Produto não encontrado."
                ));

        if (quantidade > produto.getQuantidadeEstoque()) {
            throw new EstoqueInsuficienteException(
                    "Estoque insuficiente."
            );
        }

        produto.setQuantidadeEstoque(
                produto.getQuantidadeEstoque() - quantidade
        );

        produtoRepository.save(produto);

        Venda venda = new Venda();
        venda.setProduto(produto);
        venda.setQuantidade(quantidade);
        venda.setValorUnitario(produto.getPrecoVenda());
        venda.setDataHora(LocalDateTime.now());

        return vendaRepository.save(venda);
    }
    public List<Venda> listar() {
        return vendaRepository.findAll();
    }

    public Optional<Venda> buscarPorId(Long id) {
        return vendaRepository.findById(id);
    }
}
