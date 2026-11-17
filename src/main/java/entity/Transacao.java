package entity;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import io.quarkus.panache.common.Parameters;
import jakarta.persistence.*;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Entity
public class Transacao extends PanacheEntityBase {
    public static final String RECEITA = "R";
    public static final String DESPESA = "D";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Integer id;

    @NotBlank(message = "informe a descrição")
    @Size(max = 500, message = "descrição com no máximo 500 caracteres")
    public String descricao;

    @NotNull(message = "informe o valor")
    @Positive(message = "o valor deve ser maior que zero")
    @Digits(integer = 10, fraction = 2, message = "valor com no máximo 2 casas decimais")
    public BigDecimal valor;

    // "R" = receita, "D" = despesa
    @NotNull(message = "informe o tipo")
    @Pattern(regexp = "[RD]", message = "tipo deve ser R (receita) ou D (despesa)")
    public String tipo;

    @NotNull(message = "informe a data")
    @Column(name = "datatransacao")
    public LocalDate dataTransacao;

    @ManyToOne
    @JoinColumn(name = "conta_id")
    public Conta conta;

    // Fica nula nas duas transações geradas por uma transferência.
    @ManyToOne
    @JoinColumn(name = "categoria_id")
    public Categoria categoria;

    // Mesmo código (UUID) nas duas transações de uma transferência; nulo nas demais.
    public String transferencia;

    /** Transações do usuário, com filtros opcionais por conta e por categoria. */
    public static List<Transacao> filtrar(Integer usuarioId, Integer contaId, Integer categoriaId) {
        StringBuilder query = new StringBuilder("conta.usuario.id = :usuario");
        Parameters params = Parameters.with("usuario", usuarioId);
        if (contaId != null) {
            query.append(" and conta.id = :conta");
            params.and("conta", contaId);
        }
        if (categoriaId != null) {
            query.append(" and categoria.id = :categoria");
            params.and("categoria", categoriaId);
        }
        query.append(" order by dataTransacao desc, id desc");
        return list(query.toString(), params);
    }

    /** Devolve a transação só se ela for de uma conta do usuário; senão, null. */
    public static Transacao doUsuario(Integer id, Integer usuarioId) {
        return find("id = ?1 and conta.usuario.id = ?2", id, usuarioId).firstResult();
    }
}
