package br.com.quadrafacil.repository;

import br.com.quadrafacil.entity.Reserva;
import br.com.quadrafacil.entity.EstadoReserva;
import java.time.Instant;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservaRepository extends JpaRepository<Reserva, Long> {
    List<Reserva> findByQuadraIdAndEstadoAndFimAfter(Long quadraId, EstadoReserva estado, Instant limite);
}
