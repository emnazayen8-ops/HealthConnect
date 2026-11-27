package soap;

import jakarta.xml.ws.Endpoint;

public class ServeurSOAP {

    public static void main(String[] args) {
        String base = "http://localhost:8081/soap";

        Endpoint.publish(base + "/clinique", new CliniqueService());
        Endpoint.publish(base + "/laboratoire", new LaboratoireService());
        Endpoint.publish(base + "/radiologie", new RadiologieService());

        System.out.println("Services publies sur " + base);
        System.out.println("WSDL clinique    : " + base + "/clinique?wsdl");
        System.out.println("WSDL laboratoire : " + base + "/laboratoire?wsdl");
        System.out.println("WSDL radiologie  : " + base + "/radiologie?wsdl");
    }
}