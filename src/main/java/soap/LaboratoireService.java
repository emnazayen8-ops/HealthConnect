package soap;

import dao.AnalyseDao;
import generation.AnalyseXmlGenerator;
import jakarta.jws.WebService;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import validation.XmlValidator;
import xpath.XPathService;

import java.util.ArrayList;
import java.util.List;

@WebService(endpointInterface = "soap.ILaboratoireService",
        serviceName = "LaboratoireService",
        portName = "LaboratoirePort",
        targetNamespace = "http://healthconnect.org/soap")
public class LaboratoireService implements ILaboratoireService {

    private static final String LABORATOIRE_ID = "LAB001";

    @Override
    public String envoyerResultatAnalyse(String analyseXml) {
        if (analyseXml == null || analyseXml.isBlank()) {
            return "REFUSE : document vide";
        }

        String resultat = XmlValidator.validerTexte(analyseXml, "xsd/analyse.xsd");

        if (resultat.equals("OK")) {
            return "ACCEPTE : document conforme au schema analyse.xsd";
        }
        return "REFUSE : " + resultat;
    }

    @Override
    public String demanderAnalyse(String prescriptionXml) {
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

            for (int i = 0; i < prescriptions.getLength(); i++) {
                Node prescription = prescriptions.item(i);
                String prescriptionId = xpath.lire(prescription, "@id");
                String patientId = xpath.lire(prescription, "patientId");

                NodeList analyses = xpath.chercher(doc,
                        "//prescription[@id='" + prescriptionId + "']/analysesDemandees/analyse");

                List<String> tests = new ArrayList<>();
                for (int j = 0; j < analyses.getLength(); j++) {
                    tests.add(analyses.item(j).getTextContent().trim());
                }

                if (!tests.isEmpty()) {
                    total += AnalyseDao.creerDemandes(prescriptionId, patientId, LABORATOIRE_ID, tests);
                }
            }

            return "ACCEPTE : " + total + " analyse(s) demandee(s)";

        } catch (Exception e) {
            return "ERREUR : " + e.getMessage();
        }
    }

    @Override
    public String obtenirResultatAnalyse(String prescriptionId) {
        if (prescriptionId == null || prescriptionId.isBlank()) {
            return "REFUSE : identifiant de prescription manquant";
        }

        try {
            String xml = AnalyseXmlGenerator.genererPourPrescription(prescriptionId.trim());

            if (xml == null) {
                return "AUCUN RESULTAT : prescription inconnue ou sans analyse";
            }

            String verification = XmlValidator.validerTexte(xml, "xsd/analyse.xsd");
            if (!verification.equals("OK")) {
                return "ERREUR : " + verification;
            }

            return xml;

        } catch (Exception e) {
            return "ERREUR : " + e.getMessage();
        }
    }
}