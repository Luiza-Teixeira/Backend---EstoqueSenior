package br.edu.unicap.estoquesenior.service;

import br.edu.unicap.estoquesenior.dto.DashboardResponseDTO;
import br.edu.unicap.estoquesenior.model.Venda;
import br.edu.unicap.estoquesenior.repository.ProdutoRepository;
import br.edu.unicap.estoquesenior.repository.VendaRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

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

        // 1. Total de produtos cadastrados
        long totalProdutos = produtoRepository.count();

        // 2. Produtos com estoque baixo
        long estoqueBaixo = produtoRepository.findAll().stream()
                .filter(p -> p.getQuantidadeEstoque() <= p.getEstoqueMinimo())
                .count();

        // 3. Busca todas as vendas e filtra as do dia de hoje UMA ÚNICA VEZ
        List<Venda> vendasDoDia = vendaRepository.findAll().stream()
                .filter(v -> v.getDataHora() != null && v.getDataHora().toLocalDate().isEqual(hoje))
                .toList();

        // Quantidade de vendas no dia
        long vendasDia = vendasDoDia.size();

        // Valor total vendido no dia usando a mesma lista
        BigDecimal valorTotalDia = vendasDoDia.stream()
                .map(v -> v.getValorUnitario().multiply(BigDecimal.valueOf(v.getQuantidade())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new DashboardResponseDTO(totalProdutos, estoqueBaixo, vendasDia, valorTotalDia);
    }
}