package br.com.quadrafacil.modelo;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record Reserva(UUID id, UUID clienteId, UUID quadraId, Instant inicio,
                      Instant fim, BigDecimal valorTotal, Estado estado) {
    public enum Estado { CONFIRMADA, CANCELADA }
}
