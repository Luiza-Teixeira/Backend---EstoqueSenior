package br.edu.unicap.estoquesenior.dto;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class VendaRequestDTOTest {

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void deveAceitarRequestValida() {
        VendaRequestDTO request = new VendaRequestDTO(1L, 2);

        assertTrue(validator.validate(request).isEmpty());
    }

    @Test
    void naoDeveAceitarProdutoIdNulo() {
        VendaRequestDTO request = new VendaRequestDTO(null, 2);

        assertFalse(validator.validate(request).isEmpty());
    }

    @Test
    void naoDeveAceitarQuantidadeZero() {
        VendaRequestDTO request = new VendaRequestDTO(1L, 0);

        assertFalse(validator.validate(request).isEmpty());
    }
}
