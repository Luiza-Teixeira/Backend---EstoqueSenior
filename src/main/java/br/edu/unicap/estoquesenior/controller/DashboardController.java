package br.edu.unicap.estoquesenior.controller;

import br.edu.unicap.estoquesenior.dto.DashboardResponseDTO;
import br.edu.unicap.estoquesenior.service.DashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/resumo")
    public ResponseEntity<DashboardResponseDTO> obterResumo() {
        DashboardResponseDTO resumo = dashboardService.obterResumo();
        return ResponseEntity.ok(resumo);
    }
}