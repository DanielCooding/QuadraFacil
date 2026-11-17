package br.com.quadrafacil.dto;

import jakarta.validation.constraints.NotNull;

public record SituacaoQuadraRequest(@NotNull(message = "Informe se a quadra está ativa.") Boolean ativa) {}
