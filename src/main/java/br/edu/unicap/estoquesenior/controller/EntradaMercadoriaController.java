
package br.edu.unicap.estoquesenior.controller;

import br.edu.unicap.estoquesenior.dto.EntradaMercadoriaRequestDTO;
import br.edu.unicap.estoquesenior.model.EntradaMercadoria;
import br.edu.unicap.estoquesenior.service.EntradaMercadoriaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/entradas-mercadoria")
@RequiredArgsConstructor
public class EntradaMercadoriaController {

    private final EntradaMercadoriaService entradaService;

    @PostMapping
    public ResponseEntity<EntradaMercadoria> registrar(
            @Valid @RequestBody EntradaMercadoriaRequestDTO dto) {

        EntradaMercadoria entrada = entradaService.registrar(dto);

        URI location = URI.create(
                "/entradas-mercadoria/" + entrada.getId()
        );

        return ResponseEntity.created(location).body(entrada);
    }

    @GetMapping
    public ResponseEntity<List<EntradaMercadoria>> listar() {
        return ResponseEntity.ok(entradaService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntradaMercadoria> buscarPorId(
            @PathVariable Long id) {

        return entradaService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
