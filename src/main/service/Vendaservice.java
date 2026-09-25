package br.edu.unicap.estoquesenior.service;

import br.edu.unicap.estoquesenior.model.Produto;
import br.edu.unicap.estoquesenior.model.Venda;
import br.edu.unicap.estoquesenior.repository.ProdutoRepository;
import br.edu.unicap.estoquesenior.repository.VendaRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
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

        // Verifica se a quantidade informada é válida
        if (quantidade == null || quantidade <= 0) {
            throw new IllegalArgumentException(
                    "A quantidade da venda deve ser maior que zero."
            );
        }

        // Busca o produto pelo ID
        Produto produto = produtoRepository.findById(produtoId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Produto não encontrado."
                ));

        // Verifica se existe estoque suficiente
        if (quantidade > produto.getQuantidadeEstoque()) {
            throw new IllegalArgumentException(
                    "Estoque insuficiente."
            );
        }

        // Diminui a quantidade do estoque
        produto.setQuantidadeEstoque(
                produto.getQuantidadeEstoque() - quantidade
        );

        // Salva o estoque atualizado
        produtoRepository.save(produto);

        // Cria a venda
        Venda venda = new Venda();
        venda.setProduto(produto);
        venda.setQuantidade(quantidade);
        venda.setValorUnitario(produto.getPrecoVenda());
        venda.setDataHora(LocalDateTime.now());

        // Salva a venda
        return vendaRepository.save(venda);
    }
}