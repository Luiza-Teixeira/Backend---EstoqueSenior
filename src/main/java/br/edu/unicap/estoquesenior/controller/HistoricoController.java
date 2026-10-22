package br.edu.unicap.estoquesenior.controller;

import br.edu.unicap.estoquesenior.dto.HistoricoResponseDTO;
import br.edu.unicap.estoquesenior.service.HistoricoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/historico")
@RequiredArgsConstructor
public class HistoricoController {

    private final HistoricoService historicoService;

    @GetMapping
    public ResponseEntity<List<HistoricoResponseDTO>> listar() {
        return ResponseEntity.ok(historicoService.listar());
    }
}