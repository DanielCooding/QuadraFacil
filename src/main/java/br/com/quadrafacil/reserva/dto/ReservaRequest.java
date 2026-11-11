package br.com.quadrafacil.reserva.dto;

import java.time.OffsetDateTime;
import java.util.UUID;
import jakarta.validation.constraints.NotNull;

public record ReservaRequest(@NotNull UUID clienteId, @NotNull UUID quadraId,
                             @NotNull OffsetDateTime inicio, @NotNull OffsetDateTime fim) {}
