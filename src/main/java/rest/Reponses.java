package rest;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Reponses {

    public static Response erreur(int statut, String message) {
        return Response.status(statut)
                .type(MediaType.APPLICATION_JSON)
                .entity(Map.of("erreur", message))
                .build();
    }

    public static String texte(Object valeur) {
        if (valeur == null) {
            return null;
        }

        if (valeur instanceof List) {
            List<?> liste = (List<?>) valeur;
            List<String> morceaux = new ArrayList<>();
            for (Object element : liste) {
                morceaux.add(String.valueOf(element).trim());
            }
            String resultat = String.join(", ", morceaux);
            return resultat.isEmpty() ? null : resultat;
        }

        String texte = valeur.toString().trim();
        return texte.isEmpty() ? null : texte;
    }
}