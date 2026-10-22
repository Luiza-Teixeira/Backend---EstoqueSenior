package br.edu.unicap.estoquesenior.controller;

import br.edu.unicap.estoquesenior.dto.HistoricoResponseDTO;
import br.edu.unicap.estoquesenior.service.HistoricoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class HistoricoControllerTest {

    @Mock
    private HistoricoService historicoService;

    private MockMvc mockMvc;

    @BeforeEach
    void configurar() {
        mockMvc = MockMvcBuilders.standaloneSetup(
                new HistoricoController(historicoService)
        ).build();
    }

    @Test
    void deveConsultarHistoricoERetornarRespostaDoService() throws Exception {
        HistoricoResponseDTO dto = new HistoricoResponseDTO(
                "SAIDA", 7L, "Fralda Geriatrica", 2,
                new BigDecimal("20.00"), LocalDateTime.of(2026, 10, 18, 10, 0)
        );
        when(historicoService.listar()).thenReturn(List.of(dto));

        mockMvc.perform(get("/historico"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].tipo").value("SAIDA"))
                .andExpect(jsonPath("$[0].produtoId").value(7))
                .andExpect(jsonPath("$[0].nomeProduto").value("Fralda Geriatrica"))
                .andExpect(jsonPath("$[0].quantidade").value(2))
                .andExpect(jsonPath("$[0].valorUnitario").value(20.00));

        verify(historicoService).listar();
    }
}