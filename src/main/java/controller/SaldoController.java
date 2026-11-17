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
        Integer usuarioId = logado.id();
        List<SaldoConta> contas = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;

        for (Conta conta : Conta.doUsuario(usuarioId)) {
            BigDecimal saldo = BigDecimal.ZERO;
            Integer semFiltroDeCategoria = null;
            for (Transacao transacao : Transacao.filtrar(usuarioId, conta.id, semFiltroDeCategoria)) {
                if (Transacao.RECEITA.equals(transacao.tipo)) {
                    saldo = saldo.add(transacao.valor);
                } else {
                    saldo = saldo.subtract(transacao.valor);
                }
            }
            contas.add(new SaldoConta(conta.id, conta.descricao, saldo));
            total = total.add(saldo);
        }
        return new SaldoResponse(contas, total);
    }
}
