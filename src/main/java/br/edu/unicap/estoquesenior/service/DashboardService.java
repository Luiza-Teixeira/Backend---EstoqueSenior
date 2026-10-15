package br.edu.unicap.estoquesenior.service;

import br.edu.unicap.estoquesenior.dto.DashboardResponseDTO;
import br.edu.unicap.estoquesenior.model.Produto;
import br.edu.unicap.estoquesenior.model.Venda;
import br.edu.unicap.estoquesenior.repository.ProdutoRepository;
import br.edu.unicap.estoquesenior.repository.VendaRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
public class DashboardService {

    private final ProdutoRepository produtoRepository;
    private final VendaRepository vendaRepository;

    public DashboardService(ProdutoRepository produtoRepository, VendaRepository vendaRepository) {
        this.produtoRepository = produtoRepository;
        this.vendaRepository = vendaRepository;
    }

    public DashboardResponseDTO obterResumo() {
        LocalDate hoje = LocalDate.now();

                            // 1. Total de produtos cadastrados (usa o count() padrão do repositório)
        long totalProdutos = produtoRepository.count();

                            // 2. Produtos com estoque baixo (busca todos e filtra em memória onde a quantidade é menor ou igual ao mínimo)
        long estoqueBaixo = produtoRepository.findAll().stream()
                .filter(p -> p.getQuantidadeEstoque() <= p.getEstoqueMinimo())
                .count();

                            // 3. Quantidade de vendas do dia (busca todas e filtra pelas vendas cuja dataHora é do dia de hoje)
        long vendasDia = vendaRepository.findAll().stream()
                .filter(v -> v.getDataHora().toLocalDate().isEqual(hoje))
                .count();

                            // 4. Valor total vendido no dia (calcula multiplicando quantidade x valorUnitario de cada venda de hoje)
        BigDecimal valorTotalDia = vendaRepository.findAll().stream()
                .filter(v -> v.getDataHora().toLocalDate().isEqual(hoje))
                .map(v -> v.getValorUnitario().multiply(BigDecimal.valueOf(v.getQuantidade())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new DashboardResponseDTO(totalProdutos, estoqueBaixo, vendasDia, valorTotalDia);
    }
}