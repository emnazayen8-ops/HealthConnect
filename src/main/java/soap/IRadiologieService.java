package soap;

import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebService;

@WebService(name = "Radiologie", targetNamespace = "http://healthconnect.org/soap")
public interface IRadiologieService {

    @WebMethod(operationName = "envoyerCompteRendu")
    String envoyerCompteRendu(@WebParam(name = "compteRenduXml") String compteRenduXml);

    @WebMethod(operationName = "demanderExamen")
    String demanderExamen(@WebParam(name = "prescriptionXml") String prescriptionXml);

    @WebMethod(operationName = "obtenirCompteRendu")
    String obtenirCompteRendu(@WebParam(name = "prescriptionId") String prescriptionId);
}