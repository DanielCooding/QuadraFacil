package br.com.quadrafacil.repository;

import br.com.quadrafacil.entity.HorarioFuncionamento;
import java.time.DayOfWeek;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HorarioFuncionamentoRepository extends JpaRepository<HorarioFuncionamento, Long> {
    Optional<HorarioFuncionamento> findByQuadraIdAndDiaSemana(Long quadraId, DayOfWeek diaSemana);

    List<HorarioFuncionamento> findByQuadraId(Long quadraId);
}
