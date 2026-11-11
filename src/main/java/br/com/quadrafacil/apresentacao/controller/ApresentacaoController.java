package br.com.quadrafacil.apresentacao.controller;

import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ApresentacaoController {
    @GetMapping("/api/status")
    public Map<String, String> status() {
        return Map.of("projeto", "QuadraFácil", "status", "em execução", "etapa", "entrega inicial");
    }
}
