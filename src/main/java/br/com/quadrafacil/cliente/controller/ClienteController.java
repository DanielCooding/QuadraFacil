package br.com.quadrafacil.cliente.controller;

import br.com.quadrafacil.cliente.dto.*;
import br.com.quadrafacil.cliente.service.ClienteService;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/clientes")
public class ClienteController {
    private final ClienteService service;
    public ClienteController(ClienteService service) { this.service = service; }

    @PostMapping public ResponseEntity<ClienteResponse> criar(@Valid @RequestBody ClienteRequest request) {
        var cliente = service.criar(request);
        return ResponseEntity.created(URI.create("/api/clientes/" + cliente.id())).body(cliente);
    }
    @GetMapping public List<ClienteResponse> listar() { return service.listar(); }
    @GetMapping("/{id}") public ClienteResponse consultar(@PathVariable UUID id) { return service.consultar(id); }
}
