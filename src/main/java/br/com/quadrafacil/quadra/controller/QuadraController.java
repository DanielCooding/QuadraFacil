package br.com.quadrafacil.quadra.controller;

import br.com.quadrafacil.quadra.dto.QuadraExemploResponse;
import br.com.quadrafacil.quadra.service.QuadraExemploService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class QuadraController {
    private final QuadraExemploService service;
    public QuadraController(QuadraExemploService service) { this.service = service; }

    @GetMapping("/api/quadras")
    public List<QuadraExemploResponse> listar() { return service.listar(); }
}
