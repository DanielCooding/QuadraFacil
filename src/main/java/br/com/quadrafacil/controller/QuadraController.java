package br.com.quadrafacil.controller;

import br.com.quadrafacil.dto.*;
import br.com.quadrafacil.service.QuadraService;
import br.com.quadrafacil.service.FuncionamentoService;
import br.com.quadrafacil.service.ReservaService;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/quadras")
public class QuadraController {
    private final QuadraService service;
    private final FuncionamentoService funcionamento;

    private final ReservaService reservas;

    public QuadraController(QuadraService service, FuncionamentoService funcionamento, ReservaService reservas) {
        this.service = service;
        this.funcionamento = funcionamento;
        this.reservas = reservas;
    }

    @GetMapping("/{id}/agenda")
    public AgendaResponse agenda(@PathVariable Long id,
            @RequestParam @DateTimeFormat(pattern = "uuuu-MM-dd") LocalDate data) {
        return reservas.agenda(id, data);
    }

    @GetMapping("/{id}/funcionamento")
    public List<FuncionamentoResponse> consultarFuncionamento(@PathVariable Long id) {
        return funcionamento.consultar(id);
    }

    @PutMapping("/{id}/funcionamento")
    public List<FuncionamentoResponse> substituirFuncionamento(@PathVariable Long id,
            @RequestBody List<FuncionamentoRequest> semana) {
        return funcionamento.substituir(id, semana);
    }

    @GetMapping public List<QuadraResponse> listar() { return service.listar(); }
    @GetMapping("/{id}") public QuadraResponse consultar(@PathVariable Long id) { return service.consultar(id); }
    @PostMapping public ResponseEntity<QuadraResponse> criar(@Valid @RequestBody QuadraRequest request) {
        var quadra = service.criar(request);
        return ResponseEntity.created(URI.create("/api/quadras/" + quadra.id())).body(quadra);
    }
    @PutMapping("/{id}") public QuadraResponse editar(@PathVariable Long id, @Valid @RequestBody QuadraRequest request) {
        return service.editar(id, request);
    }
    @PatchMapping("/{id}/situacao") public QuadraResponse situacao(@PathVariable Long id, @Valid @RequestBody SituacaoQuadraRequest request) {
        return service.alterarSituacao(id, request.ativa());
    }
}
