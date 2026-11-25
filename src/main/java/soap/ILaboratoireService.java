package soap;

import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebService;

@WebService(name = "Laboratoire", targetNamespace = "http://healthconnect.org/soap")
public interface ILaboratoireService {

    @WebMethod(operationName = "envoyerResultatAnalyse")
    String envoyerResultatAnalyse(@WebParam(name = "analyseXml") String analyseXml);
}