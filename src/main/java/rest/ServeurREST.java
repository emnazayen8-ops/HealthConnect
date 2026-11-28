package rest;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;
import securite.FiltreSecurite;

import java.net.URI;

public class ServeurREST {

    public static void main(String[] args) throws Exception {
        String base = "http://localhost:8082/api/";

        ResourceConfig config = new ResourceConfig()
                .register(FiltreSecurite.class)
                .register(AuthResource.class)
                .register(JournalResource.class)
                .register(PatientResource.class)
                .register(PrescriptionResource.class)
                .register(ErreurMapper.class)
                .register(JacksonFeature.class);

        HttpServer serveur = GrizzlyHttpServerFactory.createHttpServer(URI.create(base), config);

        System.out.println("API REST securisee disponible : " + base);
        System.out.println("Connexion : POST " + base + "auth/login");
        System.out.println("Appuyez sur Entree pour arreter le serveur...");

        System.in.read();
        serveur.shutdownNow();
    }
}