package br.com.quadrafacil.compartilhado.erro;

import java.util.List;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.*;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.http.converter.HttpMessageNotReadableException;

/** Contrato de erro compartilhado por todos os controllers presentes e futuros. */
@RestControllerAdvice
public class ApiExceptionHandler {
    public record Campo(String campo, String mensagem) {}
    public record Erro(int status, String mensagem, List<Campo> campos) {}

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Erro> validacao(MethodArgumentNotValidException ex) {
        var campos = ex.getBindingResult().getFieldErrors().stream()
            .map(e -> new Campo(e.getField(), e.getDefaultMessage()))
            .distinct().sorted(java.util.Comparator.comparing(Campo::campo).thenComparing(Campo::mensagem)).toList();
        return ResponseEntity.badRequest().body(new Erro(400, "Confira os campos informados.", campos));
    }
    @ExceptionHandler({HttpMessageNotReadableException.class, MethodArgumentTypeMismatchException.class})
    public ResponseEntity<Erro> formato(Exception ex) {
        return resposta(400, "JSON, campos ou identificador inválidos.");
    }
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Erro> negocio(ResponseStatusException ex) {
        int status = ex.getStatusCode().value();
        // Motivos de erro de negócio são textos controlados pelos Services, nunca SQL ou payloads.
        return resposta(status, status >= 500 ? "Não foi possível concluir a solicitação." :
            ex.getReason() == null ? "Não foi possível atender à solicitação." : ex.getReason());
    }
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Erro> integridade(DataIntegrityViolationException ex) {
        return resposta(409, "Os dados entram em conflito com um registro existente.");
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Erro> inesperado(Exception ex) {
        if (ex instanceof ErrorResponse http) {
            int status = http.getStatusCode().value();
            String mensagem = switch (status) {
                case 400 -> "Confira os parâmetros da solicitação.";
                case 404 -> "Recurso não encontrado.";
                case 405 -> "Método HTTP não permitido para este recurso.";
                case 406 -> "Formato de resposta não disponível.";
                case 415 -> "Tipo de conteúdo não suportado. Envie JSON.";
                default -> "Não foi possível atender à solicitação.";
            };
            return ResponseEntity.status(status).headers(http.getHeaders()).body(new Erro(status, mensagem, List.of()));
        }
        return resposta(500, "Não foi possível concluir a solicitação.");
    }
    private ResponseEntity<Erro> resposta(int status, String mensagem) {
        return ResponseEntity.status(status).body(new Erro(status, mensagem, List.of()));
    }
}
