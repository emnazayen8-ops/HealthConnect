package integration;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class AdaptateurLaboratoireCsv {

    private static final DateTimeFormatter FORMAT_ANCIEN = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public static String versXml(String ligneCsv) throws Exception {
        String[] c = ligneCsv.split(";", -1);

        if (c.length != 10) {
            throw new IllegalArgumentException(
                    "Ligne CSV invalide : 10 colonnes attendues, " + c.length + " recues");
        }

        String dateIso = LocalDate.parse(c[5].trim(), FORMAT_ANCIEN).toString();
        String valeur = c[6].trim().replace(',', '.');
        String critique = c[9].trim().equals("1") ? "true" : "false";

        Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        Element racine = doc.createElement("analyses");
        doc.appendChild(racine);

        Element analyse = doc.createElement("analyse");
        analyse.setAttribute("id", c[0].trim());
        racine.appendChild(analyse);

        ajouter(doc, analyse, "patientId", c[1].trim());
        ajouter(doc, analyse, "prescriptionId", c[2].trim());
        ajouter(doc, analyse, "laboratoire", c[3].trim());
        ajouter(doc, analyse, "nomTest", c[4].trim());
        ajouter(doc, analyse, "dateAnalyse", dateIso);
        ajouter(doc, analyse, "valeur", valeur);
        ajouter(doc, analyse, "unite", c[7].trim());
        ajouter(doc, analyse, "valeurNormale", c[8].trim());
        ajouter(doc, analyse, "critique", critique);
        ajouter(doc, analyse, "statut", "validee");

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
}