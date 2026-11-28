package choregraphie;

import java.util.function.Consumer;

public class ReactionJournal implements Consumer<Evenement> {

    @Override
    public void accept(Evenement e) {
        System.out.println("[JOURNAL] " + e.date().withNano(0) + " " + e.type()
                + " patient=" + e.patientId() + " prescription=" + e.prescriptionId());
    }
}