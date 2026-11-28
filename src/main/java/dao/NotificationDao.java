package dao;

import db.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class NotificationDao {

    public static void creer(String destinataireType, String destinataireId,
                             String type, String message) throws SQLException {

        String sql = "INSERT INTO notification (destinataire_type, destinataire_id, type, message) "
                + "VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, destinataireType);
            ps.setString(2, destinataireId);
            ps.setString(3, type);
            ps.setString(4, message);
            ps.executeUpdate();
        }
    }
}