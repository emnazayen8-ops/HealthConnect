package rest;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Map;

@Path("/patients")
@Produces(MediaType.APPLICATION_JSON)
public class PatientResource {

    @GET
    public Response lister() throws SQLException {
        return Response.ok(DossierDao.listerPatients()).build();
    }

    @GET
    @Path("/{id}")
    public Response trouver(@PathParam("id") String id) throws SQLException {
        Map<String, Object> patient = DossierDao.trouverPatient(id);

        if (patient == null) {
            return Reponses.erreur(404, "Patient introuvable : " + id);
        }
        return Response.ok(patient).build();
    }

    @GET
    @Path("/{id}/analyses")
    public Response analyses(@PathParam("id") String id) throws SQLException {
        if (DossierDao.trouverPatient(id) == null) {
            return Reponses.erreur(404, "Patient introuvable : " + id);
        }
        return Response.ok(DossierDao.analysesDuPatient(id)).build();
    }

    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response modifier(@PathParam("id") String id, Map<String, Object> corps)
            throws SQLException {

        if (corps == null) {
            return Reponses.erreur(400, "Corps JSON manquant");
        }
        if (DossierDao.trouverPatient(id) == null) {
            return Reponses.erreur(404, "Patient introuvable : " + id);
        }

        String nom = Reponses.texte(corps.get("nom"));
        String prenom = Reponses.texte(corps.get("prenom"));
        String sexe = Reponses.texte(corps.get("sexe"));
        String dateTexte = Reponses.texte(corps.get("dateNaissance"));

        if (nom == null || prenom == null) {
            return Reponses.erreur(400, "nom et prenom sont obligatoires");
        }

        sexe = (sexe == null) ? "" : sexe.toUpperCase();
        if (!sexe.equals("M") && !sexe.equals("F")) {
            return Reponses.erreur(400, "sexe doit etre M ou F");
        }

        LocalDate dateNaissance;
        try {
            dateNaissance = LocalDate.parse(dateTexte == null ? "" : dateTexte);
        } catch (DateTimeParseException e) {
            return Reponses.erreur(400, "dateNaissance invalide, format attendu 2001-07-14");
        }

        DossierDao.modifierPatient(id, nom, prenom, dateNaissance, sexe,
                Reponses.texte(corps.get("telephone")),
                Reponses.texte(corps.get("antecedents")));

        return Response.ok(DossierDao.trouverPatient(id)).build();
    }
}