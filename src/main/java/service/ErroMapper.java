package service;

import dto.Erro;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.jboss.resteasy.reactive.server.ServerExceptionMapper;

public class ErroMapper {
    @ServerExceptionMapper
    public Response mapear(ApiException e) {
        return Response.status(e.status())
                .type(MediaType.APPLICATION_JSON)
                .entity(new Erro(e.getMessage()))
                .build();
    }
}
