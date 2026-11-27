package dao;

import db.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ConsultationDao {

    private static String prochainId(Connection conn) throws SQLException {
        String sql = "SELECT MAX(CAST(SUBSTRING(id, 2) AS UNSIGNED)) FROM consultation";
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            int dernier = 0;
            if (rs.next()) {
                dernier = rs.getInt(1);
            }
            return String.format("C%03d", dernier + 1);
        }
    }

    public static String creer(String patientId, String medecinId, String motif) throws SQLException {
        String sql = "INSERT INTO consultation "
                + "(id, patient_id, medecin_id, date_consultation, motif) "
                + "VALUES (?, ?, ?, CURDATE(), ?)";

        try (Connection conn = DatabaseConnection.getConnection()) {
            String id = prochainId(conn);

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, id);
                ps.setString(2, patientId);
                ps.setString(3, medecinId);
                ps.setString(4, motif);
                ps.executeUpdate();
            }
            return id;
        }
    }

    public static boolean enregistrerDiagnostic(String consultationId, String diagnostic)
            throws SQLException {
        String sql = "UPDATE consultation SET diagnostic = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, diagnostic);
            ps.setString(2, consultationId);
            return ps.executeUpdate() > 0;
        }
    }
}