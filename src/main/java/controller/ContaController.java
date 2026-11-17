package controller;

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

@Path("conta")
@Authenticated // sem cookie válido: 401
public class ContaController {
    @Inject
    UsuarioLogado logado;

    @GET
    public List<Conta> todas() {
        return Conta.doUsuario(logado.id());
    }

    @GET
    @Path("{id}")
    public Conta buscar(@PathParam("id") Integer id) {
        return buscarDoUsuario(id);
    }

    @POST
    @Transactional
    public Response save(@Valid Conta obj) {
        obj.id = null;
        obj.descricao = obj.descricao.trim();
        obj.usuario = logado.usuario();
        obj.persist();
        return Response.status(Response.Status.CREATED).entity(obj).build();
    }

    @PUT
    @Path("{id}")
    @Transactional
    public Conta update(@PathParam("id") Integer id, @Valid Conta dados) {
        Conta obj = buscarDoUsuario(id);
        obj.descricao = dados.descricao.trim();
        return obj;
    }

    @DELETE
    @Path("{id}")
    @Transactional
    public Response delete(@PathParam("id") Integer id) {
        Conta obj = buscarDoUsuario(id);
        if (Transacao.count("conta.id", id) > 0) {
            throw ApiException.conflito("a conta tem transações; exclua as transações antes");
        }
        obj.delete();
        return Response.noContent().build();
    }

    // Conta de outro usuário responde 404, como se não existisse.
    private Conta buscarDoUsuario(Integer id) {
        Conta obj = Conta.doUsuario(id, logado.id());
        if (obj == null) throw ApiException.naoEncontrado("conta não encontrada");
        return obj;
    }
}
