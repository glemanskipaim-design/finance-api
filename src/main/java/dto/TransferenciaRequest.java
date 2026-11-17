package dto;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public record TransferenciaRequest(
        @NotNull(message = "informe a conta de origem") Integer contaOrigemId,
        @NotNull(message = "informe a conta de destino") Integer contaDestinoId,
        @NotNull(message = "informe o valor") @Positive(message = "o valor deve ser maior que zero")
        @Digits(integer = 10, fraction = 2, message = "valor com no máximo 2 casas decimais") BigDecimal valor,
        @NotNull(message = "informe a data") LocalDate data,
        @Size(max = 400, message = "descrição com no máximo 400 caracteres") String descricao) {
}
