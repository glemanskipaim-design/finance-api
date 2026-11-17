package controller;

import entity.Categoria;
import entity.Conta;
import entity.Transacao;
import io.quarkus.security.Authenticated;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import security.UsuarioLogado;
import erro.ApiException;

import java.util.List;

@Path("transacao")
@Authenticated
public class TransacaoController {
    @Inject
    UsuarioLogado logado;

    /** Lista as transações; filtros opcionais: ?contaId=1&categoriaId=2 */
    @GET
    public List<Transacao> todas(@QueryParam("contaId") Integer contaId,
                                 @QueryParam("categoriaId") Integer categoriaId) {
        return Transacao.filtrar(logado.id(), contaId, categoriaId);
    }

    @GET
    @Path("{id}")
    public Transacao buscar(@PathParam("id") Integer id) {
        return buscarDoUsuario(id);
    }

    @POST
    @Transactional
    public Response save(@Valid Transacao obj) {
        obj.id = null;
        obj.transferencia = null; // transferência só pelo endpoint /transferencia
        obj.descricao = obj.descricao.trim();
        resolverRelacionamentos(obj);
        obj.persist();
        return Response.status(Response.Status.CREATED).entity(obj).build();
    }

    @PUT
    @Path("{id}")
    @Transactional
    public Transacao update(@PathParam("id") Integer id, @Valid Transacao dados) {
        Transacao obj = buscarDoUsuario(id);
        if (obj.transferencia != null) {
            throw ApiException.conflito("transação de transferência não pode ser editada; exclua e lance a transferência de novo");
        }
        obj.descricao = dados.descricao.trim();
        obj.valor = dados.valor;
        obj.tipo = dados.tipo;
        obj.dataTransacao = dados.dataTransacao;
        obj.conta = dados.conta;
        obj.categoria = dados.categoria;
        resolverRelacionamentos(obj);
        return obj;
    }

    /** Excluir uma perna de transferência exclui as duas, para o saldo não ficar torto. */
    @DELETE
    @Path("{id}")
    @Transactional
    public Response delete(@PathParam("id") Integer id) {
        Transacao obj = buscarDoUsuario(id);
        if (obj.transferencia != null) {
            Transacao.delete("transferencia", obj.transferencia);
        } else {
            obj.delete();
        }
        return Response.noContent().build();
    }

    private Transacao buscarDoUsuario(Integer id) {
        Transacao obj = Transacao.doUsuario(id, logado.id());
        if (obj == null) throw ApiException.naoEncontrado("transação não encontrada");
        return obj;
    }

    // O JSON traz só o id da conta e da categoria ({"conta": {"id": 1}}).
    // Aqui buscamos as entidades no banco, conferindo que são do usuário logado.
    private void resolverRelacionamentos(Transacao transacao) {
        Integer usuarioId = logado.id();

        Conta conta = null;
        if (transacao.conta != null && transacao.conta.id != null) {
            conta = Conta.doUsuario(transacao.conta.id, usuarioId);
        }
        if (conta == null) throw ApiException.invalido("conta inexistente");
        transacao.conta = conta;

        Categoria categoria = null;
        if (transacao.categoria != null && transacao.categoria.id != null) {
            categoria = Categoria.doUsuario(transacao.categoria.id, usuarioId);
        }
        if (categoria == null) throw ApiException.invalido("categoria inexistente");
        transacao.categoria = categoria;
    }
}
