package br.com.quadrafacil;

import br.com.quadrafacil.cliente.dto.ClienteRequest;
import br.com.quadrafacil.quadra.dto.QuadraRequest;
import br.com.quadrafacil.reserva.dto.ReservaRequest;
import jakarta.validation.Validation;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ContratosDtoTest {
    @Test void validaCamposObrigatoriosEmailEValorDecimal() {
        try (var factory = Validation.buildDefaultValidatorFactory()) {
            var validator = factory.getValidator();
            assertTrue(validator.validate(new ClienteRequest("João", "joao@example.com")).isEmpty());
            assertFalse(validator.validate(new ClienteRequest(" ", "email-invalido")).isEmpty());
            assertTrue(validator.validate(new QuadraRequest("Central", "Futebol", new BigDecimal("120.00"))).isEmpty());
            assertFalse(validator.validate(new QuadraRequest("Central", "Futebol", BigDecimal.ZERO)).isEmpty());
            assertFalse(validator.validate(new QuadraRequest("Central", "Futebol", new BigDecimal("1.001"))).isEmpty());
            assertEquals(4, validator.validate(new ReservaRequest(null, null, null, null)).size());
        }
    }
}
