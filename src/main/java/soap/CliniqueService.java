package soap;

import dao.ConsultationDao;
import dao.PatientDao;
import dao.PrescriptionDao;
import generation.PrescriptionXmlGenerator;
import jakarta.jws.WebService;
import validation.XmlValidator;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

@WebService(endpointInterface = "soap.ICliniqueService",
        serviceName = "CliniqueService",
        portName = "CliniquePort",
        targetNamespace = "http://healthconnect.org/soap")
public class CliniqueService implements ICliniqueService {

    @Override
    public String inscrirePatient(String nom, String prenom, String dateNaissance,
                                  String sexe, String telephone, String antecedents) {

        if (vide(nom) || vide(prenom)) {
            return "REFUSE : le nom et le prenom sont obligatoires";
        }

        String s = (sexe == null) ? "" : sexe.trim().toUpperCase();
        if (!s.equals("M") && !s.equals("F")) {
            return "REFUSE : le sexe doit etre M ou F";
        }

        LocalDate date;
        try {
            date = LocalDate.parse(dateNaissance == null ? "" : dateNaissance.trim());
        } catch (DateTimeParseException e) {
            return "REFUSE : date invalide, format attendu 2001-07-14";
        }

        try {
            String id = PatientDao.inscrire(nom.trim(), prenom.trim(), date, s,
                    telephone == null ? "" : telephone.trim(),
                    antecedents == null ? "" : antecedents.trim());
            return "ACCEPTE : patient inscrit avec l'identifiant " + id;
        } catch (Exception e) {
            return "ERREUR : " + e.getMessage();
        }
    }

    @Override
    public String enregistrerConsultation(String patientId, String medecinId, String motif) {
        if (vide(patientId) || vide(medecinId) || vide(motif)) {
            return "REFUSE : patientId, medecinId et motif sont obligatoires";
        }

        try {
            String id = ConsultationDao.creer(patientId.trim(), medecinId.trim(), motif.trim());
            return "ACCEPTE : consultation creee avec l'identifiant " + id;
        } catch (Exception e) {
            return "ERREUR : " + e.getMessage();
        }
    }

    @Override
    public String creerPrescription(String consultationId, String analysesDemandees,
                                    String examensDemandes, String medicaments) {
        if (vide(consultationId)) {
            return "REFUSE : consultationId obligatoire";
        }
        if (vide(analysesDemandees) && vide(examensDemandes) && vide(medicaments)) {
            return "REFUSE : la prescription est vide";
        }

        try {
            String id = PrescriptionDao.creer(consultationId.trim(), analysesDemandees,
                    examensDemandes, medicaments);

            if (id == null) {
                return "REFUSE : consultation inconnue";
            }
            return "ACCEPTE : prescription creee avec l'identifiant " + id;
        } catch (Exception e) {
            return "ERREUR : " + e.getMessage();
        }
    }

    @Override
    public String enregistrerDiagnostic(String consultationId, String diagnostic) {
        if (vide(consultationId) || vide(diagnostic)) {
            return "REFUSE : consultationId et diagnostic sont obligatoires";
        }

        try {
            boolean ok = ConsultationDao.enregistrerDiagnostic(consultationId.trim(), diagnostic.trim());
            return ok ? "ACCEPTE : diagnostic enregistre" : "REFUSE : consultation inconnue";
        } catch (Exception e) {
            return "ERREUR : " + e.getMessage();
        }
    }

    @Override
    public String obtenirPrescription(String prescriptionId) {
        if (vide(prescriptionId)) {
            return "REFUSE : identifiant de prescription manquant";
        }

        try {
            String xml = PrescriptionXmlGenerator.generer(prescriptionId.trim());

            if (xml == null) {
                return "AUCUN RESULTAT : prescription inconnue";
            }

            String verification = XmlValidator.validerTexte(xml, "xsd/prescription.xsd");
            if (!verification.equals("OK")) {
                return "ERREUR : " + verification;
            }

            return xml;
        } catch (Exception e) {
            return "ERREUR : " + e.getMessage();
        }
    }

    private static boolean vide(String texte) {
        return texte == null || texte.isBlank();
    }
}