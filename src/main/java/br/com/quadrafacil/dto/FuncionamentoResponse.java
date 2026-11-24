package br.com.quadrafacil.dto;

import br.com.quadrafacil.entity.HorarioFuncionamento;
import java.time.DayOfWeek;
import java.time.format.DateTimeFormatter;

public record FuncionamentoResponse(DayOfWeek diaSemana, String abertura, String fechamento) {
    private static final DateTimeFormatter HORA = DateTimeFormatter.ofPattern("HH:mm");

    public static FuncionamentoResponse de(HorarioFuncionamento horario) {
        return new FuncionamentoResponse(horario.getDiaSemana(),
            horario.getAbertura().format(HORA), horario.getFechamento().format(HORA));
    }
}
