package br.com.quadrafacil.dto;

import br.com.quadrafacil.entity.Cliente;

public record ClienteResponse(Long id, String nome, String email) {
    public static ClienteResponse de(Cliente cliente) {
        return new ClienteResponse(cliente.getId(), cliente.getNome(), cliente.getEmail());
    }
}
