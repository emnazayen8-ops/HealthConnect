package soap;

import dao.RadiologieDao;
import generation.RadiologieXmlGenerator;
import jakarta.jws.WebService;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import validation.XmlValidator;
import xpath.XPathService;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;

@WebService(endpointInterface = "soap.IRadiologieService",
        serviceName = "RadiologieService",
        portName = "RadiologiePort",
        targetNamespace = "http://healthconnect.org/soap")
public class RadiologieService implements IRadiologieService {

    private static final String CENTRE_ID = "RAD001";

    @Override
    public String envoyerCompteRendu(String compteRenduXml) {
        if (compteRenduXml == null || compteRenduXml.isBlank()) {
            return "REFUSE : document vide";
        }

        String resultat = XmlValidator.validerTexte(compteRenduXml, "xsd/radiologie.xsd");

        if (resultat.equals("OK")) {
            return "ACCEPTE : document conforme au schema radiologie.xsd";
        }
        return "REFUSE : " + resultat;
    }

    @Override
    public String demanderExamen(String prescriptionXml) {
        if (prescriptionXml == null || prescriptionXml.isBlank()) {
            return "REFUSE : document vide";
        }

        String verification = XmlValidator.validerTexte(prescriptionXml, "xsd/prescription.xsd");
        if (!verification.equals("OK")) {
            return "REFUSE : " + verification;
        }

        try {
            XPathService xpath = new XPathService();
            Document doc = xpath.chargerTexte(prescriptionXml);

            NodeList prescriptions = xpath.chercher(doc, "//prescription");
            int total = 0;
            List<String> ignores = new ArrayList<>();

            for (int i = 0; i < prescriptions.getLength(); i++) {
                Node prescription = prescriptions.item(i);
                String prescriptionId = xpath.lire(prescription, "@id");
                String patientId = xpath.lire(prescription, "patientId");

                NodeList examens = xpath.chercher(doc,
                        "//prescription[@id='" + prescriptionId + "']/examensDemandes/examen");

                for (int j = 0; j < examens.getLength(); j++) {
                    String examen = examens.item(j).getTextContent().trim();
                    String type = typeDe(examen);

                    if (type == null) {
                        ignores.add(examen);
                        continue;
                    }

                    RadiologieDao.creerDemande(prescriptionId, patientId, CENTRE_ID,
                            type, regionDe(examen));
                    total++;
                }
            }

            String reponse = "ACCEPTE : " + total + " examen(s) demande(s)";
            if (!ignores.isEmpty()) {
                reponse += " | type non reconnu, ignore(s) : " + ignores;
            }
            return reponse;

        } catch (Exception e) {
            return "ERREUR : " + e.getMessage();
        }
    }

    @Override
    public String obtenirCompteRendu(String prescriptionId) {
        if (prescriptionId == null || prescriptionId.isBlank()) {
            return "REFUSE : identifiant de prescription manquant";
        }

        try {
            String xml = RadiologieXmlGenerator.genererPourPrescription(prescriptionId.trim());

            if (xml == null) {
                return "AUCUN RESULTAT : prescription inconnue ou sans examen";
            }

            String verification = XmlValidator.validerTexte(xml, "xsd/radiologie.xsd");
            if (!verification.equals("OK")) {
                return "ERREUR : " + verification;
            }

            return xml;

        } catch (Exception e) {
            return "ERREUR : " + e.getMessage();
        }
    }

    private static String normaliser(String texte) {
        return Normalizer.normalize(texte, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase()
                .trim();
    }

    private static String typeDe(String examen) {
        String t = normaliser(examen);
        if (t.startsWith("radio")) return "Radio";
        if (t.startsWith("scanner")) return "Scanner";
        if (t.startsWith("irm")) return "IRM";
        if (t.startsWith("echo")) return "Echographie";
        return null;
    }

    private static String regionDe(String examen) {
        String[] mots = examen.trim().split("\\s+", 2);
        if (mots.length < 2) {
            return null;
        }
        String region = mots[1].replaceFirst("(?i)^(du|de la|de l'|des|de|d')\\s*", "").trim();
        return region.isEmpty() ? null : region;
    }
}