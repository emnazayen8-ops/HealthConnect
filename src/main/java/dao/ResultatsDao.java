package dao;

import db.DatabaseConnection;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ResultatsDao {

    public static int saisirAnalyse(String prescriptionId, String nomTest, String valeur,
                                    String unite, String valeurNormale, boolean critique)
            throws SQLException {

        String sql = "UPDATE analyse SET date_analyse = CURDATE(), valeur = ?, unite = ?, "
                + "valeur_normale = ?, critique = ?, statut = 'realisee' "
                + "WHERE prescription_id = ? AND nom_test = ? AND statut = 'demandee'";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBigDecimal(1, new BigDecimal(valeur));
            ps.setString(2, unite);
            ps.setString(3, valeurNormale);
            ps.setBoolean(4, critique);
            ps.setString(5, prescriptionId);
            ps.setString(6, nomTest);
            return ps.executeUpdate();
        }
    }

    public static int saisirCompteRendu(String prescriptionId, String typeExamen,
                                        String description, String conclusion, String radiologue)
            throws SQLException {

        String sql = "UPDATE radiologie SET date_examen = CURDATE(), description = ?, "
                + "conclusion = ?, radiologue = ?, statut = 'realisee' "
                + "WHERE prescription_id = ? AND type_examen = ? AND statut = 'demandee'";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, description);
            ps.setString(2, conclusion);
            ps.setString(3, radiologue);
            ps.setString(4, prescriptionId);
            ps.setString(5, typeExamen);
            return ps.executeUpdate();
        }
    }

    public static int changerStatutAnalyses(String prescriptionId, String de, String vers)
            throws SQLException {
        return changerStatut("analyse", prescriptionId, de, vers);
    }

    public static int changerStatutExamens(String prescriptionId, String de, String vers)
            throws SQLException {
        return changerStatut("radiologie", prescriptionId, de, vers);
    }

    private static int changerStatut(String table, String prescriptionId, String de, String vers)
            throws SQLException {

        String sql = "UPDATE " + table + " SET statut = ? WHERE prescription_id = ? AND statut = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, vers);
            ps.setString(2, prescriptionId);
            ps.setString(3, de);
            return ps.executeUpdate();
        }
    }
}