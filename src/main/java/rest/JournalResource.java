package rest;

import dao.JournalDao;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.sql.SQLException;

@Path("/journal")
@Produces(MediaType.APPLICATION_JSON)
public class JournalResource {

    @GET
    public Response lister() throws SQLException {
        return Response.ok(JournalDao.derniers(50)).build();
    }
}