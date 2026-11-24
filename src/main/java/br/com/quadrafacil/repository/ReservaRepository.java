package br.com.quadrafacil.repository;

import br.com.quadrafacil.entity.Reserva;
import br.com.quadrafacil.entity.EstadoReserva;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservaRepository extends JpaRepository<Reserva, Long> {
    @Query("select r.quadra.id from Reserva r where r.id = :id")
    Optional<Long> buscarQuadraId(@Param("id") Long id);

    boolean existsByQuadraIdAndEstadoAndInicioBeforeAndFimAfter(
        Long quadraId, EstadoReserva estado, Instant fim, Instant inicio);

    List<Reserva> findByQuadraIdAndEstadoAndInicioGreaterThanEqualAndInicioLessThanOrderByInicioAscIdAsc(
        Long quadraId, EstadoReserva estado, Instant inicioDia, Instant inicioDiaSeguinte);

    List<Reserva> findByQuadraIdAndEstadoAndFimAfter(Long quadraId, EstadoReserva estado, Instant limite);
}
