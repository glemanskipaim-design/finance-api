package dto;

import entity.Usuario;

/** Dados públicos do usuário (sem o hash da senha). */
public record UsuarioResponse(Integer id, String nome, String email) {
    public static UsuarioResponse de(Usuario usuario) {
        return new UsuarioResponse(usuario.id, usuario.nome, usuario.email);
    }
}
