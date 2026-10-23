package br.edu.unicap.estoquesenior.controller;

import br.edu.unicap.estoquesenior.dto.EstoqueResponseDTO;
import br.edu.unicap.estoquesenior.service.EstoqueService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/estoque")
@RequiredArgsConstructor
public class EstoqueController {

    private final EstoqueService estoqueService;

    // GET /estoque -> consulta a situação dos produtos
    @GetMapping
    public ResponseEntity<List<EstoqueResponseDTO>> consultarEstoqueGeral() {
        return ResponseEntity.ok(estoqueService.consultarEstoqueGeral());
    }

    // GET /estoque/baixo -> lista produtos com quantidadeEstoque <= estoqueMinimo
    @GetMapping("/baixo")
    public ResponseEntity<List<EstoqueResponseDTO>> listarEstoqueBaixo() {
        return ResponseEntity.ok(estoqueService.listarProdutosComEstoqueBaixo());
    }
}