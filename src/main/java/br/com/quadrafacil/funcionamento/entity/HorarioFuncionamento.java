package br.com.quadrafacil.funcionamento.entity;

import jakarta.persistence.*;
import java.util.UUID;
import java.time.DayOfWeek;
import java.time.LocalTime;
import br.com.quadrafacil.quadra.entity.Quadra;

@Entity
@Table(name = "horarios_funcionamento", uniqueConstraints = @UniqueConstraint(columnNames = {"quadra_id", "dia_semana"}))
public class HorarioFuncionamento {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "quadra_id", nullable = false)
    private Quadra quadra;

    @Enumerated(EnumType.STRING)
    @Column(name = "dia_semana", nullable = false, length = 10)
    private DayOfWeek diaSemana;

    @Column(nullable = false)
    private LocalTime abertura;

    @Column(nullable = false)
    private LocalTime fechamento;

    protected HorarioFuncionamento() {}

    public HorarioFuncionamento(Quadra quadra, DayOfWeek diaSemana, LocalTime abertura, LocalTime fechamento) {
        this.quadra = quadra;
        this.diaSemana = diaSemana;
        this.abertura = abertura;
        this.fechamento = fechamento;
    }

    public UUID getId() { return id; }
    public Quadra getQuadra() { return quadra; }
    public DayOfWeek getDiaSemana() { return diaSemana; }
    public LocalTime getAbertura() { return abertura; }
    public LocalTime getFechamento() { return fechamento; }
}
