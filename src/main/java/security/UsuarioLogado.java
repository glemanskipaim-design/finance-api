package security;

import entity.Usuario;
import erro.ApiException;
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

    /** Se o usuário do token não existe mais (ex.: banco recriado), responde 401. */
    public Usuario usuario() {
        Usuario usuario = Usuario.findById(id());
        if (usuario == null) throw ApiException.naoAutorizado("sessão inválida; entre de novo");
        return usuario;
    }
}
