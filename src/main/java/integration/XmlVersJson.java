package integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class XmlVersJson {

    public static String convertir(String xml) throws Exception {
        Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder()
                .parse(new InputSource(new StringReader(xml)));
        Element racine = doc.getDocumentElement();

        Map<String, Object> resultat = new LinkedHashMap<>();
        resultat.put(racine.getTagName(), versObjet(racine));

        return new ObjectMapper()
                .enable(SerializationFeature.INDENT_OUTPUT)
                .writeValueAsString(resultat);
    }

    private static Object versObjet(Element element) {
        List<Element> enfants = enfants(element);
        NamedNodeMap attributs = element.getAttributes();

        if (enfants.isEmpty() && attributs.getLength() == 0) {
            return valeur(element.getTagName(), element.getTextContent().trim());
        }

        if (attributs.getLength() == 0 && memeNom(enfants)) {
            List<Object> liste = new ArrayList<>();
            for (Element enfant : enfants) {
                liste.add(versObjet(enfant));
            }
            return liste;
        }

        Map<String, Object> objet = new LinkedHashMap<>();

        for (int i = 0; i < attributs.getLength(); i++) {
            Node attribut = attributs.item(i);
            objet.put(attribut.getNodeName(), attribut.getNodeValue());
        }

        for (Element enfant : enfants) {
            ajouter(objet, enfant.getTagName(), versObjet(enfant));
        }
        return objet;
    }

    @SuppressWarnings("unchecked")
    private static void ajouter(Map<String, Object> objet, String nom, Object valeur) {
        if (!objet.containsKey(nom)) {
            objet.put(nom, valeur);
            return;
        }

        Object existant = objet.get(nom);
        if (existant instanceof List) {
            ((List<Object>) existant).add(valeur);
        } else {
            List<Object> liste = new ArrayList<>();
            liste.add(existant);
            liste.add(valeur);
            objet.put(nom, liste);
        }
    }

    private static List<Element> enfants(Element element) {
        List<Element> liste = new ArrayList<>();
        NodeList noeuds = element.getChildNodes();
        for (int i = 0; i < noeuds.getLength(); i++) {
            if (noeuds.item(i).getNodeType() == Node.ELEMENT_NODE) {
                liste.add((Element) noeuds.item(i));
            }
        }
        return liste;
    }

    private static boolean memeNom(List<Element> enfants) {
        String premier = enfants.get(0).getTagName();
        for (Element enfant : enfants) {
            if (!enfant.getTagName().equals(premier)) {
                return false;
            }
        }
        return true;
    }

    private static Object valeur(String nom, String texte) {
        if (texte.equals("true") || texte.equals("false")) {
            return Boolean.valueOf(texte);
        }
        if (nom.equals("valeur")) {
            try {
                return new BigDecimal(texte);
            } catch (NumberFormatException e) {
                return texte;
            }
        }
        return texte;
    }
}