package br.com.quadrafacil.service;

import br.com.quadrafacil.dto.FuncionamentoRequest;
import br.com.quadrafacil.dto.FuncionamentoResponse;
import br.com.quadrafacil.entity.EstadoReserva;
import br.com.quadrafacil.entity.HorarioFuncionamento;
import br.com.quadrafacil.entity.Quadra;
import br.com.quadrafacil.repository.HorarioFuncionamentoRepository;
import br.com.quadrafacil.repository.ReservaRepository;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional(readOnly = true)
public class FuncionamentoService {
    static final ZoneId BRASILIA = ZoneId.of("America/Sao_Paulo");
    private static final Pattern HORARIO_VALIDO = Pattern.compile("(?:[01][0-9]|2[0-3]):(?:00|30)");

    private final QuadraService quadraService;
    private final HorarioFuncionamentoRepository horarios;
    private final ReservaRepository reservas;

    public FuncionamentoService(QuadraService quadraService,
            HorarioFuncionamentoRepository horarios, ReservaRepository reservas) {
        this.quadraService = quadraService;
        this.horarios = horarios;
        this.reservas = reservas;
    }

    public List<FuncionamentoResponse> consultar(Long id) {
        quadraService.encontrar(id);
        return horarios.findByQuadraId(id).stream()
            .sorted(Comparator.comparing(HorarioFuncionamento::getDiaSemana))
            .map(FuncionamentoResponse::de).toList();
    }

    @Transactional
    public List<FuncionamentoResponse> substituir(Long id, List<FuncionamentoRequest> semana) {
        Quadra quadra = quadraService.bloquear(id);
        var proposta = new EnumMap<DayOfWeek, HorarioFuncionamento>(DayOfWeek.class);
        for (FuncionamentoRequest item : semana) {
            if (item == null || item.diaSemana() == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Informe o dia da semana.");
            }
            DayOfWeek dia;
            try {
                dia = DayOfWeek.valueOf(item.diaSemana());
            } catch (IllegalArgumentException ex) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Dia da semana inválido.");
            }
            LocalTime abertura = interpretarHora(item.abertura());
            LocalTime fechamento = interpretarHora(item.fechamento());
            if (!abertura.isBefore(fechamento)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A abertura deve ser anterior ao fechamento.");
            }
            if (proposta.putIfAbsent(dia, new HorarioFuncionamento(quadra, dia, abertura, fechamento)) != null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Informe apenas um horário por dia da semana.");
            }
        }

        validarReservas(id, proposta);
        horarios.deleteAll(horarios.findByQuadraId(id));
        horarios.flush();
        return horarios.saveAll(proposta.values()).stream().map(FuncionamentoResponse::de).toList();
    }

    private LocalTime interpretarHora(String valor) {
        if (valor == null || !HORARIO_VALIDO.matcher(valor).matches()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Use horários no formato HH:mm em blocos de 30 minutos.");
        }
        return LocalTime.parse(valor);
    }

    private void validarReservas(Long id, Map<DayOfWeek, HorarioFuncionamento> proposta) {
        for (var reserva : reservas.findByQuadraIdAndEstadoAndFimAfter(id, EstadoReserva.CONFIRMADA, Instant.now())) {
            var inicio = reserva.getInicio().atZone(BRASILIA);
            var fim = reserva.getFim().atZone(BRASILIA);
            var horario = proposta.get(inicio.getDayOfWeek());
            if (horario == null || !inicio.toLocalDate().equals(fim.toLocalDate())
                    || inicio.toLocalTime().isBefore(horario.getAbertura())
                    || fim.toLocalTime().isAfter(horario.getFechamento())) {
                throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "A alteração de funcionamento invalida uma reserva confirmada.");
            }
        }
    }
}
