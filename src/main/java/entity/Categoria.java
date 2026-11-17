package entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

@Entity
public class Categoria extends PanacheEntityBase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Integer id;

    @NotBlank(message = "informe a descrição")
    @Size(max = 150, message = "descrição com no máximo 150 caracteres")
    public String descricao;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    public Usuario usuario;

    public static List<Categoria> doUsuario(Integer usuarioId) {
        return list("usuario.id = ?1 order by descricao", usuarioId);
    }

    /** Devolve a categoria só se ela pertencer ao usuário; senão, null. */
    public static Categoria doUsuario(Integer id, Integer usuarioId) {
        return find("id = ?1 and usuario.id = ?2", id, usuarioId).firstResult();
    }
}
