package br.com.quadrafacil.repository;

import br.com.quadrafacil.entity.HorarioFuncionamento;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HorarioFuncionamentoRepository extends JpaRepository<HorarioFuncionamento, Long> {
    List<HorarioFuncionamento> findByQuadraId(Long quadraId);
}
