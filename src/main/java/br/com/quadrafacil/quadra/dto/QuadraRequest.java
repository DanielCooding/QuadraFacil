package br.com.quadrafacil.quadra.dto;

import java.math.BigDecimal;
import jakarta.validation.constraints.*;

public record QuadraRequest(
    @NotBlank(message = "O nome é obrigatório.") @Size(max = 100, message = "O nome deve ter até 100 caracteres.") String nome,
    @NotBlank(message = "A modalidade é obrigatória.") @Size(max = 40, message = "A modalidade deve ter até 40 caracteres.") String modalidade,
    @NotNull(message = "O preço é obrigatório.") @DecimalMin(value = "0.01", message = "O preço deve ser maior que zero.")
    @Digits(integer = 10, fraction = 2, message = "Use até 10 dígitos inteiros e duas casas decimais no preço.") BigDecimal precoHora) {
    public QuadraRequest {
        nome = nome == null ? null : nome.strip();
        modalidade = modalidade == null ? null : modalidade.strip();
    }
}
