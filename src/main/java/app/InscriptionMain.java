package app;

import dao.PatientDao;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

public class InscriptionMain {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.println("=== Inscription d'un nouveau patient ===");

        System.out.print("Nom : ");
        String nom = sc.nextLine().trim();

        System.out.print("Prénom : ");
        String prenom = sc.nextLine().trim();

        System.out.print("Date de naissance (AAAA-MM-JJ) : ");
        String dateTexte = sc.nextLine().trim();

        System.out.print("Sexe (M ou F) : ");
        String sexe = sc.nextLine().trim().toUpperCase();

        System.out.print("Téléphone (facultatif) : ");
        String telephone = sc.nextLine().trim();

        System.out.print("Antécédents, séparés par des virgules (facultatif) : ");
        String antecedents = sc.nextLine().trim();

        if (nom.isEmpty() || prenom.isEmpty()) {
            System.out.println("Le nom et le prénom sont obligatoires.");
            return;
        }

        if (!sexe.equals("M") && !sexe.equals("F")) {
            System.out.println("Le sexe doit être M ou F.");
            return;
        }

        LocalDate dateNaissance;
        try {
            dateNaissance = LocalDate.parse(dateTexte);
        } catch (DateTimeParseException e) {
            System.out.println("Date invalide. Format attendu : 2001-07-14");
            return;
        }

        try {
            String id = PatientDao.inscrire(nom, prenom, dateNaissance, sexe, telephone, antecedents);
            System.out.println("Patient inscrit avec l'identifiant : " + id);
        } catch (SQLException e) {
            System.out.println("Erreur lors de l'inscription : " + e.getMessage());
        }
    }
}