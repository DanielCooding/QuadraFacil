package br.com.quadrafacil.repository;

import br.com.quadrafacil.entity.Quadra;
import jakarta.persistence.LockModeType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface QuadraRepository extends JpaRepository<Quadra, Long> {
    // A criacao de reservas deve adquirir o mesmo bloqueio antes de consultar o funcionamento.
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select q from Quadra q where q.id = :id")
    Optional<Quadra> buscarParaAtualizacao(@Param("id") Long id);
}
