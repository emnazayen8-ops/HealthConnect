package app;

import choregraphie.*;
import integration.ClientsSoap;
import integration.Middleware;
import soap.ICliniqueService;

public class ChoregraphieMain {

    public static void main(String[] args) throws Exception {

        // ---------- Preparation du decor (hors choregraphie) ----------
        System.out.println("##### Preparation : un patient avec une prescription #####");
        ICliniqueService clinique = ClientsSoap.clinique();

        String patientId = id(clinique.inscrirePatient(
                "Mansouri", "Walid", "1975-01-15", "M", "29888777", "Diabete"));
        String consultationId = id(clinique.enregistrerConsultation(
                patientId, "MED002", "Controle de la glycemie"));
        String prescriptionId = id(clinique.creerPrescription(
                consultationId, "Glycemie a jeun, Creatinine", "", "Metformine 500 mg"));

        new Middleware().acheminerPrescription(prescriptionId);

        // ---------- La choregraphie ----------
        BusEvenements bus = new BusEvenements();
        bus.abonner("RESULTAT_CRITIQUE", new ReactionMedecin());
        bus.abonner("RESULTAT_CRITIQUE", new ReactionPatient());
        bus.abonner("RESULTAT_CRITIQUE", new ReactionPharmacie("PH001"));
        bus.abonner("RESULTAT_CRITIQUE", new ReactionJournal());

        LaboratoirePublieur laboratoire = new LaboratoirePublieur(bus);

        System.out.println();
        System.out.println("##### Cas 1 : resultat NORMAL, personne ne reagit #####");
        laboratoire.publierResultat(patientId, "MED002", prescriptionId,
                "Creatinine", "0.90", "mg/dL", "0.6 - 1.2", false);

        System.out.println();
        System.out.println("##### Cas 2 : resultat CRITIQUE, chacun reagit de son cote #####");
        laboratoire.publierResultat(patientId, "MED002", prescriptionId,
                "Glycemie a jeun", "3.40", "g/L", "0.7 - 1.1", true);
    }

    private static String id(String reponse) {
        if (!reponse.startsWith("ACCEPTE")) {
            throw new IllegalStateException("Preparation impossible : " + reponse);
        }
        return reponse.substring(reponse.lastIndexOf(' ') + 1).trim();
    }
}