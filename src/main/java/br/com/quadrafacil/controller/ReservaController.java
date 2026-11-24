package br.com.quadrafacil.controller;

import br.com.quadrafacil.dto.ReservaRequest;
import br.com.quadrafacil.dto.ReservaResponse;
import br.com.quadrafacil.service.ReservaService;
import jakarta.validation.Valid;
import java.net.URI;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reservas")
public class ReservaController {
    private final ReservaService reservas;

    public ReservaController(ReservaService reservas) {
        this.reservas = reservas;
    }

    @PostMapping
    public ResponseEntity<ReservaResponse> criar(@Valid @RequestBody ReservaRequest pedido) {
        var reserva = reservas.criar(pedido);
        return ResponseEntity.created(URI.create("/api/reservas/" + reserva.id())).body(reserva);
    }

    @GetMapping("/{id}")
    public ReservaResponse consultar(@PathVariable Long id) {
        return reservas.consultar(id);
    }

    @PostMapping("/{id}/cancelamento")
    public ReservaResponse cancelar(@PathVariable Long id) {
        return reservas.cancelar(id);
    }
}
