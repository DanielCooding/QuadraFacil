package br.com.quadrafacil.quadra.dto;

import jakarta.validation.constraints.NotNull;

public record SituacaoQuadraRequest(@NotNull Boolean ativa) {}
