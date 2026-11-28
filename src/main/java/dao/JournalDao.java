package dao;

import db.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class JournalDao {

    public static void enregistrer(int utilisateurId, String patientId, String action)
            throws SQLException {

        String sql = "INSERT INTO journal_acces (utilisateur_id, patient_id, action) VALUES (?, ?, ?)";
        String texte = action.length() > 50 ? action.substring(0, 50) : action;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, utilisateurId);
            ps.setString(2, patientId);
            ps.setString(3, texte);
            ps.executeUpdate();
        }
    }

    public static List<Map<String, Object>> derniers(int limite) throws SQLException {
        String sql = "SELECT j.id, u.login, u.role, j.patient_id, j.action, j.date_action "
                + "FROM journal_acces j JOIN utilisateur u ON u.id = j.utilisateur_id "
                + "ORDER BY j.id DESC LIMIT ?";

        List<Map<String, Object>> liste = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, limite);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> ligne = new LinkedHashMap<>();
                    ligne.put("id", rs.getInt("id"));
                    ligne.put("login", rs.getString("login"));
                    ligne.put("role", rs.getString("role"));
                    ligne.put("patientId", rs.getString("patient_id"));
                    ligne.put("action", rs.getString("action"));
                    ligne.put("dateAction", rs.getString("date_action"));
                    liste.add(ligne);
                }
            }
        }
        return liste;
    }
}