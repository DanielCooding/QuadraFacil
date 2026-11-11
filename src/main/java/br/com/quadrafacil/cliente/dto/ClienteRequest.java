package br.com.quadrafacil.cliente.dto;

import jakarta.validation.constraints.*;

public record ClienteRequest(
    @NotBlank @Size(max = 100) String nome,
    @NotBlank @Email @Size(max = 254) String email) {}
