package br.com.quadrafacil.modelo;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.UUID;

public record HorarioFuncionamento(UUID quadraId, DayOfWeek diaSemana,
                                  LocalTime abertura, LocalTime fechamento) {}
