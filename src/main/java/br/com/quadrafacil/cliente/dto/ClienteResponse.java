package br.com.quadrafacil.cliente.dto;

import br.com.quadrafacil.cliente.entity.Cliente;
import java.util.UUID;

public record ClienteResponse(UUID id, String nome, String email) {
    public static ClienteResponse de(Cliente cliente) {
        return new ClienteResponse(cliente.getId(), cliente.getNome(), cliente.getEmail());
    }
}
