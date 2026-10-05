package entity;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
public class Transacao extends PanacheEntityBase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Integer id;

    public String descricao;

    public BigDecimal valor;

    // "R" = receita, "D" = despesa
    public String tipo;

    @Column(name = "datatransacao")
    public LocalDate dataTransacao;

    @ManyToOne
    @JoinColumn(name = "conta_id")
    public Conta conta;

    @ManyToOne
    @JoinColumn(name = "categoria_id")
    public Categoria categoria;
}
