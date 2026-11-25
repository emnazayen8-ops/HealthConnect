package app;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import xpath.XPathService;

import java.util.LinkedHashSet;
import java.util.Set;

public class XPathMain {

    public static void main(String[] args) throws Exception {
        XPathService service = new XPathService();
        Document patients = service.charger("xml/patient.xml");
        Document analyses = service.charger("xml/analyse.xml");

        // 1. Rechercher un patient
        System.out.println("=== 1. Rechercher le patient P001 ===");
        NodeList r1 = service.chercher(patients, "//dossierPatient[patient/@id='P001']");
        for (int i = 0; i < r1.getLength(); i++) {
            Node d = r1.item(i);
            System.out.println(service.lire(d, "patient/prenom") + " "
                    + service.lire(d, "patient/nom")
                    + ", né(e) le " + service.lire(d, "patient/dateNaissance"));
        }

        // 2. Rechercher les analyses d'un patient
        System.out.println();
        System.out.println("=== 2. Analyses du patient P003 ===");
        NodeList r2 = service.chercher(analyses, "//analyse[patientId='P003']");
        for (int i = 0; i < r2.getLength(); i++) {
            Node a = r2.item(i);
            System.out.println(service.lire(a, "nomTest") + " : "
                    + service.lire(a, "valeur") + " "
                    + service.lire(a, "unite")
                    + " (critique : " + service.lire(a, "critique") + ")");
        }

        // 3. Rechercher les patients diabétiques
        System.out.println();
        System.out.println("=== 3. Patients diabétiques ===");
        NodeList r3 = service.chercher(patients,
                "//dossierPatient[antecedents/antecedent='Diabète']");
        for (int i = 0; i < r3.getLength(); i++) {
            Node d = r3.item(i);
            System.out.println(service.lire(d, "patient/prenom") + " "
                    + service.lire(d, "patient/nom"));
        }

        // 4. Rechercher les dossiers avec résultats critiques
        System.out.println();
        System.out.println("=== 4. Dossiers avec résultats critiques ===");
        NodeList critiques = service.chercher(analyses, "//analyse[critique='true']");
        Set<String> idsPatients = new LinkedHashSet<>();
        for (int i = 0; i < critiques.getLength(); i++) {
            Node a = critiques.item(i);
            System.out.println("Résultat critique : " + service.lire(a, "nomTest") + " = "
                    + service.lire(a, "valeur") + " " + service.lire(a, "unite")
                    + " (patient " + service.lire(a, "patientId") + ")");
            idsPatients.add(service.lire(a, "patientId"));
        }
        for (String id : idsPatients) {
            NodeList dossiers = service.chercher(patients,
                    "//dossierPatient[patient/@id='" + id + "']");
            for (int i = 0; i < dossiers.getLength(); i++) {
                Node d = dossiers.item(i);
                System.out.println("Dossier concerné : " + service.lire(d, "patient/prenom") + " "
                        + service.lire(d, "patient/nom"));
            }
        }
    }
}