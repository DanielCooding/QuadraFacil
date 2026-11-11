package br.com.quadrafacil.quadra.dto;

import java.math.BigDecimal;
import jakarta.validation.constraints.*;

public record QuadraRequest(
    @NotBlank @Size(max = 100) String nome,
    @NotBlank @Size(max = 40) String modalidade,
    @NotNull @DecimalMin("0.01") @Digits(integer = 10, fraction = 2) BigDecimal precoHora) {
    public QuadraRequest {
        nome = nome == null ? null : nome.strip();
        modalidade = modalidade == null ? null : modalidade.strip();
    }
}
