package br.com.quadrafacil.cliente.service;

import br.com.quadrafacil.cliente.dto.*;
import br.com.quadrafacil.cliente.entity.Cliente;
import br.com.quadrafacil.cliente.repository.ClienteRepository;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional(readOnly = true)
public class ClienteService {
    private final ClienteRepository repository;
    public ClienteService(ClienteRepository repository) { this.repository = repository; }

    @Transactional
    public ClienteResponse criar(ClienteRequest request) {
        return ClienteResponse.de(repository.save(new Cliente(request.nome(), request.email())));
    }
    public List<ClienteResponse> listar() {
        return repository.findAll(Sort.by("nome").and(Sort.by("id")))
            .stream().map(ClienteResponse::de).toList();
    }
    public ClienteResponse consultar(UUID id) {
        return ClienteResponse.de(repository.findById(id).orElseThrow(() ->
            new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente não encontrado.")));
    }
}
