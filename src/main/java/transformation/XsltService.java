package transformation;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.io.InputStream;

public class XsltService {

    public static void versHtml(String cheminXml, String cheminXsl,
                                String fichierSortie, String patientId) throws Exception {
        ClassLoader loader = XsltService.class.getClassLoader();

        try (InputStream xml = loader.getResourceAsStream(cheminXml);
             InputStream xsl = loader.getResourceAsStream(cheminXsl)) {

            if (xml == null || xsl == null) {
                throw new IllegalArgumentException("Fichier introuvable : " + cheminXml + " ou " + cheminXsl);
            }

            Transformer transformer = TransformerFactory.newInstance()
                    .newTransformer(new StreamSource(xsl));

            if (patientId != null) {
                transformer.setParameter("patientId", patientId);
            }

            File sortie = new File(fichierSortie);
            sortie.getParentFile().mkdirs();

            transformer.transform(new StreamSource(xml), new StreamResult(sortie));
            System.out.println("Fichier créé : " + sortie.getPath());
        }
    }
}