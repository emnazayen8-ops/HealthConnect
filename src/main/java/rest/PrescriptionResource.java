package rest;

import dao.PrescriptionDao;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

@Path("/prescriptions")
@Produces(MediaType.APPLICATION_JSON)
public class PrescriptionResource {

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response creer(Map<String, Object> corps) throws SQLException {

        if (corps == null) {
            return Reponses.erreur(400, "Corps JSON manquant");
        }

        String consultationId = Reponses.texte(corps.get("consultationId"));
        String analyses = Reponses.texte(corps.get("analysesDemandees"));
        String examens = Reponses.texte(corps.get("examensDemandes"));
        String medicaments = Reponses.texte(corps.get("medicaments"));

        if (consultationId == null) {
            return Reponses.erreur(400, "consultationId est obligatoire");
        }
        if (analyses == null && examens == null && medicaments == null) {
            return Reponses.erreur(400, "la prescription est vide");
        }

        String id = PrescriptionDao.creer(consultationId, analyses, examens, medicaments);

        if (id == null) {
            return Reponses.erreur(404, "Consultation introuvable : " + consultationId);
        }

        Map<String, Object> reponse = new LinkedHashMap<>();
        reponse.put("id", id);
        reponse.put("consultationId", consultationId);
        reponse.put("statut", "creee");

        return Response.status(201).entity(reponse).build();
    }

    @GET
    public Response pourPharmacie(@QueryParam("pharmacie") String pharmacieId) throws SQLException {
        if (pharmacieId == null || pharmacieId.isBlank()) {
            return Reponses.erreur(400, "le parametre pharmacie est obligatoire");
        }
        return Response.ok(DossierDao.prescriptionsPourPharmacie(pharmacieId.trim())).build();
    }
}
