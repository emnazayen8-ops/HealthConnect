package rest;

import dao.Utilisateur;
import dao.UtilisateurDao;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import securite.JwtService;
import securite.MotDePasse;

import java.util.LinkedHashMap;
import java.util.Map;

@Path("/auth")
@Produces(MediaType.APPLICATION_JSON)
public class AuthResource {

    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response login(Map<String, Object> corps) throws Exception {

        String login = (corps == null) ? null : Reponses.texte(corps.get("login"));
        Object brut = (corps == null) ? null : corps.get("motDePasse");
        String motDePasse = (brut == null) ? null : brut.toString();

        if (login == null || motDePasse == null || motDePasse.isEmpty()) {
            return Reponses.erreur(400, "login et motDePasse sont obligatoires");
        }

        Utilisateur utilisateur = UtilisateurDao.trouverParLogin(login);

        if (utilisateur == null || !MotDePasse.verifier(motDePasse, utilisateur.motDePasse())) {
            return Reponses.erreur(401, "Identifiants incorrects");
        }

        Map<String, Object> reponse = new LinkedHashMap<>();
        reponse.put("jeton", JwtService.creer(utilisateur));
        reponse.put("type", "Bearer");
        reponse.put("expireDansSecondes", JwtService.DUREE_SECONDES);
        reponse.put("role", utilisateur.role());

        return Response.ok(reponse).build();
    }
}