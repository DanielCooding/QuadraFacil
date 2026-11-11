package br.com.quadrafacil.reserva.entity;

import jakarta.persistence.*;
import java.util.UUID;
import java.math.BigDecimal;
import java.time.Instant;
import br.com.quadrafacil.cliente.entity.Cliente;
import br.com.quadrafacil.quadra.entity.Quadra;

@Entity
@Table(name = "reservas")
public class Reserva {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "quadra_id", nullable = false)
    private Quadra quadra;

    @Column(nullable = false)
    private Instant inicio;

    @Column(nullable = false)
    private Instant fim;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal valorTotal;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoReserva estado;

    protected Reserva() {}

    public Reserva(Cliente cliente, Quadra quadra, Instant inicio, Instant fim, BigDecimal valorTotal, EstadoReserva estado) {
        this.cliente = cliente;
        this.quadra = quadra;
        this.inicio = inicio;
        this.fim = fim;
        this.valorTotal = valorTotal;
        this.estado = estado;
    }

    public UUID getId() { return id; }
    public Cliente getCliente() { return cliente; }
    public Quadra getQuadra() { return quadra; }
    public Instant getInicio() { return inicio; }
    public Instant getFim() { return fim; }
    public BigDecimal getValorTotal() { return valorTotal; }
    public EstadoReserva getEstado() { return estado; }
}
