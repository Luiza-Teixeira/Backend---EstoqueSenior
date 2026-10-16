package br.edu.unicap.estoquesenior.controller;

import br.edu.unicap.estoquesenior.dto.ProdutoAtualizacaoDTO;
import br.edu.unicap.estoquesenior.dto.ProdutoRequestDTO;
import br.edu.unicap.estoquesenior.model.Produto;
import br.edu.unicap.estoquesenior.service.ProdutoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/produtos")
@RequiredArgsConstructor
public class ProdutoController {

    private final ProdutoService produtoService;

    @PostMapping
    public ResponseEntity<Produto> cadastrar(
            @Valid @RequestBody ProdutoRequestDTO dto) {

        Produto produto = produtoService.cadastrar(dto);

        URI location = URI.create("/produtos/" + produto.getId());

        return ResponseEntity.created(location).body(produto);
    }

    @GetMapping
    public ResponseEntity<List<Produto>> listar() {
        return ResponseEntity.ok(produtoService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Produto> buscarPorId(
            @PathVariable Long id) {

        return ResponseEntity.ok(produtoService.buscarPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Produto> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody ProdutoAtualizacaoDTO dto) {

        return ResponseEntity.ok(
                produtoService.atualizar(id, dto)
        );
    }
}
