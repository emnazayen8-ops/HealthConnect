package app;

import dao.ResultatsDao;
import orchestration.DemandeParcours;
import orchestration.MoteurOrchestration;
import orchestration.Parcours;

public class OrchestrationMain {

    public static void main(String[] args) throws Exception {
        MoteurOrchestration moteur = new MoteurOrchestration();

        DemandeParcours demande = new DemandeParcours(
                "Zouari", "Sana", "1989-06-30", "F", "27555666", "Diabete",
                "MED002", "Fatigue et soif intense",
                "Glycemie a jeun, HbA1c", "Radio du thorax");

        System.out.println("##### PHASE 1 : le patient arrive #####");
        Parcours parcours = moteur.demarrer(demande);
        if (parcours == null) {
            System.out.println("Parcours interrompu.");
            return;
        }

        System.out.println();
        System.out.println("##### PHASE 2 : les resultats ne sont pas encore la #####");
        System.out.println("Etat : " + moteur.traiterResultats(parcours));

        System.out.println();
        System.out.println("##### Le laboratoire et la radiologie saisissent leurs resultats #####");
        ResultatsDao.saisirAnalyse(parcours.prescriptionId(), "Glycemie a jeun",
                "3.20", "g/L", "0.7 - 1.1", true);
        ResultatsDao.saisirAnalyse(parcours.prescriptionId(), "HbA1c",
                "9.80", "%", "4 - 6", true);
        ResultatsDao.saisirCompteRendu(parcours.prescriptionId(), "Radio",
                "Pas d'anomalie visible", "Examen normal", "Dr Leila Mansour");

        System.out.println();
        System.out.println("##### PHASE 3 : traitement des resultats #####");
        System.out.println("Etat : " + moteur.traiterResultats(parcours));

        System.out.println();
        System.out.println("##### Relance : pas de doublon #####");
        System.out.println("Etat : " + moteur.traiterResultats(parcours));
    }
}