package dto;

import entity.Transacao;

/** As duas transações geradas: a despesa na origem e a receita no destino. */
public record TransferenciaResponse(String transferencia, Transacao saida, Transacao entrada) {
}
