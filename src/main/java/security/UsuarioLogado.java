package security;

import entity.Usuario;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.jwt.JsonWebToken;

/** Quem fez a requisição. O Quarkus já validou o JWT do cookie; o "subject" é o id do usuário. */
@RequestScoped
public class UsuarioLogado {
    @Inject
    JsonWebToken jwt;

    public Integer id() {
        return Integer.valueOf(jwt.getSubject());
    }

    public Usuario usuario() {
        return Usuario.findById(id());
    }
}
