package br.edu.unicap.estoquesenior.service;

import br.edu.unicap.estoquesenior.dto.HistoricoResponseDTO;
import br.edu.unicap.estoquesenior.model.EntradaMercadoria;
import br.edu.unicap.estoquesenior.model.Produto;
import br.edu.unicap.estoquesenior.model.Venda;
import br.edu.unicap.estoquesenior.repository.EntradaMercadoriaRepository;
import br.edu.unicap.estoquesenior.repository.VendaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

@Service
public class HistoricoService {

    private final VendaRepository vendaRepository;
    private final EntradaMercadoriaRepository entradaMercadoriaRepository;

    public HistoricoService(
            VendaRepository vendaRepository,
            EntradaMercadoriaRepository entradaMercadoriaRepository) {
        this.vendaRepository = vendaRepository;
        this.entradaMercadoriaRepository = entradaMercadoriaRepository;
    }

    @Transactional(readOnly = true)
    public List<HistoricoResponseDTO> listar() {
        Stream<HistoricoResponseDTO> entradas = entradaMercadoriaRepository.findAll().stream()
                .map(HistoricoService::converterEntrada);
        Stream<HistoricoResponseDTO> vendas = vendaRepository.findAll().stream()
                .map(HistoricoService::converterVenda);

        return Stream.concat(entradas, vendas)
                .sorted(Comparator.comparing(HistoricoResponseDTO::dataHora).reversed())
                .toList();
    }

    private static HistoricoResponseDTO converterEntrada(EntradaMercadoria entrada) {
        Produto produto = entrada.getProduto();
        return new HistoricoResponseDTO(
                "ENTRADA",
                produto.getId(),
                produto.getNome(),
                entrada.getQuantidade(),
                entrada.getCustoUnitario(),
                entrada.getDataHora()
        );
    }

    private static HistoricoResponseDTO converterVenda(Venda venda) {
        Produto produto = venda.getProduto();
        return new HistoricoResponseDTO(
                "SAIDA",
                produto.getId(),
                produto.getNome(),
                venda.getQuantidade(),
                venda.getValorUnitario(),
                venda.getDataHora()
        );
    }
}