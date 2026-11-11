package br.com.quadrafacil.quadra.controller;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.http.converter.HttpMessageNotReadableException;

/** Tratamento local do módulo. A padronização dos demais módulos continua em B07. */
@RestControllerAdvice(assignableTypes = QuadraController.class)
public class QuadraExceptionHandler {
    public record Campo(String campo, String mensagem) {}
    public record Erro(int status, String mensagem, List<Campo> campos) {}

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Erro> validacao(MethodArgumentNotValidException ex) {
        var campos = ex.getBindingResult().getFieldErrors().stream()
            .map(e -> new Campo(e.getField(), "Confira o preenchimento e o formato deste campo." )).toList();
        return ResponseEntity.badRequest().body(new Erro(400, "Confira os campos informados.", campos));
    }
    @ExceptionHandler({HttpMessageNotReadableException.class, MethodArgumentTypeMismatchException.class})
    public ResponseEntity<Erro> formato(Exception ex) {
        return ResponseEntity.badRequest().body(new Erro(400, "JSON, campos ou identificador inválidos.", List.of()));
    }
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Erro> ausente(ResponseStatusException ex) {
        return ResponseEntity.status(ex.getStatusCode()).body(new Erro(ex.getStatusCode().value(), ex.getReason(), List.of()));
    }
}
