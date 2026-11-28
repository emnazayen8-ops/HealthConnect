package choregraphie;

import dao.ResultatsDao;

public class LaboratoirePublieur {

    private final BusEvenements bus;

    public LaboratoirePublieur(BusEvenements bus) {
        this.bus = bus;
    }

    public void publierResultat(String patientId, String medecinId, String prescriptionId,
                                String nomTest, String valeur, String unite,
                                String valeurNormale, boolean critique) throws Exception {

        int lignes = ResultatsDao.saisirAnalyse(prescriptionId, nomTest, valeur, unite,
                valeurNormale, critique);

        System.out.println("[LABORATOIRE] resultat publie : " + nomTest + " = " + valeur + " " + unite
                + (critique ? " (CRITIQUE)" : ""));

        if (lignes == 0) {
            System.out.println("[LABORATOIRE] aucune demande en attente pour ce test");
            return;
        }

        if (critique) {
            bus.publier(Evenement.resultatCritique(patientId, medecinId, prescriptionId,
                    nomTest + " = " + valeur + " " + unite));
        }
    }
}