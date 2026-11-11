package br.com.quadrafacil.quadra.dto;

import java.math.BigDecimal;
import java.util.UUID;

/** Contrato legado do exemplo inicial; proprietarioId será retirado em B04. */
public record QuadraExemploResponse(UUID id, UUID proprietarioId, String nome,
                                   String modalidade, BigDecimal precoHora, boolean ativa) {}
