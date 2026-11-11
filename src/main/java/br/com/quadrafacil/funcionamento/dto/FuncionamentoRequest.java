package br.com.quadrafacil.funcionamento.dto;

import java.time.DayOfWeek;
import java.time.LocalTime;
import jakarta.validation.constraints.NotNull;

public record FuncionamentoRequest(@NotNull DayOfWeek diaSemana,
                                   @NotNull LocalTime abertura, @NotNull LocalTime fechamento) {}
