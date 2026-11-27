package dao;

import db.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class RadiologieDao {

    private static String prochainId(Connection conn) throws SQLException {
        String sql = "SELECT MAX(CAST(SUBSTRING(id, 2) AS UNSIGNED)) FROM radiologie";
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            int dernier = 0;
            if (rs.next()) {
                dernier = rs.getInt(1);
            }
            return String.format("R%03d", dernier + 1);
        }
    }

    public static String creerDemande(String prescriptionId, String patientId, String centreId,
                                      String typeExamen, String region) throws SQLException {

        String sql = "INSERT INTO radiologie "
                + "(id, patient_id, prescription_id, centre_id, type_examen, region_examinee, statut) "
                + "VALUES (?, ?, ?, ?, ?, ?, 'demandee')";

        try (Connection conn = DatabaseConnection.getConnection()) {
            String id = prochainId(conn);

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, id);
                ps.setString(2, patientId);
                ps.setString(3, prescriptionId);
                ps.setString(4, centreId);
                ps.setString(5, typeExamen);
                ps.setString(6, region);
                ps.executeUpdate();
            }
            return id;
        }
    }
}