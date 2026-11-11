package br.com.quadrafacil.reserva.repository;

import br.com.quadrafacil.reserva.entity.Reserva;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservaRepository extends JpaRepository<Reserva, UUID> {}
