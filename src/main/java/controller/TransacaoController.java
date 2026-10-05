package controller;

import entity.Categoria;
import entity.Conta;
import entity.Transacao;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("transacao")
public class TransacaoController {
    @GET
    public List<Transacao> todas() {
        return Transacao.listAll();
    }

    @GET
    @Path("{id}")
    public Transacao buscar(@PathParam("id") Integer id) {
        Transacao obj = Transacao.findById(id);
        if (obj == null) throw new NotFoundException();
        return obj;
    }

    @POST
    @Transactional
    public Response save(Transacao obj) {
        obj.id = null;
        resolverRelacionamentos(obj);
        obj.persist();
        return Response.status(Response.Status.CREATED).entity(obj).build();
    }

    @PUT
    @Path("{id}")
    @Transactional
    public Transacao update(@PathParam("id") Integer id, Transacao dados) {
        Transacao obj = Transacao.findById(id);
        if (obj == null) throw new NotFoundException();
        obj.descricao = dados.descricao;
        obj.valor = dados.valor;
        obj.tipo = dados.tipo;
        obj.dataTransacao = dados.dataTransacao;
        obj.conta = dados.conta;
        obj.categoria = dados.categoria;
        resolverRelacionamentos(obj);
        return obj;
    }

    @DELETE
    @Path("{id}")
    @Transactional
    public Response delete(@PathParam("id") Integer id) {
        if (!Transacao.deleteById(id)) throw new NotFoundException();
        return Response.noContent().build();
    }

    private void resolverRelacionamentos(Transacao t) {
        if (t.conta == null || t.conta.id == null || (t.conta = Conta.findById(t.conta.id)) == null)
            throw new BadRequestException("conta inexistente");
        if (t.categoria == null || t.categoria.id == null || (t.categoria = Categoria.findById(t.categoria.id)) == null)
            throw new BadRequestException("categoria inexistente");
    }
}


