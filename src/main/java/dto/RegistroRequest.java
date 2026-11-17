package dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegistroRequest(
        @NotBlank(message = "informe o nome") @Size(max = 150, message = "nome com no máximo 150 caracteres") String nome,
        @NotBlank(message = "informe o e-mail") @Email(message = "e-mail inválido") @Size(max = 150) String email,
        // BCrypt só considera os primeiros 72 bytes da senha.
        @NotBlank(message = "informe a senha") @Size(min = 6, max = 72, message = "senha entre 6 e 72 caracteres") String senha) {
}
