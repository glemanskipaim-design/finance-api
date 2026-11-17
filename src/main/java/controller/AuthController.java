package controller;

import dto.LoginRequest;
import dto.RegistroRequest;
import dto.UsuarioResponse;
import entity.Usuario;
import io.quarkus.elytron.security.common.BcryptUtil;
import io.quarkus.security.Authenticated;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;
import security.TokenService;
import security.UsuarioLogado;
import service.ApiException;

import java.util.Locale;

@Path("auth")
public class AuthController {
    @Inject
    TokenService tokens;

    @Inject
    UsuarioLogado logado;

    /** Cria o usuário e já devolve o cookie de sessão (entra direto). */
    @POST
    @Path("registrar")
    @Transactional
    public Response registrar(@Valid RegistroRequest dados) {
        String email = normalizar(dados.email());
        if (Usuario.porEmail(email) != null) {
            throw ApiException.conflito("e-mail já cadastrado");
        }
        Usuario usuario = new Usuario();
        usuario.nome = dados.nome().trim();
        usuario.email = email;
        usuario.senhaHash = BcryptUtil.bcryptHash(dados.senha()); // só o hash vai pro banco
        usuario.persist();
        return Response.status(Response.Status.CREATED)
                .entity(UsuarioResponse.de(usuario))
                .cookie(tokens.cookieDeLogin(usuario))
                .build();
    }

    @POST
    @Path("login")
    public Response login(@Valid LoginRequest dados) {
        Usuario usuario = Usuario.porEmail(normalizar(dados.email()));
        if (usuario == null || !BcryptUtil.matches(dados.senha(), usuario.senhaHash)) {
            // Mensagem única: não revela se o erro foi no e-mail ou na senha.
            throw ApiException.naoAutorizado("e-mail ou senha inválidos");
        }
        return Response.ok(UsuarioResponse.de(usuario))
                .cookie(tokens.cookieDeLogin(usuario))
                .build();
    }

    @POST
    @Path("logout")
    public Response logout() {
        return Response.noContent().cookie(tokens.cookieDeLogout()).build();
    }

    /** Quem está logado. O front chama ao abrir para saber se a sessão (cookie) ainda vale. */
    @GET
    @Path("me")
    @Authenticated
    public UsuarioResponse me() {
        return UsuarioResponse.de(logado.usuario());
    }

    private static String normalizar(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }
}
