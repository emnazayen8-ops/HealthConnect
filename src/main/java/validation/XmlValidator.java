package validation;

import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.IOException;
import java.io.InputStream;

public class XmlValidator {

    public static boolean valider(String cheminXml, String cheminXsd) {
        ClassLoader loader = XmlValidator.class.getClassLoader();

        try (InputStream xml = loader.getResourceAsStream(cheminXml);
             InputStream xsd = loader.getResourceAsStream(cheminXsd)) {

            if (xml == null || xsd == null) {
                System.out.println("Fichier introuvable : " + cheminXml + " ou " + cheminXsd);
                return false;
            }

            SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema schema = factory.newSchema(new StreamSource(xsd));
            Validator validator = schema.newValidator();

            validator.validate(new StreamSource(xml));

            System.out.println("VALIDE   : " + cheminXml);
            return true;

        } catch (SAXException e) {
            System.out.println("INVALIDE : " + cheminXml);
            System.out.println("   Raison : " + e.getMessage());
            return false;

        } catch (IOException e) {
            System.out.println("Erreur de lecture : " + e.getMessage());
            return false;
        }
    }
}