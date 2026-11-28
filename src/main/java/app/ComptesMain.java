package app;

import dao.UtilisateurDao;
import securite.MotDePasse;

public class ComptesMain {

    public static void main(String[] args) {
        creer("ahmed.benali", "patient123", "PATIENT", "P001", null, null);
        creer("salma.gharbi", "patient123", "PATIENT", "P002", null, null);
        creer("dr.trabelsi", "medecin123", "MEDECIN", null, "MED001", null);
        creer("dr.karray", "medecin123", "MEDECIN", null, "MED002", null);
        creer("pharmacie.centrale", "pharma123", "PHARMACIEN", null, null, "PH001");
        creer("pharmacie.elamel", "pharma123", "PHARMACIEN", null, null, "PH002");
        creer("admin", "admin123", "ADMIN", null, null, null);
    }

    private static void creer(String login, String motDePasse, String role,
                              String patientId, String medecinId, String pharmacieId) {
        try {
            if (UtilisateurDao.trouverParLogin(login) != null) {
                System.out.println("Existe deja : " + login);
                return;
            }
            UtilisateurDao.creer(login, MotDePasse.hasher(motDePasse), role,
                    patientId, medecinId, pharmacieId);
            System.out.println("Compte cree : " + login + " (" + role + ")");
        } catch (Exception e) {
            System.out.println("Erreur pour " + login + " : " + e.getMessage());
        }
    }
}