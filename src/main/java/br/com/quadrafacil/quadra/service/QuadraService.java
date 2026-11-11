package br.com.quadrafacil.quadra.service;

import br.com.quadrafacil.quadra.dto.*;
import br.com.quadrafacil.quadra.entity.Quadra;
import br.com.quadrafacil.quadra.repository.QuadraRepository;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional(readOnly = true)
public class QuadraService {
    private final QuadraRepository repository;
    public QuadraService(QuadraRepository repository) { this.repository = repository; }

    public List<QuadraResponse> listar() {
        return repository.findAll(Sort.by("nome").ascending().and(Sort.by("id")))
            .stream().map(QuadraResponse::de).toList();
    }
    public QuadraResponse consultar(UUID id) { return QuadraResponse.de(encontrar(id)); }

    @Transactional
    public QuadraResponse criar(QuadraRequest request) {
        return QuadraResponse.de(repository.save(new Quadra(request.nome(), request.modalidade(), request.precoHora(), true)));
    }
    @Transactional
    public QuadraResponse editar(UUID id, QuadraRequest request) {
        var quadra = encontrar(id);
        quadra.atualizar(request.nome(), request.modalidade(), request.precoHora());
        return QuadraResponse.de(quadra);
    }
    @Transactional
    public QuadraResponse alterarSituacao(UUID id, boolean ativa) {
        var quadra = encontrar(id);
        quadra.alterarSituacao(ativa);
        return QuadraResponse.de(quadra);
    }
    private Quadra encontrar(UUID id) {
        return repository.findById(id).orElseThrow(() ->
            new ResponseStatusException(HttpStatus.NOT_FOUND, "Quadra não encontrada."));
    }
}
