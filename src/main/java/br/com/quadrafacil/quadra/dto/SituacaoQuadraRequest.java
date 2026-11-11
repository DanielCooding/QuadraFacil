package br.com.quadrafacil.quadra.dto;

import jakarta.validation.constraints.NotNull;

public record SituacaoQuadraRequest(@NotNull(message = "Informe se a quadra está ativa.") Boolean ativa) {}
