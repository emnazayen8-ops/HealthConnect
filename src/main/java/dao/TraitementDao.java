package dao;

import db.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TraitementDao {

    public static List<String> preparer(String patientId, String pharmacieId) throws SQLException {

        String lecture = "SELECT id, medicaments FROM prescription "
                + "WHERE patient_id = ? AND medicaments IS NOT NULL "
                + "AND statut IN ('creee', 'envoyee') "
                + "AND (pharmacie_id IS NULL OR pharmacie_id = ?)";

        String ecriture = "UPDATE prescription SET statut = 'preparee', pharmacie_id = ? WHERE id = ?";

        List<String[]> trouvees = new ArrayList<>();
        List<String> prepares = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection()) {

            try (PreparedStatement ps = conn.prepareStatement(lecture)) {
                ps.setString(1, patientId);
                ps.setString(2, pharmacieId);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        trouvees.add(new String[]{rs.getString("id"), rs.getString("medicaments")});
                    }
                }
            }

            for (String[] t : trouvees) {
                try (PreparedStatement ps = conn.prepareStatement(ecriture)) {
                    ps.setString(1, pharmacieId);
                    ps.setString(2, t[0]);
                    ps.executeUpdate();
                }
                prepares.add(t[0] + " : " + t[1]);
            }
        }
        return prepares;
    }
}