package br.com.quadrafacil.modelo;

import java.math.BigDecimal;
import java.util.UUID;

public record Quadra(UUID id, UUID proprietarioId, String nome, String modalidade,
                     BigDecimal precoHora, boolean ativa) {}
