package controller;

import dto.TransferenciaRequest;
import dto.TransferenciaResponse;
import entity.Conta;
import entity.Transacao;
import io.quarkus.security.Authenticated;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;
import security.UsuarioLogado;
import service.ApiException;

import java.util.UUID;

@Path("transferencia")
@Authenticated
public class TransferenciaController {
    @Inject
    UsuarioLogado logado;

    /**
     * Uma transferência vira duas transações ligadas pelo mesmo código:
     * despesa (D) na conta de origem e receita (R) na conta de destino.
     * O @Transactional garante que as duas são gravadas juntas ou nenhuma é.
     */
    @POST
    @Transactional
    public Response transferir(@Valid TransferenciaRequest dados) {
        if (dados.contaOrigemId().equals(dados.contaDestinoId())) {
            throw ApiException.invalido("origem e destino devem ser contas diferentes");
        }
        Conta origem = Conta.doUsuario(dados.contaOrigemId(), logado.id());
        Conta destino = Conta.doUsuario(dados.contaDestinoId(), logado.id());
        if (origem == null || destino == null) {
            throw ApiException.invalido("conta inexistente");
        }

        String codigo = UUID.randomUUID().toString();
        String obs = dados.descricao() == null || dados.descricao().isBlank() ? "" : " - " + dados.descricao().trim();
        Transacao saida = perna(codigo, origem, Transacao.DESPESA, "Transferência para " + destino.descricao + obs, dados);
        Transacao entrada = perna(codigo, destino, Transacao.RECEITA, "Transferência de " + origem.descricao + obs, dados);

        return Response.status(Response.Status.CREATED)
                .entity(new TransferenciaResponse(codigo, saida, entrada))
                .build();
    }

    private static Transacao perna(String codigo, Conta conta, String tipo, String descricao, TransferenciaRequest dados) {
        Transacao t = new Transacao();
        t.transferencia = codigo;
        t.conta = conta;
        t.tipo = tipo;
        t.descricao = descricao;
        t.valor = dados.valor();
        t.dataTransacao = dados.data();
        t.persist();
        return t;
    }
}
