package soap;

import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebService;

@WebService(name = "Clinique", targetNamespace = "http://healthconnect.org/soap")
public interface ICliniqueService {

    @WebMethod(operationName = "inscrirePatient")
    String inscrirePatient(@WebParam(name = "nom") String nom,
                           @WebParam(name = "prenom") String prenom,
                           @WebParam(name = "dateNaissance") String dateNaissance,
                           @WebParam(name = "sexe") String sexe,
                           @WebParam(name = "telephone") String telephone,
                           @WebParam(name = "antecedents") String antecedents);

    @WebMethod(operationName = "enregistrerConsultation")
    String enregistrerConsultation(@WebParam(name = "patientId") String patientId,
                                   @WebParam(name = "medecinId") String medecinId,
                                   @WebParam(name = "motif") String motif);

    @WebMethod(operationName = "creerPrescription")
    String creerPrescription(@WebParam(name = "consultationId") String consultationId,
                             @WebParam(name = "analysesDemandees") String analysesDemandees,
                             @WebParam(name = "examensDemandes") String examensDemandes,
                             @WebParam(name = "medicaments") String medicaments);

    @WebMethod(operationName = "enregistrerDiagnostic")
    String enregistrerDiagnostic(@WebParam(name = "consultationId") String consultationId,
                                 @WebParam(name = "diagnostic") String diagnostic);

    @WebMethod(operationName = "obtenirPrescription")
    String obtenirPrescription(@WebParam(name = "prescriptionId") String prescriptionId);
}