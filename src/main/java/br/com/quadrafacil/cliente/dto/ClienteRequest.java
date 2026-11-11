package br.com.quadrafacil.cliente.dto;

import jakarta.validation.constraints.*;

public record ClienteRequest(
    @NotBlank(message = "O nome é obrigatório.")
    @Size(max = 100, message = "O nome deve ter até 100 caracteres.") String nome,
    @NotBlank(message = "O e-mail é obrigatório.")
    @Email(message = "Informe um e-mail válido.")
    @Size(max = 254, message = "O e-mail deve ter até 254 caracteres.") String email) {
    public ClienteRequest {
        nome = nome == null ? null : nome.strip();
        email = email == null ? null : email.strip();
    }
}
