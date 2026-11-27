package dao;

import db.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class AnalyseDao {

    private static String prochainId(Connection conn) throws SQLException {
        String sql = "SELECT MAX(CAST(SUBSTRING(id, 2) AS UNSIGNED)) FROM analyse";
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            int dernier = 0;
            if (rs.next()) {
                dernier = rs.getInt(1);
            }
            return String.format("A%03d", dernier + 1);
        }
    }

    public static int creerDemandes(String prescriptionId, String patientId,
                                    String laboratoireId, List<String> tests)
            throws SQLException {

        String sql = "INSERT INTO analyse "
                + "(id, patient_id, prescription_id, laboratoire_id, nom_test, critique, statut) "
                + "VALUES (?, ?, ?, ?, ?, FALSE, 'demandee')";

        int compteur = 0;
        try (Connection conn = DatabaseConnection.getConnection()) {
            for (String test : tests) {
                String id = prochainId(conn);
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setString(1, id);
                    ps.setString(2, patientId);
                    ps.setString(3, prescriptionId);
                    ps.setString(4, laboratoireId);
                    ps.setString(5, test);
                    ps.executeUpdate();
                }
                compteur++;
            }
        }
        return compteur;
    }
}