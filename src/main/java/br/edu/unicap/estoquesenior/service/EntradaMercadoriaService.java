
package br.edu.unicap.estoquesenior.service;

import br.edu.unicap.estoquesenior.dto.EntradaMercadoriaRequestDTO;
import br.edu.unicap.estoquesenior.model.EntradaMercadoria;
import br.edu.unicap.estoquesenior.model.Produto;
import br.edu.unicap.estoquesenior.repository.EntradaMercadoriaRepository;
import br.edu.unicap.estoquesenior.repository.ProdutoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EntradaMercadoriaService {

    private final EntradaMercadoriaRepository entradaRepository;
    private final ProdutoRepository produtoRepository;

    @Transactional
    public EntradaMercadoria registrar(
            EntradaMercadoriaRequestDTO dto) {

        if (dto.quantidade() == null || dto.quantidade() <= 0) {
            throw new IllegalArgumentException(
                    "A quantidade da entrada deve ser maior que zero."
            );
        }

        Produto produto = produtoRepository.findById(dto.produtoId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Produto não encontrado."
                ));

        int estoqueAtual = produto.getQuantidadeEstoque();

        if ((long) estoqueAtual + dto.quantidade() > Integer.MAX_VALUE) {
            throw new IllegalArgumentException(
                    "A quantidade total excede o limite permitido."
            );
        }

        produto.setQuantidadeEstoque(
                estoqueAtual + dto.quantidade()
        );

        produtoRepository.save(produto);

        EntradaMercadoria entrada = new EntradaMercadoria();
        entrada.setProduto(produto);
        entrada.setQuantidade(dto.quantidade());
        entrada.setDataHora(LocalDateTime.now());

        return entradaRepository.save(entrada);
    }

    @Transactional(readOnly = true)
    public List<EntradaMercadoria> listar() {
        return entradaRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<EntradaMercadoria> buscarPorId(Long id) {
        return entradaRepository.findById(id);
    }
}
