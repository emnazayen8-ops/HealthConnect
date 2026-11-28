package choregraphie;

import java.time.LocalDateTime;

public record Evenement(String type, String patientId, String medecinId,
                        String prescriptionId, String detail, LocalDateTime date) {

    public static Evenement resultatCritique(String patientId, String medecinId,
                                             String prescriptionId, String detail) {
        return new Evenement("RESULTAT_CRITIQUE", patientId, medecinId,
                prescriptionId, detail, LocalDateTime.now());
    }
}