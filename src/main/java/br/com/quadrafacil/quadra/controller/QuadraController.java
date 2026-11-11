package br.com.quadrafacil.quadra.controller;

import br.com.quadrafacil.quadra.dto.*;
import br.com.quadrafacil.quadra.service.QuadraService;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/quadras")
public class QuadraController {
    private final QuadraService service;
    public QuadraController(QuadraService service) { this.service = service; }

    @GetMapping public List<QuadraResponse> listar() { return service.listar(); }
    @GetMapping("/{id}") public QuadraResponse consultar(@PathVariable UUID id) { return service.consultar(id); }
    @PostMapping public ResponseEntity<QuadraResponse> criar(@Valid @RequestBody QuadraRequest request) {
        var quadra = service.criar(request);
        return ResponseEntity.created(URI.create("/api/quadras/" + quadra.id())).body(quadra);
    }
    @PutMapping("/{id}") public QuadraResponse editar(@PathVariable UUID id, @Valid @RequestBody QuadraRequest request) {
        return service.editar(id, request);
    }
    @PatchMapping("/{id}/situacao") public QuadraResponse situacao(@PathVariable UUID id, @Valid @RequestBody SituacaoQuadraRequest request) {
        return service.alterarSituacao(id, request.ativa());
    }
}
