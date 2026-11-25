package soap;

import jakarta.xml.ws.Endpoint;

public class ServeurSOAP {

    public static void main(String[] args) {
        String url = "http://localhost:8081/soap/laboratoire";

        Endpoint.publish(url, new LaboratoireService());

        System.out.println("Service publie : " + url);
        System.out.println("WSDL : " + url + "?wsdl");
    }
}