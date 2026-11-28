package choregraphie;

import dao.NotificationDao;

import java.sql.SQLException;
import java.util.function.Consumer;

public class ReactionPatient implements Consumer<Evenement> {

    @Override
    public void accept(Evenement e) {
        try {
            NotificationDao.creer("PATIENT", e.patientId(), "resultat_critique",
                    "Un de vos resultats necessite une consultation rapide");
            System.out.println("[PATIENT] " + e.patientId() + " alerte");
        } catch (SQLException ex) {
            throw new IllegalStateException(ex.getMessage(), ex);
        }
    }
}