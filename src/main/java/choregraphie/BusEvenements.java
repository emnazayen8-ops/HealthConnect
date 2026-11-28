package choregraphie;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class BusEvenements {

    private final Map<String, List<Consumer<Evenement>>> abonnes = new HashMap<>();

    public void abonner(String typeEvenement, Consumer<Evenement> reaction) {
        abonnes.computeIfAbsent(typeEvenement, cle -> new ArrayList<>()).add(reaction);
    }

    public void publier(Evenement evenement) {
        List<Consumer<Evenement>> liste = abonnes.getOrDefault(evenement.type(), List.of());

        System.out.println("[BUS] evenement publie : " + evenement.type()
                + " (patient " + evenement.patientId() + ")");
        System.out.println("[BUS] " + liste.size() + " abonne(s) a prevenir");

        for (Consumer<Evenement> reaction : liste) {
            try {
                reaction.accept(evenement);
            } catch (Exception e) {
                System.out.println("[BUS] un abonne a echoue : " + e.getMessage());
            }
        }
    }
}