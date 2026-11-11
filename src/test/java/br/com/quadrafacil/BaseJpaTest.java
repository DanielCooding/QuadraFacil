package br.com.quadrafacil;

import br.com.quadrafacil.cliente.entity.Cliente;
import br.com.quadrafacil.cliente.repository.ClienteRepository;
import br.com.quadrafacil.quadra.entity.Quadra;
import br.com.quadrafacil.quadra.repository.QuadraRepository;
import br.com.quadrafacil.reserva.entity.*;
import br.com.quadrafacil.reserva.repository.ReservaRepository;
import br.com.quadrafacil.funcionamento.entity.HorarioFuncionamento;
import br.com.quadrafacil.funcionamento.repository.HorarioFuncionamentoRepository;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.time.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class BaseJpaTest {
    @Autowired ClienteRepository clientes;
    @Autowired QuadraRepository quadras;
    @Autowired ReservaRepository reservas;
    @Autowired HorarioFuncionamentoRepository horarios;
    @Autowired EntityManager em;

    @Test void persisteERelacionaAsQuatroEntidadesNoH2() {
        var cliente = clientes.save(new Cliente("João", "joao@example.com"));
        var quadra = quadras.save(new Quadra("Central", "Futebol", new BigDecimal("120.00"), true));
        var horario = horarios.save(new HorarioFuncionamento(quadra, DayOfWeek.MONDAY,
            LocalTime.of(8, 0), LocalTime.of(22, 0)));
        var inicio = Instant.parse("2030-01-07T21:00:00Z");
        var reserva = reservas.save(new Reserva(cliente, quadra, inicio, inicio.plusSeconds(3600),
            new BigDecimal("120.00"), EstadoReserva.CONFIRMADA));
        em.flush();
        em.clear();
        var salva = reservas.findById(reserva.getId()).orElseThrow();
        assertEquals(cliente.getId(), salva.getCliente().getId());
        assertEquals(quadra.getId(), salva.getQuadra().getId());
        assertEquals(inicio, salva.getInicio());
        assertEquals(new BigDecimal("120.00"), salva.getValorTotal());
        assertEquals(EstadoReserva.CONFIRMADA, salva.getEstado());
        assertEquals(DayOfWeek.MONDAY, horarios.findById(horario.getId()).orElseThrow().getDiaSemana());
        assertEquals("João", clientes.findById(cliente.getId()).orElseThrow().getNome());
    }
}
