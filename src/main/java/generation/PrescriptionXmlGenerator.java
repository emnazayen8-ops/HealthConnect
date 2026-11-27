package generation;

import db.DatabaseConnection;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class PrescriptionXmlGenerator {

    public static String generer(String prescriptionId) throws Exception {
        String sql = "SELECT p.id, p.patient_id, "
                + "CONCAT('Dr ', m.prenom, ' ', m.nom) AS medecin, "
                + "p.consultation_id, p.date_prescription, p.analyses_demandees, "
                + "p.examens_demandes, p.medicaments, ph.nom AS pharmacie, p.statut "
                + "FROM prescription p "
                + "JOIN medecin m ON m.id = p.medecin_id "
                + "LEFT JOIN pharmacie ph ON ph.id = p.pharmacie_id "
                + "WHERE p.id = ?";

        Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        Element racine = doc.createElement("prescriptions");
        doc.appendChild(racine);

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, prescriptionId);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }

                Element prescription = doc.createElement("prescription");
                prescription.setAttribute("id", rs.getString("id"));
                racine.appendChild(prescription);

                ajouter(doc, prescription, "patientId", rs.getString("patient_id"));
                ajouter(doc, prescription, "medecin", rs.getString("medecin"));
                ajouter(doc, prescription, "consultationId", rs.getString("consultation_id"));
                ajouter(doc, prescription, "datePrescription", rs.getString("date_prescription"));

                ajouterListe(doc, prescription, "analysesDemandees", "analyse",
                        rs.getString("analyses_demandees"));
                ajouterListe(doc, prescription, "examensDemandes", "examen",
                        rs.getString("examens_demandes"));
                ajouterListe(doc, prescription, "medicaments", "medicament",
                        rs.getString("medicaments"));

                String pharmacie = rs.getString("pharmacie");
                if (pharmacie != null && !pharmacie.isBlank()) {
                    ajouter(doc, prescription, "pharmacie", pharmacie);
                }

                ajouter(doc, prescription, "statut", rs.getString("statut"));
            }
        }

        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");

        StringWriter sortie = new StringWriter();
        transformer.transform(new DOMSource(doc), new StreamResult(sortie));
        return sortie.toString();
    }

    private static void ajouter(Document doc, Element parent, String nom, String valeur) {
        Element element = doc.createElement(nom);
        element.setTextContent(valeur);
        parent.appendChild(element);
    }

    private static void ajouterListe(Document doc, Element parent, String conteneur,
                                     String enfant, String texte) {
        if (texte == null || texte.isBlank()) {
            return;
        }

        Element liste = doc.createElement(conteneur);
        int nombre = 0;

        for (String valeur : texte.split(",")) {
            if (!valeur.isBlank()) {
                ajouter(doc, liste, enfant, valeur.trim());
                nombre++;
            }
        }

        if (nombre > 0) {
            parent.appendChild(liste);
        }
    }
}