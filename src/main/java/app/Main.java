package app;

import db.DatabaseConnection;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Main {

    public static void main(String[] args) {
        String sql = "SELECT id, nom, prenom, antecedents FROM patient";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            System.out.println("Connexion réussie !");

            while (rs.next()) {
                System.out.println(rs.getString("id") + " - "
                        + rs.getString("prenom") + " "
                        + rs.getString("nom")
                        + " | Antécédents : " + rs.getString("antecedents"));
            }

        } catch (SQLException e) {
            System.out.println("Erreur : " + e.getMessage());
        }
    }
}