package br.com.quadrafacil;

import br.com.quadrafacil.modelo.Quadra;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ApresentacaoController {
    @GetMapping("/api/status")
    public Map<String, String> status() {
        return Map.of("projeto", "QuadraFácil", "status", "em execução", "etapa", "entrega inicial");
    }

    @GetMapping("/api/quadras")
    public List<Quadra> quadras() {
        // Exemplo fixo para demonstrar a API. Não há banco de dados nesta etapa.
        return List.of(new Quadra(
            UUID.fromString("00000000-0000-0000-0000-000000000001"),
            UUID.fromString("00000000-0000-0000-0000-000000000002"),
            "Quadra Society — exemplo", "Futebol", new BigDecimal("120.00"), true));
    }
}
