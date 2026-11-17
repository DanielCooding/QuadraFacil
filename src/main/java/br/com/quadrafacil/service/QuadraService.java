package br.com.quadrafacil.service;

import br.com.quadrafacil.dto.*;
import br.com.quadrafacil.entity.Quadra;
import br.com.quadrafacil.repository.QuadraRepository;
import java.util.List;
import java.util.ArrayList;
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
        List<QuadraResponse> resposta = new ArrayList<>();
        for (Quadra item : repository.findAll(Sort.by("nome", "id"))) {
            resposta.add(QuadraResponse.de(item));
        }
        return resposta;
    }
    public QuadraResponse consultar(Long id) { return QuadraResponse.de(encontrar(id)); }

    @Transactional
    public QuadraResponse criar(QuadraRequest request) {
        return QuadraResponse.de(repository.save(new Quadra(request.nome(), request.modalidade(), request.precoHora(), true)));
    }
    @Transactional
    public QuadraResponse editar(Long id, QuadraRequest request) {
        Quadra quadra = encontrar(id);
        quadra.atualizar(request.nome(), request.modalidade(), request.precoHora());
        return QuadraResponse.de(quadra);
    }
    @Transactional
    public QuadraResponse alterarSituacao(Long id, boolean ativa) {
        Quadra quadra = encontrar(id);
        quadra.alterarSituacao(ativa);
        return QuadraResponse.de(quadra);
    }
    private Quadra encontrar(Long id) {
        return repository.findById(id).orElseThrow(() ->
            new ResponseStatusException(HttpStatus.NOT_FOUND, "Quadra não encontrada."));
    }
}
