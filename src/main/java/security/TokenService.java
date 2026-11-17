package security;

import entity.Usuario;
import io.smallrye.jwt.build.Jwt;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.core.NewCookie;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.time.Duration;
import java.util.Set;

/**
 * Emite o JWT e o entrega dentro de um cookie httpOnly.
 * A validação do token em cada requisição é feita pelo próprio Quarkus (smallrye-jwt),
 * configurado em application.properties para ler o cookie {@value #COOKIE}.
 */
@ApplicationScoped
public class TokenService {
    public static final String COOKIE = "finance_token";

    @ConfigProperty(name = "mp.jwt.verify.issuer")
    String issuer;

    @ConfigProperty(name = "app.jwt.duracao-minutos", defaultValue = "120")
    long duracaoMinutos;

    @ConfigProperty(name = "app.cookie.secure", defaultValue = "false")
    boolean cookieSeguro;

    public NewCookie cookieDeLogin(Usuario usuario) {
        String token = Jwt.issuer(issuer)
                .subject(String.valueOf(usuario.id))   // id do usuário: é o que os controllers usam
                .upn(usuario.email)
                .claim("nome", usuario.nome)
                .groups(Set.of("usuario"))
                .expiresIn(Duration.ofMinutes(duracaoMinutos))
                .sign();                              // assinado com a chave privada RSA (RS256)
        return cookie(token, (int) Duration.ofMinutes(duracaoMinutos).toSeconds());
    }

    /** Cookie vazio com validade zero: o navegador apaga o token. */
    public NewCookie cookieDeLogout() {
        return cookie("", 0);
    }

    private NewCookie cookie(String valor, int maxAgeSegundos) {
        return new NewCookie.Builder(COOKIE)
                .value(valor)
                .path("/")
                .httpOnly(true)                       // JavaScript não lê o token (protege contra XSS)
                .secure(cookieSeguro)                 // só HTTPS em produção
                .sameSite(NewCookie.SameSite.STRICT)  // não vai em requisições de outros sites (CSRF)
                .maxAge(maxAgeSegundos)
                .build();
    }
}
