package br.com.quadrafacil.dto;

import br.com.quadrafacil.entity.EstadoReserva;
import br.com.quadrafacil.entity.Reserva;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

public record AgendaResponse(LocalDate data, String fuso, QuadraResponse quadra,
                             Janela funcionamento, List<HorarioReservado> reservas) {
    public record Janela(String abertura, String fechamento) {}

    public record HorarioReservado(Long id, Long clienteId, Instant inicio, Instant fim, EstadoReserva estado) {
        public static HorarioReservado de(Reserva reserva) {
            return new HorarioReservado(reserva.getId(), reserva.getCliente().getId(),
                reserva.getInicio(), reserva.getFim(), reserva.getEstado());
        }
    }
}
