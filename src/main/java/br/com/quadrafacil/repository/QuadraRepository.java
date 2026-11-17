package br.com.quadrafacil.repository;

import br.com.quadrafacil.entity.Quadra;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuadraRepository extends JpaRepository<Quadra, UUID> {}
