package soap;

import jakarta.jws.WebService;
import validation.XmlValidator;

@WebService(endpointInterface = "soap.ILaboratoireService",
        serviceName = "LaboratoireService",
        portName = "LaboratoirePort",
        targetNamespace = "http://healthconnect.org/soap")
public class LaboratoireService implements ILaboratoireService {

    @Override
    public String envoyerResultatAnalyse(String analyseXml) {
        if (analyseXml == null || analyseXml.isBlank()) {
            return "REFUSE : document vide";
        }

        String resultat = XmlValidator.validerTexte(analyseXml, "xsd/analyse.xsd");

        if (resultat.equals("OK")) {
            return "ACCEPTE : document conforme au schema analyse.xsd";
        }
        return "REFUSE : " + resultat;
    }
}