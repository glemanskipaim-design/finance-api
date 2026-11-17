package controller;

import entity.Categoria;
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

@Path("categoria")
@Authenticated
public class CategoriaController {
    @Inject
    UsuarioLogado logado;

    @GET
    public List<Categoria> todas() {
        return Categoria.doUsuario(logado.id());
    }

    @GET
    @Path("{id}")
    public Categoria buscar(@PathParam("id") Integer id) {
        return buscarDoUsuario(id);
    }

    @POST
    @Transactional
    public Response save(@Valid Categoria obj) {
        obj.id = null;
        obj.descricao = obj.descricao.trim();
        obj.usuario = logado.usuario();
        obj.persist();
        return Response.status(Response.Status.CREATED).entity(obj).build();
    }

    @PUT
    @Path("{id}")
    @Transactional
    public Categoria update(@PathParam("id") Integer id, @Valid Categoria dados) {
        Categoria obj = buscarDoUsuario(id);
        obj.descricao = dados.descricao.trim();
        return obj;
    }

    @DELETE
    @Path("{id}")
    @Transactional
    public Response delete(@PathParam("id") Integer id) {
        Categoria obj = buscarDoUsuario(id);
        if (Transacao.count("categoria.id", id) > 0) {
            throw ApiException.conflito("a categoria tem transações; exclua ou mude essas transações antes");
        }
        obj.delete();
        return Response.noContent().build();
    }

    private Categoria buscarDoUsuario(Integer id) {
        Categoria obj = Categoria.doUsuario(id, logado.id());
        if (obj == null) throw ApiException.naoEncontrado("categoria não encontrada");
        return obj;
    }
}
