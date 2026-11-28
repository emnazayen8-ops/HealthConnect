package app;

import integration.Middleware;

public class IntegrationMain {

    public static void main(String[] args) throws Exception {
        Middleware bus = new Middleware();

        System.out.println("=== 1. Adaptation d'un format ancien (CSV) ===");
        bus.integrerResultatLaboratoire(
                "A100;P003;PR002;Laboratoire Al Shifa;Glycemie;28/11/2026;3,10;g/L;0.7 - 1.1;1");

        System.out.println();
        System.out.println("=== 2. Acheminement d'une prescription ===");
        bus.acheminerPrescription("PR004");

        System.out.println();
        System.out.println("=== 3. Resultats rassembles en JSON ===");
        System.out.println(bus.recupererResultats("PR003"));
    }
}