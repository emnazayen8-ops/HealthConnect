package dao;

import db.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UtilisateurDao {

    public static Utilisateur trouverParLogin(String login) throws SQLException {
        String sql = "SELECT id, login, mot_de_passe, role, patient_id, medecin_id, pharmacie_id "
                + "FROM utilisateur WHERE login = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, login);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                return new Utilisateur(rs.getInt("id"), rs.getString("login"),
                        rs.getString("mot_de_passe"), rs.getString("role"),
                        rs.getString("patient_id"), rs.getString("medecin_id"),
                        rs.getString("pharmacie_id"));
            }
        }
    }

    public static void creer(String login, String motDePasseHache, String role,
                             String patientId, String medecinId, String pharmacieId)
            throws SQLException {

        String sql = "INSERT INTO utilisateur "
                + "(login, mot_de_passe, role, patient_id, medecin_id, pharmacie_id) "
                + "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, login);
            ps.setString(2, motDePasseHache);
            ps.setString(3, role);
            ps.setString(4, patientId);
            ps.setString(5, medecinId);
            ps.setString(6, pharmacieId);
            ps.executeUpdate();
        }
    }
}