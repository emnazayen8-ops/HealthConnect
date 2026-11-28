package rest;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import java.util.Map;

@Provider
public class ErreurMapper implements ExceptionMapper<Exception> {

    @Override
    public Response toResponse(Exception e) {
        if (e instanceof WebApplicationException) {
            WebApplicationException w = (WebApplicationException) e;
            return Response.fromResponse(w.getResponse())
                    .type(MediaType.APPLICATION_JSON)
                    .entity(Map.of("erreur", String.valueOf(w.getMessage())))
                    .build();
        }
        return Reponses.erreur(500, "Erreur serveur : " + e.getMessage());
    }
}