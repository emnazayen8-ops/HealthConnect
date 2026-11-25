package dao;

import db.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;

public class PatientDao {

    private static String prochainId(Connection conn) throws SQLException {
        String sql = "SELECT MAX(CAST(SUBSTRING(id, 2) AS UNSIGNED)) FROM patient";
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            int dernier = 0;
            if (rs.next()) {
                dernier = rs.getInt(1);
            }
            return String.format("P%03d", dernier + 1);
        }
    }

    public static String inscrire(String nom, String prenom, LocalDate dateNaissance,
                                  String sexe, String telephone, String antecedents)
            throws SQLException {

        String sql = "INSERT INTO patient "
                + "(id, nom, prenom, date_naissance, sexe, telephone, antecedents) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection()) {
            String id = prochainId(conn);

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, id);
                ps.setString(2, nom);
                ps.setString(3, prenom);
                ps.setDate(4, java.sql.Date.valueOf(dateNaissance));
                ps.setString(5, sexe);
                ps.setString(6, telephone.isBlank() ? null : telephone);
                ps.setString(7, antecedents.isBlank() ? null : antecedents);
                ps.executeUpdate();
            }
            return id;
        }
    }
}