package app;

import transformation.XsltService;

public class XsltMain {

    public static void main(String[] args) throws Exception {
        XsltService.versHtml("xml/patient.xml", "xsl/dossiers-medecin.xsl",
                "output/dossiers-medecin.html", null);

        XsltService.versHtml("xml/analyse.xml", "xsl/analyses-medecin.xsl",
                "output/analyses-medecin.html", null);

        XsltService.versHtml("xml/patient.xml", "xsl/vue-patient.xsl",
                "output/vue-patient-P001.html", "P001");

        XsltService.versHtml("xml/patient.xml", "xsl/vue-patient.xsl",
                "output/vue-patient-P003.html", "P003");
    }
}