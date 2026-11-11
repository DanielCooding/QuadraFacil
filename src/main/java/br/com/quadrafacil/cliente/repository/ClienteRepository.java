package br.com.quadrafacil.cliente.repository;

import br.com.quadrafacil.cliente.entity.Cliente;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClienteRepository extends JpaRepository<Cliente, UUID> {}
