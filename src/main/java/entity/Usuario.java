package entity;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Usuario extends PanacheEntityBase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Integer id;

    public String nome;

    @Column(unique = true)
    public String email;

    // Só o hash BCrypt fica no banco; a senha em texto nunca é guardada.
    @Column(name = "senhahash")
    public String senhaHash;

    public static Usuario porEmail(String email) {
        return find("email", email).firstResult();
    }
}
