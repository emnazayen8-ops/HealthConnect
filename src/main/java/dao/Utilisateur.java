package dao;

public record Utilisateur(int id, String login, String motDePasse, String role,
                          String patientId, String medecinId, String pharmacieId) {
}