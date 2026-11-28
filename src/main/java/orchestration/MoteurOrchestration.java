package orchestration;

import soap.ICliniqueService;
import validation.XmlValidator;
import xpath.XPathService;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MoteurOrchestration {

    private static final Pattern ID = Pattern.compile("^ACCEPTE.*identifiant\\s+(\\S+)$");

    private final Middleware middleware = new Middleware();
    private final XPathService xpath = new XPathService();
    private final List<String> journal = new ArrayList<>();

    public List<String> getJournal() {
        return journal;
    }

    private void log(String etape, String detail) {
        String ligne = LocalTime.now().withNano(0) + " [" + etape + "] " + detail;
        journal.add(ligne);
        System.out.println(ligne);
    }

    // ===== PHASE 1 : Consultation, Prescription, Analyse =====
    public Parcours demarrer(DemandeParcours d) throws Exception {
        log("DEMARRAGE", "parcours de " + d.prenom() + " " + d.nom());

        ICliniqueService clinique = ClientsSoap.clinique();

        // Etape 1 : Consultation
        String repPatient = clinique.inscrirePatient(d.nom(), d.prenom(), d.dateNaissance(),
                d.sexe(), d.telephone(), d.antecedents());
        String patientId = extraireId(repPatient);
        if (patientId == null) {
            log("ARRET", "inscription refusee : " + repPatient);
            return null;
        }
        log("CONSULTATION", "patient inscrit : " + patientId);

        String repConsultation = clinique.enregistrerConsultation(patientId, d.medecinId(), d.motif());
        String consultationId = extraireId(repConsultation);
        if (consultationId == null) {
            log("ARRET", "consultation refusee : " + repConsultation);
            return null;
        }
        log("CONSULTATION", "consultation creee : " + consultationId);

        // Etape 2 : Prescription
        String repPrescription = clinique.creerPrescription(consultationId,
                texte(d.analyses()), texte(d.examens()), "");
        String prescriptionId = extraireId(repPrescription);
        if (prescriptionId == null) {
            log("ARRET", "prescription refusee : " + repPrescription);
            return null;
        }
        log("PRESCRIPTION", "prescription creee : " + prescriptionId);

        // Etape 3 : Analyse (envoi des demandes aux services)
        middleware.acheminerPrescription(prescriptionId);
        log("ANALYSE", "demandes envoyees au laboratoire et a la radiologie, en attente des resultats");

        return new Parcours(patientId, d.medecinId(), consultationId, prescriptionId);
    }

    // ===== PHASE 2 : Validation, Archivage, Notification =====
    public String traiterResultats(Parcours p) throws Exception {
        String id = p.prescriptionId();
        log("RESULTATS", "controle de la prescription " + id);

        String analyses = ClientsSoap.laboratoire().obtenirResultatAnalyse(id);
        String examens = ClientsSoap.radiologie().obtenirCompteRendu(id);

        if (!estXml(analyses) && !estXml(examens)) {
            log("ARRET", "aucune analyse ni examen pour cette prescription");
            return "RIEN_A_TRAITER";
        }

        int enAttente = compter(analyses, "//analyse[statut='demandee']")
                + compter(examens, "//compteRendu[statut='demandee']");
        if (enAttente > 0) {
            log("ATTENTE", enAttente + " resultat(s) pas encore recu(s)");
            return "EN_ATTENTE";
        }

        int aTraiter = compter(analyses, "//analyse[statut='realisee']")
                + compter(examens, "//compteRendu[statut='realisee']");
        if (aTraiter == 0) {
            log("INFO", "resultats deja traites");
            return "DEJA_TRAITE";
        }

        // Etape 4 : Validation
        if (!valide(analyses, "xsd/analyse.xsd") || !valide(examens, "xsd/radiologie.xsd")) {
            log("REFUS", "document non conforme au schema, renvoi demande a l'organisation");
            return "REFUSE";
        }
        int critiques = compter(analyses, "//analyse[critique='true']");

        ResultatsDao.changerStatutAnalyses(id, "realisee", "validee");
        ResultatsDao.changerStatutExamens(id, "realisee", "validee");
        log("VALIDATION", aTraiter + " resultat(s) conforme(s) aux schemas XSD, statut validee");

        // Etape 5 : Archivage
        ResultatsDao.changerStatutAnalyses(id, "validee", "archivee");
        ResultatsDao.changerStatutExamens(id, "validee", "archivee");
        archiver(id);

        // Etape 6 : Notification
        if (critiques > 0) {
            NotificationDao.creer("MEDECIN", p.medecinId(), "resultat_critique",
                    "ALERTE : resultat critique pour le patient " + p.patientId()
                            + " (prescription " + id + ")");
            NotificationDao.creer("PATIENT", p.patientId(), "resultat_critique",
                    "Un de vos resultats necessite une consultation rapide");
            log("NOTIFICATION", critiques + " resultat(s) CRITIQUE(S) : alerte envoyee au medecin et au patient");
            return "TERMINE_AVEC_ALERTE";
        }

        NotificationDao.creer("MEDECIN", p.medecinId(), "resultats_disponibles",
                "Les resultats du patient " + p.patientId() + " sont disponibles (prescription " + id + ")");
        log("NOTIFICATION", "medecin prevenu : resultats disponibles");
        return "TERMINE";
    }

    private void archiver(String id) throws Exception {
        Path dossier = Path.of("archives");
        Files.createDirectories(dossier);

        String analyses = ClientsSoap.laboratoire().obtenirResultatAnalyse(id);
        String examens = ClientsSoap.radiologie().obtenirCompteRendu(id);

        if (estXml(analyses)) {
            Files.writeString(dossier.resolve(id + "-analyses.xml"), analyses);
        }
        if (estXml(examens)) {
            Files.writeString(dossier.resolve(id + "-radiologie.xml"), examens);
        }
        log("ARCHIVAGE", "documents conserves dans le dossier archives, statut archivee");
    }

    private String extraireId(String reponse) {
        if (reponse == null) {
            return null;
        }
        Matcher m = ID.matcher(reponse.trim());
        return m.matches() ? m.group(1) : null;
    }

    private boolean estXml(String texte) {
        return texte != null && texte.trim().startsWith("<");
    }

    private int compter(String xml, String expression) throws Exception {
        if (!estXml(xml)) {
            return 0;
        }
        return xpath.chercher(xpath.chargerTexte(xml), expression).getLength();
    }

    private boolean valide(String xml, String xsd) {
        if (!estXml(xml)) {
            return true;
        }
        return XmlValidator.validerTexte(xml, xsd).equals("OK");
    }

    private String texte(String valeur) {
        return valeur == null ? "" : valeur;
    }
}