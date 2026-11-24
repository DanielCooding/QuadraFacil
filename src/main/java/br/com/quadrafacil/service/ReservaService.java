package br.com.quadrafacil.service;

import static br.com.quadrafacil.entity.EstadoReserva.CANCELADA;
import static br.com.quadrafacil.entity.EstadoReserva.CONFIRMADA;
import static br.com.quadrafacil.service.FuncionamentoService.BRASILIA;

import br.com.quadrafacil.dto.AgendaResponse;
import br.com.quadrafacil.dto.FuncionamentoResponse;
import br.com.quadrafacil.dto.ReservaRequest;
import br.com.quadrafacil.dto.ReservaResponse;
import br.com.quadrafacil.entity.Reserva;
import br.com.quadrafacil.repository.ClienteRepository;
import br.com.quadrafacil.repository.HorarioFuncionamentoRepository;
import br.com.quadrafacil.repository.ReservaRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional(readOnly = true)
public class ReservaService {
    private static final BigDecimal VALOR_MAXIMO = new BigDecimal("9999999999.99");

    private final ClienteRepository clientes;
    private final QuadraService quadras;
    private final HorarioFuncionamentoRepository horarios;
    private final ReservaRepository reservas;

    public ReservaService(ClienteRepository clientes, QuadraService quadras,
            HorarioFuncionamentoRepository horarios, ReservaRepository reservas) {
        this.clientes = clientes;
        this.quadras = quadras;
        this.horarios = horarios;
        this.reservas = reservas;
    }

    @Transactional
    public ReservaResponse criar(ReservaRequest pedido) {
        var cliente = clientes.findById(pedido.clienteId()).orElseThrow(() ->
            new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente não encontrado."));
        var quadra = quadras.bloquear(pedido.quadraId());
        if (!quadra.isAtiva()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "A quadra está inativa.");
        }

        Instant inicio = pedido.inicio().toInstant();
        Instant fim = pedido.fim().toInstant();
        long minutos = Duration.between(inicio, fim).toMinutes();
        validarIntervalo(quadra.getId(), inicio.atZone(BRASILIA), fim.atZone(BRASILIA), minutos);

        BigDecimal total = quadra.getPrecoHora().multiply(BigDecimal.valueOf(minutos))
            .divide(BigDecimal.valueOf(60), 2, RoundingMode.HALF_UP);
        if (total.compareTo(VALOR_MAXIMO) > 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O valor total excede o limite da reserva.");
        }

        if (reservas.existsByQuadraIdAndEstadoAndInicioBeforeAndFimAfter(quadra.getId(), CONFIRMADA, fim, inicio)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                "Já existe uma reserva para essa quadra no intervalo informado.");
        }

        return ReservaResponse.de(reservas.save(new Reserva(cliente, quadra, inicio, fim, total, CONFIRMADA)));
    }

    public ReservaResponse consultar(Long id) {
        return ReservaResponse.de(encontrar(id));
    }

    @Transactional
    public ReservaResponse cancelar(Long id) {
        // Consulta só o ID da quadra para carregar a reserva depois de obter o bloqueio.
        Long quadraId = reservas.buscarQuadraId(id).orElseThrow(ReservaService::reservaNaoEncontrada);
        quadras.bloquear(quadraId);
        Reserva reserva = encontrar(id);
        if (reserva.getEstado() == CANCELADA) {
            return ReservaResponse.de(reserva);
        }
        if (!Instant.now().isBefore(reserva.getInicio())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Não é possível cancelar após o início da reserva.");
        }
        reserva.cancelar();
        return ReservaResponse.de(reserva);
    }

    public AgendaResponse agenda(Long quadraId, LocalDate data) {
        var quadra = quadras.consultar(quadraId);
        var funcionamento = horarios.findByQuadraIdAndDiaSemana(quadraId, data.getDayOfWeek())
            .map(FuncionamentoResponse::de)
            .map(hora -> new AgendaResponse.Janela(hora.abertura(), hora.fechamento()))
            .orElse(null);

        Instant inicioDia = data.atStartOfDay(BRASILIA).toInstant();
        Instant diaSeguinte = data.plusDays(1).atStartOfDay(BRASILIA).toInstant();
        var confirmadas = reservas
            .findByQuadraIdAndEstadoAndInicioGreaterThanEqualAndInicioLessThanOrderByInicioAscIdAsc(
                quadraId, CONFIRMADA, inicioDia, diaSeguinte)
            .stream().map(AgendaResponse.HorarioReservado::de).toList();

        return new AgendaResponse(data, BRASILIA.getId(), quadra, funcionamento, confirmadas);
    }

    private void validarIntervalo(Long quadraId, ZonedDateTime inicio, ZonedDateTime fim, long minutos) {
        if (!inicio.toInstant().isAfter(Instant.now()) || minutos < 60 || minutos % 30 != 0
                || !alinhado(inicio.toLocalTime()) || !alinhado(fim.toLocalTime())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "Informe um intervalo futuro de pelo menos uma hora, em blocos de 30 minutos.");
        }

        var horario = horarios.findByQuadraIdAndDiaSemana(quadraId, inicio.getDayOfWeek());
        if (!inicio.toLocalDate().equals(fim.toLocalDate()) || horario.isEmpty()
                || inicio.toLocalTime().isBefore(horario.get().getAbertura())
                || fim.toLocalTime().isAfter(horario.get().getFechamento())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "O intervalo deve estar dentro do funcionamento da quadra no mesmo dia.");
        }
    }

    private static boolean alinhado(LocalTime hora) {
        return hora.getMinute() % 30 == 0 && hora.getSecond() == 0 && hora.getNano() == 0;
    }

    private Reserva encontrar(Long id) {
        return reservas.findById(id).orElseThrow(ReservaService::reservaNaoEncontrada);
    }

    private static ResponseStatusException reservaNaoEncontrada() {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, "Reserva não encontrada.");
    }
}
