package br.com.quadrafacil.quadra.entity;

import jakarta.persistence.*;
import java.util.UUID;
import java.math.BigDecimal;

@Entity
@Table(name = "quadras")
public class Quadra {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 100)
    private String nome;

    @Column(nullable = false, length = 40)
    private String modalidade;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal precoHora;

    @Column(nullable = false)
    private boolean ativa;

    protected Quadra() {}

    public Quadra(String nome, String modalidade, BigDecimal precoHora, boolean ativa) {
        this.nome = nome;
        this.modalidade = modalidade;
        this.precoHora = precoHora;
        this.ativa = ativa;
    }

    public UUID getId() { return id; }
    public String getNome() { return nome; }
    public String getModalidade() { return modalidade; }
    public BigDecimal getPrecoHora() { return precoHora; }
    public boolean isAtiva() { return ativa; }
}
