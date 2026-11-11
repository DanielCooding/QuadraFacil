package br.com.quadrafacil.quadra.repository;

import br.com.quadrafacil.quadra.entity.Quadra;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuadraRepository extends JpaRepository<Quadra, UUID> {}
