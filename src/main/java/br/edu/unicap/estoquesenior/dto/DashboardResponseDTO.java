package br.edu.unicap.estoquesenior.dto;

import java.math.BigDecimal;

public class DashboardResponseDTO {
    private long totalProdutosCadastrados;
    private long quantidadeProdutosEstoqueBaixo;
    private long quantidadeVendasDia;
    private BigDecimal valorTotalVendidoDia;

    public DashboardResponseDTO(long totalProdutosCadastrados, long quantidadeProdutosEstoqueBaixo, 
                                long quantidadeVendasDia, BigDecimal valorTotalVendidoDia) {
        this.totalProdutosCadastrados = totalProdutosCadastrados;
        this.quantidadeProdutosEstoqueBaixo = quantidadeProdutosEstoqueBaixo;
        this.quantidadeVendasDia = quantidadeVendasDia;
        this.valorTotalVendidoDia = valorTotalVendidoDia;
    }

    public long getTotalProdutosCadastrados() { return totalProdutosCadastrados; }
    public long getQuantidadeProdutosEstoqueBaixo() { return quantidadeProdutosEstoqueBaixo; }
    public long getQuantidadeVendasDia() { return quantidadeVendasDia; }
    public BigDecimal getValorTotalVendidoDia() { return valorTotalVendidoDia; }
}