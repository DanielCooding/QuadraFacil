package br.com.quadrafacil.funcionamento.repository;

import br.com.quadrafacil.funcionamento.entity.HorarioFuncionamento;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HorarioFuncionamentoRepository extends JpaRepository<HorarioFuncionamento, UUID> {}
