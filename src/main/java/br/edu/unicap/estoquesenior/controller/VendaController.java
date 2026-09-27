package br.edu.unicap.estoquesenior.controller;

import br.edu.unicap.estoquesenior.dto.VendaRequestDTO;
import br.edu.unicap.estoquesenior.model.Venda;
import br.edu.unicap.estoquesenior.service.VendaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping("/vendas")
@RequiredArgsConstructor
public class VendaController {

    private final VendaService vendaService;

    @PostMapping
    public ResponseEntity<Venda> registrar(@Valid @RequestBody VendaRequestDTO dto) {
        Venda venda = vendaService.registrarVenda(dto.produtoId(), dto.quantidade());

        URI location = URI.create("/vendas/" + venda.getId());

        return ResponseEntity.created(location).body(venda);
    }
}
