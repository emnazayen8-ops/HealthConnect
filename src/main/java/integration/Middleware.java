package integration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import xpath.XPathService;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class Middleware {

    private final List<String> journal = new ArrayList<>();

    public List<String> getJournal() {
        return journal;
    }

    private void log(String etape, String detail) {
        String ligne = LocalTime.now().withNano(0) + " [" + etape + "] " + detail;
        journal.add(ligne);
        System.out.println(ligne);
    }

    // Cas 1 : un systeme ancien du laboratoire envoie une ligne CSV
    public void integrerResultatLaboratoire(String ligneCsv) throws Exception {
        log("RECEPTION", "ligne CSV venant du systeme du laboratoire");

        String xml = AdaptateurLaboratoireCsv.versXml(ligneCsv);
        log("ADAPTATION", "CSV transforme en analyse.xml (date, virgule et booleen harmonises)");

        String reponse = ClientsSoap.laboratoire().envoyerResultatAnalyse(xml);
        log("VALIDATION", "Service Laboratoire : " + reponse);
    }

    // Cas 2 : la clinique cree une prescription, le middleware la distribue
    public void acheminerPrescription(String prescriptionId) throws Exception {
        log("RECEPTION", "prescription " + prescriptionId + " a acheminer");

        String xml = ClientsSoap.clinique().obtenirPrescription(prescriptionId);

        if (!xml.trim().startsWith("<")) {
            log("ERREUR", xml);
            return;
        }
        log("VALIDATION", "prescription.xml fournie par la Clinique, conforme au XSD");

        XPathService xpath = new XPathService();
        Document doc = xpath.chargerTexte(xml);

        int analyses = xpath.chercher(doc, "//analysesDemandees/analyse").getLength();
        int examens = xpath.chercher(doc, "//examensDemandes/examen").getLength();
        int medicaments = xpath.chercher(doc, "//medicaments/medicament").getLength();

        if (analyses > 0) {
            String extrait = extrait(xml, "examensDemandes", "medicaments", "pharmacie");
            log("ROUTAGE", analyses + " analyse(s) -> service Laboratoire (sans examens ni medicaments)");
            log("REPONSE", "Laboratoire : " + ClientsSoap.laboratoire().demanderAnalyse(extrait));
        }

        if (examens > 0) {
            String extrait = extrait(xml, "analysesDemandees", "medicaments", "pharmacie");
            log("ROUTAGE", examens + " examen(s) -> service Radiologie (sans analyses ni medicaments)");
            log("REPONSE", "Radiologie : " + ClientsSoap.radiologie().demanderExamen(extrait));
        }

        if (medicaments > 0) {
            log("ROUTAGE", medicaments + " medicament(s) -> mis a disposition de la pharmacie (API REST)");
        }

        if (analyses == 0 && examens == 0 && medicaments == 0) {
            log("INFO", "rien a acheminer");
        }
    }

    // Cas 3 : le medecin veut les resultats, le middleware les rassemble en JSON
    public String recupererResultats(String prescriptionId) throws Exception {
        log("DEMANDE", "resultats de la prescription " + prescriptionId);

        String analyses = ClientsSoap.laboratoire().obtenirResultatAnalyse(prescriptionId);
        String radiologie = ClientsSoap.radiologie().obtenirCompteRendu(prescriptionId);

        ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
        ObjectNode dossier = mapper.createObjectNode();
        dossier.put("prescriptionId", prescriptionId);

        ajouterResultat(mapper, dossier, "analyses", analyses);
        ajouterResultat(mapper, dossier, "comptesRendus", radiologie);

        return mapper.writeValueAsString(dossier);
    }

    private void ajouterResultat(ObjectMapper mapper, ObjectNode cible, String nom, String xml)
            throws Exception {
        if (xml == null || !xml.trim().startsWith("<")) {
            log("INFO", nom + " : " + xml);
            cible.put(nom, "aucun resultat");
            return;
        }

        JsonNode json = mapper.readTree(XmlVersJson.convertir(xml));
        cible.set(nom, json.get(nom));
        log("TRANSFORMATION", nom + " : XML transforme en JSON");
    }

    // Minimisation : ne garder que ce dont le destinataire a besoin
    private String extrait(String xml, String... elementsASupprimer) throws Exception {
        XPathService xpath = new XPathService();
        Document doc = xpath.chargerTexte(xml);

        for (String nom : elementsASupprimer) {
            NodeList noeuds = xpath.chercher(doc, "//prescription/" + nom);
            for (int i = noeuds.getLength() - 1; i >= 0; i--) {
                Node noeud = noeuds.item(i);
                noeud.getParentNode().removeChild(noeud);
            }
        }

        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");

        StringWriter sortie = new StringWriter();
        transformer.transform(new DOMSource(doc), new StreamResult(sortie));
        return sortie.toString();
    }
}