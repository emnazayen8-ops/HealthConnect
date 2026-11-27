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

public class RadiologieXmlGenerator {

    public static String genererPourPrescription(String prescriptionId) throws Exception {
        Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        Element racine = doc.createElement("comptesRendus");
        doc.appendChild(racine);

        String sql = "SELECT r.id, r.patient_id, r.prescription_id, c.nom AS centre, "
                + "r.type_examen, r.region_examinee, r.date_examen, r.description, "
                + "r.conclusion, r.radiologue, r.statut "
                + "FROM radiologie r JOIN centre_radiologie c ON c.id = r.centre_id "
                + "WHERE r.prescription_id = ? ORDER BY r.id";

        int nombre = 0;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, prescriptionId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    nombre++;

                    Element cr = doc.createElement("compteRendu");
                    cr.setAttribute("id", rs.getString("id"));
                    racine.appendChild(cr);

                    ajouter(doc, cr, "patientId", rs.getString("patient_id"));
                    ajouter(doc, cr, "prescriptionId", rs.getString("prescription_id"));
                    ajouter(doc, cr, "centre", rs.getString("centre"));
                    ajouter(doc, cr, "typeExamen", rs.getString("type_examen"));
                    ajouterSiPresent(doc, cr, "regionExaminee", rs.getString("region_examinee"));
                    ajouterSiPresent(doc, cr, "dateExamen", rs.getString("date_examen"));
                    ajouterSiPresent(doc, cr, "description", rs.getString("description"));
                    ajouterSiPresent(doc, cr, "conclusion", rs.getString("conclusion"));
                    ajouterSiPresent(doc, cr, "radiologue", rs.getString("radiologue"));
                    ajouter(doc, cr, "statut", rs.getString("statut"));
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