package br.com.quadrafacil.quadra.service;

import br.com.quadrafacil.quadra.dto.QuadraExemploResponse;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;

/** Somente demonstração. B04 substituirá a lista fixa por consulta ao repository. */
@Service
public class QuadraExemploService {
    public List<QuadraExemploResponse> listar() {
        return List.of(new QuadraExemploResponse(
            UUID.fromString("00000000-0000-0000-0000-000000000001"),
            UUID.fromString("00000000-0000-0000-0000-000000000002"),
            "Quadra Society — exemplo", "Futebol", new BigDecimal("120.00"), true));
    }
}
