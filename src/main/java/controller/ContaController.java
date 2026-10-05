package controller;

import entity.Conta;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("conta")
public class ContaController {
    @GET
    public List<Conta> todas() {
        return Conta.listAll();
    }

    @GET
    @Path("{id}")
    public Conta buscar(@PathParam("id") Integer id) {
        Conta obj = Conta.findById(id);
        if (obj == null) throw new NotFoundException();
        return obj;
    }

    @POST
    @Transactional
    public Response save(Conta obj) {
        obj.id = null;
        obj.persist();
        return Response.status(Response.Status.CREATED).entity(obj).build();
    }

    @PUT
    @Path("{id}")
    @Transactional
    public Conta update(@PathParam("id") Integer id, Conta dados) {
        Conta obj = Conta.findById(id);
        if (obj == null) throw new NotFoundException();
        obj.descricao = dados.descricao;
        return obj;
    }

    @DELETE
    @Path("{id}")
    @Transactional
    public Response delete(@PathParam("id") Integer id) {
        if (!Conta.deleteById(id)) throw new NotFoundException();
        return Response.noContent().build();
    }
}
