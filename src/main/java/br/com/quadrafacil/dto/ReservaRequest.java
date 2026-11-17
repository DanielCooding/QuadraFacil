package br.com.quadrafacil.dto;

import java.time.OffsetDateTime;
import jakarta.validation.constraints.NotNull;

public record ReservaRequest(@NotNull Long clienteId, @NotNull Long quadraId,
                             @NotNull OffsetDateTime inicio, @NotNull OffsetDateTime fim) {}
