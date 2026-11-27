package xpath;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.io.InputStream;

public class XPathService {

    private final XPath xpath = XPathFactory.newInstance().newXPath();

    public Document charger(String chemin) throws Exception {
        try (InputStream in = getClass().getClassLoader().getResourceAsStream(chemin)) {
            if (in == null) {
                throw new IllegalArgumentException("Fichier introuvable : " + chemin);
            }
            return DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(in);
        }
    }

    public NodeList chercher(Document doc, String expression) throws Exception {
        return (NodeList) xpath.evaluate(expression, doc, XPathConstants.NODESET);
    }

    public String lire(Node noeud, String expression) throws Exception {
        return xpath.evaluate(expression, noeud);
    }
    public Document chargerTexte(String contenuXml) throws Exception {
        return DocumentBuilderFactory.newInstance().newDocumentBuilder()
                .parse(new InputSource(new StringReader(contenuXml)));
    }
}