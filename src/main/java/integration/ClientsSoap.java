package integration;

import jakarta.xml.ws.Service;
import soap.ICliniqueService;
import soap.ILaboratoireService;
import soap.IRadiologieService;

import javax.xml.namespace.QName;
import java.net.URL;

public class ClientsSoap {

    private static final String NAMESPACE = "http://healthconnect.org/soap";
    private static final String BASE = "http://localhost:8081/soap";

    public static ICliniqueService clinique() throws Exception {
        return port("clinique", "CliniqueService", ICliniqueService.class);
    }

    public static ILaboratoireService laboratoire() throws Exception {
        return port("laboratoire", "LaboratoireService", ILaboratoireService.class);
    }

    public static IRadiologieService radiologie() throws Exception {
        return port("radiologie", "RadiologieService", IRadiologieService.class);
    }

    private static <T> T port(String chemin, String nomService, Class<T> interfaceService)
            throws Exception {
        URL wsdl = java.net.URI.create(BASE + "/" + chemin + "?wsdl").toURL();
        Service service = Service.create(wsdl, new QName(NAMESPACE, nomService));
        return service.getPort(interfaceService);
    }
}