package securite;

import dao.JournalDao;
import jakarta.annotation.Priority;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.ext.Provider;
import rest.Reponses;

import java.util.Map;

@Provider
@Priority(Priorities.AUTHENTICATION)
public class FiltreSecurite implements ContainerRequestFilter {

    @Override
    public void filter(ContainerRequestContext ctx) {
        String chemin = ctx.getUriInfo().getPath();
        while (chemin.startsWith("/")) {
            chemin = chemin.substring(1);
        }
        String methode = ctx.getMethod();

        // Seule route publique : la connexion
        if (chemin.equals("auth/login")) {
            return;
        }

        // 1. AUTHENTIFICATION : le jeton est-il present et valide ?
        String entete = ctx.getHeaderString("Authorization");
        if (entete == null || !entete.startsWith("Bearer ")) {
            ctx.abortWith(Reponses.erreur(401, "Authentification requise : jeton manquant"));
            return;
        }

        Map<String, Object> claims = JwtService.verifier(entete.substring(7).trim());
        if (claims == null) {
            ctx.abortWith(Reponses.erreur(401, "Jeton invalide ou expire"));
            return;
        }

        // 2. AUTORISATION : le role a-t-il le droit ?
        String role = String.valueOf(claims.get("role"));
        String[] segments = chemin.split("/");
        boolean permis = autorise(role, methode, segments, claims, ctx);

        // 3. TRACABILITE : on note l'acces a un dossier patient
        String patientCible = (segments.length >= 2 && segments[0].equals("patients")) ? segments[1] : null;
        String action = (permis ? "" : "REFUSE ") + methode + " /" + chemin;
        journaliser(claims, patientCible, action);

        if (!permis) {
            ctx.abortWith(Reponses.erreur(403, "Acces interdit pour le role " + role));
        }
    }

    private boolean autorise(String role, String methode, String[] s,
                             Map<String, Object> claims, ContainerRequestContext ctx) {
        String ressource = s[0];

        switch (role) {
            case "PATIENT":
                if (!ressource.equals("patients") || !methode.equals("GET")) {
                    return false;
                }
                if (s.length < 2 || s.length > 3) {
                    return false;
                }
                if (s.length == 3 && !s[2].equals("analyses")) {
                    return false;
                }
                return s[1].equals(claims.get("patientId"));

            case "MEDECIN":
                if (ressource.equals("patients")) {
                    return methode.equals("GET") || methode.equals("PUT");
                }
                return ressource.equals("prescriptions") && methode.equals("POST");

            case "PHARMACIEN":
                if (!ressource.equals("prescriptions") || !methode.equals("GET")) {
                    return false;
                }
                String demandee = ctx.getUriInfo().getQueryParameters().getFirst("pharmacie");
                return demandee != null && demandee.equals(claims.get("pharmacieId"));

            case "ADMIN":
                return ressource.equals("journal") && methode.equals("GET");

            default:
                return false;
        }
    }

    private void journaliser(Map<String, Object> claims, String patientId, String action) {
        if (patientId == null) {
            return;
        }
        try {
            int utilisateurId = ((Number) claims.get("uid")).intValue();
            JournalDao.enregistrer(utilisateurId, patientId, action);
        } catch (Exception e) {
            System.out.println("[SECURITE] journal non enregistre : " + e.getMessage());
        }
    }
}