package br.com.quadrafacil.dto;

import br.com.quadrafacil.entity.EstadoReserva;
import br.com.quadrafacil.entity.Reserva;
import java.math.BigDecimal;
import java.time.Instant;

public record ReservaResponse(Long id, Long clienteId, Long quadraId, Instant inicio, Instant fim,
                              BigDecimal valorTotal, EstadoReserva estado) {
    public static ReservaResponse de(Reserva reserva) {
        return new ReservaResponse(reserva.getId(), reserva.getCliente().getId(), reserva.getQuadra().getId(),
            reserva.getInicio(), reserva.getFim(), reserva.getValorTotal(), reserva.getEstado());
    }
}
