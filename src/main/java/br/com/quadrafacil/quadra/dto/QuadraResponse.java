package br.com.quadrafacil.quadra.dto;

import br.com.quadrafacil.quadra.entity.Quadra;
import java.math.BigDecimal;
import java.util.UUID;

public record QuadraResponse(UUID id, String nome, String modalidade, BigDecimal precoHora, boolean ativa) {
    public static QuadraResponse de(Quadra quadra) {
        return new QuadraResponse(quadra.getId(), quadra.getNome(), quadra.getModalidade(), quadra.getPrecoHora(), quadra.isAtiva());
    }
}
