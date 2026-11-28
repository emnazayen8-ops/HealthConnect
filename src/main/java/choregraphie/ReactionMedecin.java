package choregraphie;

import dao.NotificationDao;

import java.sql.SQLException;
import java.util.function.Consumer;

public class ReactionMedecin implements Consumer<Evenement> {

    @Override
    public void accept(Evenement e) {
        try {
            NotificationDao.creer("MEDECIN", e.medecinId(), "resultat_critique",
                    "ALERTE : " + e.detail() + " pour le patient " + e.patientId());
            System.out.println("[MEDECIN] " + e.medecinId() + " notifie : " + e.detail());
        } catch (SQLException ex) {
            throw new IllegalStateException(ex.getMessage(), ex);
        }
    }
}