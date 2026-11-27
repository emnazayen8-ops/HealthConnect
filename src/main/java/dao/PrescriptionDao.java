package dao;

import db.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class PrescriptionDao {

    private static String prochainId(Connection conn) throws SQLException {
        String sql = "SELECT MAX(CAST(SUBSTRING(id, 3) AS UNSIGNED)) FROM prescription";
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            int dernier = 0;
            if (rs.next()) {
                dernier = rs.getInt(1);
            }
            return String.format("PR%03d", dernier + 1);
        }
    }

    public static String creer(String consultationId, String analyses,
                               String examens, String medicaments) throws SQLException {

        String lecture = "SELECT patient_id, medecin_id FROM consultation WHERE id = ?";
        String ecriture = "INSERT INTO prescription "
                + "(id, patient_id, medecin_id, consultation_id, date_prescription, "
                + "analyses_demandees, examens_demandes, medicaments, statut) "
                + "VALUES (?, ?, ?, ?, CURDATE(), ?, ?, ?, 'creee')";

        try (Connection conn = DatabaseConnection.getConnection()) {

            String patientId;
            String medecinId;

            try (PreparedStatement ps = conn.prepareStatement(lecture)) {
                ps.setString(1, consultationId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        return null;
                    }
                    patientId = rs.getString("patient_id");
                    medecinId = rs.getString("medecin_id");
                }
            }

            String id = prochainId(conn);

            try (PreparedStatement ps = conn.prepareStatement(ecriture)) {
                ps.setString(1, id);
                ps.setString(2, patientId);
                ps.setString(3, medecinId);
                ps.setString(4, consultationId);
                ps.setString(5, vide(analyses));
                ps.setString(6, vide(examens));
                ps.setString(7, vide(medicaments));
                ps.executeUpdate();
            }
            return id;
        }
    }

    private static String vide(String texte) {
        return (texte == null || texte.isBlank()) ? null : texte.trim();
    }
}