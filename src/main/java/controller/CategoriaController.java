package controller;

import entity.Categoria;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("categoria")
public class CategoriaController {
    @GET
    public List<Categoria> todas() {
        return Categoria.listAll();
    }

    @GET
    @Path("{id}")
    public Categoria buscar(@PathParam("id") Integer id) {
        Categoria obj = Categoria.findById(id);
        if (obj == null) throw new NotFoundException();
        return obj;
    }

    @POST
    @Transactional
    public Response save(Categoria obj) {
        obj.id = null;
        obj.persist();
        return Response.status(Response.Status.CREATED).entity(obj).build();
    }

    @PUT
    @Path("{id}")
    @Transactional
    public Categoria update(@PathParam("id") Integer id, Categoria dados) {
        Categoria obj = Categoria.findById(id);
        if (obj == null) throw new NotFoundException();
        obj.descricao = dados.descricao;
        return obj;
    }

    @DELETE
    @Path("{id}")
    @Transactional
    public Response delete(@PathParam("id") Integer id) {
        if (!Categoria.deleteById(id)) throw new NotFoundException();
        return Response.noContent().build();
    }
}
