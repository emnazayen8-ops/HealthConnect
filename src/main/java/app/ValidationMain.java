package app;

import validation.XmlValidator;

public class ValidationMain {

    public static void main(String[] args) {
        String[] documents = {"patient", "analyse", "radiologie", "prescription"};

        int valides = 0;

        for (String nom : documents) {
            boolean ok = XmlValidator.valider("xml/" + nom + ".xml", "xsd/" + nom + ".xsd");
            if (ok) {
                valides++;
            }
        }

        System.out.println();
        System.out.println(valides + " document(s) valide(s) sur " + documents.length);
    }
}