package br.com.quadrafacil.service;

import br.com.quadrafacil.dto.*;
import br.com.quadrafacil.entity.Cliente;
import br.com.quadrafacil.repository.ClienteRepository;
import java.util.List;
import java.util.ArrayList;
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
        List<ClienteResponse> resposta = new ArrayList<>();
        for (Cliente item : repository.findAll(Sort.by("nome", "id"))) {
            resposta.add(ClienteResponse.de(item));
        }
        return resposta;
    }
    public ClienteResponse consultar(Long id) {
        return ClienteResponse.de(repository.findById(id).orElseThrow(() ->
            new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente não encontrado.")));
    }
}
