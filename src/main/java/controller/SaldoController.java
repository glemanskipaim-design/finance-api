package controller;

import dto.SaldoResponse;
import dto.SaldoResponse.SaldoConta;
import entity.Conta;
import entity.Transacao;
import io.quarkus.security.Authenticated;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import security.UsuarioLogado;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Path("saldo")
@Authenticated
public class SaldoController {
    @Inject
    UsuarioLogado logado;

    /** Saldo de cada conta (receitas - despesas) e o saldo geral (soma das contas). */
    @GET
    public SaldoResponse saldo() {
        List<SaldoConta> contas = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;

        for (Conta conta : Conta.doUsuario(logado.id())) {
            BigDecimal saldo = BigDecimal.ZERO;
            for (Transacao t : Transacao.filtrar(logado.id(), conta.id, null)) {
                if (Transacao.RECEITA.equals(t.tipo)) {
                    saldo = saldo.add(t.valor);
                } else {
                    saldo = saldo.subtract(t.valor);
                }
            }
            contas.add(new SaldoConta(conta.id, conta.descricao, saldo));
            total = total.add(saldo);
        }
        return new SaldoResponse(contas, total);
    }
}
