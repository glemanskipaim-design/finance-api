package dto;

import java.math.BigDecimal;
import java.util.List;

public record SaldoResponse(List<SaldoConta> contas, BigDecimal total) {
    public record SaldoConta(Integer id, String descricao, BigDecimal saldo) {
    }
}
