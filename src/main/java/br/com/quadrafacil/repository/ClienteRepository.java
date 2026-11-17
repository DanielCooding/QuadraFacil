package br.com.quadrafacil.repository;

import br.com.quadrafacil.entity.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {}
