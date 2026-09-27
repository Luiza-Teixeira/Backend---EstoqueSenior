package br.edu.unicap.estoquesenior.controller;

import br.edu.unicap.estoquesenior.model.Venda;
import br.edu.unicap.estoquesenior.service.VendaService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class VendaControllerTest {

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private VendaService vendaService;

    @InjectMocks
    private VendaController vendaController;

    @BeforeEach
    void setUp() {
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        mockMvc = MockMvcBuilders.standaloneSetup(vendaController)
                .setValidator(validator)
                .build();
    }

    @Test
    void deveRegistrarVendaComSucesso() throws Exception {
        Venda venda = new Venda();
        venda.setId(10L);
        venda.setQuantidade(2);
        venda.setValorUnitario(new BigDecimal("25.50"));
        venda.setDataHora(LocalDateTime.of(2026, 9, 27, 10, 30));

        when(vendaService.registrarVenda(1L, 2)).thenReturn(venda);

        java.util.Map<String, Object> payload = new java.util.HashMap<>();
        payload.put("produtoId", 1L);
        payload.put("quantidade", 2);

        mockMvc.perform(post("/vendas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/vendas/10"))
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.quantidade").value(2));

        verify(vendaService).registrarVenda(1L, 2);
    }

    @Test
    void naoDeveRegistrarVendaComProdutoIdInvalido() throws Exception {
        mockMvc.perform(post("/vendas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"produtoId\":0,\"quantidade\":2}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void naoDeveRegistrarVendaComQuantidadeInvalida() throws Exception {
        mockMvc.perform(post("/vendas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"produtoId\":1,\"quantidade\":0}"))
                .andExpect(status().isBadRequest());
    }
}
