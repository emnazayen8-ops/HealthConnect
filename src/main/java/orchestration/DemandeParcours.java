package orchestration;

public record DemandeParcours(String nom, String prenom, String dateNaissance, String sexe,
                              String telephone, String antecedents, String medecinId,
                              String motif, String analyses, String examens) {
}