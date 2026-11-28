package dao;

import db.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DossierDao {

    private static final String COLONNES_PATIENT =
            "id, nom, prenom, date_naissance AS dateNaissance, sexe, telephone, antecedents";

    public static List<Map<String, Object>> listerPatients() throws SQLException {
        String sql = "SELECT " + COLONNES_PATIENT + " FROM patient ORDER BY id";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            return lignes(rs);
        }
    }

    public static Map<String, Object> trouverPatient(String id) throws SQLException {
        String sqlPatient = "SELECT " + COLONNES_PATIENT + " FROM patient WHERE id = ?";
        String sqlConsultations = "SELECT c.id, c.date_consultation AS dateConsultation, "
                + "CONCAT('Dr ', m.prenom, ' ', m.nom) AS medecin, c.motif, c.diagnostic "
                + "FROM consultation c JOIN medecin m ON m.id = c.medecin_id "
                + "WHERE c.patient_id = ? ORDER BY c.date_consultation";

        try (Connection conn = DatabaseConnection.getConnection()) {

            Map<String, Object> patient;

            try (PreparedStatement ps = conn.prepareStatement(sqlPatient)) {
                ps.setString(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    List<Map<String, Object>> resultat = lignes(rs);
                    if (resultat.isEmpty()) {
                        return null;
                    }
                    patient = resultat.get(0);
                }
            }

            try (PreparedStatement ps = conn.prepareStatement(sqlConsultations)) {
                ps.setString(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    patient.put("consultations", lignes(rs));
                }
            }

            return patient;
        }
    }

    public static List<Map<String, Object>> analysesDuPatient(String patientId) throws SQLException {
        String sql = "SELECT a.id, a.prescription_id AS prescriptionId, l.nom AS laboratoire, "
                + "a.nom_test AS nomTest, a.date_analyse AS dateAnalyse, a.valeur, a.unite, "
                + "a.valeur_normale AS valeurNormale, a.critique, a.statut "
                + "FROM analyse a JOIN laboratoire l ON l.id = a.laboratoire_id "
                + "WHERE a.patient_id = ? ORDER BY a.id";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, patientId);
            try (ResultSet rs = ps.executeQuery()) {
                return lignes(rs);
            }
        }
    }

    public static List<Map<String, Object>> prescriptionsPourPharmacie(String pharmacieId)
            throws SQLException {
        String sql = "SELECT id, patient_id AS patientId, date_prescription AS datePrescription, "
                + "medicaments, statut FROM prescription "
                + "WHERE pharmacie_id = ? AND medicaments IS NOT NULL ORDER BY id";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, pharmacieId);
            try (ResultSet rs = ps.executeQuery()) {
                return lignes(rs);
            }
        }
    }

    public static void modifierPatient(String id, String nom, String prenom,
                                       LocalDate dateNaissance, String sexe,
                                       String telephone, String antecedents) throws SQLException {
        String sql = "UPDATE patient SET nom = ?, prenom = ?, date_naissance = ?, "
                + "sexe = ?, telephone = ?, antecedents = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nom);
            ps.setString(2, prenom);
            ps.setDate(3, java.sql.Date.valueOf(dateNaissance));
            ps.setString(4, sexe);
            ps.setString(5, telephone);
            ps.setString(6, antecedents);
            ps.setString(7, id);
            ps.executeUpdate();
        }
    }

    private static List<Map<String, Object>> lignes(ResultSet rs) throws SQLException {
        ResultSetMetaData meta = rs.getMetaData();
        int colonnes = meta.getColumnCount();
        List<Map<String, Object>> liste = new ArrayList<>();

        while (rs.next()) {
            Map<String, Object> ligne = new LinkedHashMap<>();
            for (int i = 1; i <= colonnes; i++) {
                Object valeur = rs.getObject(i);
                if (valeur instanceof java.sql.Date
                        || valeur instanceof java.sql.Timestamp
                        || valeur instanceof TemporalAccessor) {
                    valeur = valeur.toString();
                }
                ligne.put(meta.getColumnLabel(i), valeur);
            }
            liste.add(ligne);
        }
        return liste;
    }
}