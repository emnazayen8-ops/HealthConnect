package choregraphie;

import dao.TraitementDao;

import java.sql.SQLException;
import java.util.List;
import java.util.function.Consumer;

public class ReactionPharmacie implements Consumer<Evenement> {

    private final String pharmacieId;

    public ReactionPharmacie(String pharmacieId) {
        this.pharmacieId = pharmacieId;
    }

    @Override
    public void accept(Evenement e) {
        try {
            List<String> prepares = TraitementDao.preparer(e.patientId(), pharmacieId);

            if (prepares.isEmpty()) {
                System.out.println("[PHARMACIE " + pharmacieId + "] aucun medicament a preparer pour "
                        + e.patientId());
                return;
            }

            for (String ligne : prepares) {
                System.out.println("[PHARMACIE " + pharmacieId + "] prepare le traitement, prescription "
                        + ligne);
            }
        } catch (SQLException ex) {
            throw new IllegalStateException(ex.getMessage(), ex);
        }
    }
}