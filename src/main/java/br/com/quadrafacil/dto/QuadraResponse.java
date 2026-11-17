package br.com.quadrafacil.dto;

import br.com.quadrafacil.entity.Quadra;
import java.math.BigDecimal;

public record QuadraResponse(Long id, String nome, String modalidade, BigDecimal precoHora, boolean ativa) {
    public static QuadraResponse de(Quadra quadra) {
        return new QuadraResponse(quadra.getId(), quadra.getNome(), quadra.getModalidade(), quadra.getPrecoHora(), quadra.isAtiva());
    }
}
