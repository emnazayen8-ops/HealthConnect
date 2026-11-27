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

public class AnalyseXmlGenerator {

    public static String genererPourPrescription(String prescriptionId) throws Exception {
        Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        Element racine = doc.createElement("analyses");
        doc.appendChild(racine);

        String sql = "SELECT a.id, a.patient_id, a.prescription_id, l.nom AS laboratoire, "
                + "a.nom_test, a.date_analyse, a.valeur, a.unite, a.valeur_normale, "
                + "a.critique, a.statut "
                + "FROM analyse a JOIN laboratoire l ON l.id = a.laboratoire_id "
                + "WHERE a.prescription_id = ? ORDER BY a.id";

        int nombre = 0;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, prescriptionId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    nombre++;

                    Element analyse = doc.createElement("analyse");
                    analyse.setAttribute("id", rs.getString("id"));
                    racine.appendChild(analyse);

                    ajouter(doc, analyse, "patientId", rs.getString("patient_id"));
                    ajouter(doc, analyse, "prescriptionId", rs.getString("prescription_id"));
                    ajouter(doc, analyse, "laboratoire", rs.getString("laboratoire"));
                    ajouter(doc, analyse, "nomTest", rs.getString("nom_test"));
                    ajouterSiPresent(doc, analyse, "dateAnalyse", rs.getString("date_analyse"));
                    ajouterSiPresent(doc, analyse, "valeur", rs.getString("valeur"));
                    ajouterSiPresent(doc, analyse, "unite", rs.getString("unite"));
                    ajouterSiPresent(doc, analyse, "valeurNormale", rs.getString("valeur_normale"));
                    ajouter(doc, analyse, "critique", rs.getBoolean("critique") ? "true" : "false");
                    ajouter(doc, analyse, "statut", rs.getString("statut"));
                }
            }
        }

        if (nombre == 0) {
            return null;
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

    private static void ajouterSiPresent(Document doc, Element parent, String nom, String valeur) {
        if (valeur != null && !valeur.isBlank()) {
            ajouter(doc, parent, nom, valeur);
        }
    }
}