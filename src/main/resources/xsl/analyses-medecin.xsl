<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

    <xsl:output method="html" encoding="UTF-8" indent="yes"/>

    <xsl:template match="/analyses">
        <html>
            <head>
                <meta charset="UTF-8"/>
                <title>Résultats d'analyses - Vue médecin</title>
                <style>
                    body { font-family: Arial, sans-serif; margin: 30px; }
                    h1 { color: #1a5276; }
                    table { border-collapse: collapse; width: 100%; }
                    th, td { border: 1px solid #ccc; padding: 8px 10px; text-align: left; }
                    th { background: #d6eaf8; }
                    tr.critique { background: #fadbd8; color: #922b21; font-weight: bold; }
                </style>
            </head>
            <body>
                <h1>Résultats d'analyses</h1>
                <p>
                    Nombre d'analyses : <xsl:value-of select="count(analyse)"/>
                    | Résultats critiques :
                    <xsl:value-of select="count(analyse[critique='true'])"/>
                </p>
                <table>
                    <tr>
                        <th>Patient</th>
                        <th>Test</th>
                        <th>Valeur</th>
                        <th>Unité</th>
                        <th>Valeur normale</th>
                        <th>Laboratoire</th>
                        <th>Résultat</th>
                    </tr>
                    <xsl:for-each select="analyse">
                        <tr>
                            <xsl:if test="critique='true'">
                                <xsl:attribute name="class">critique</xsl:attribute>
                            </xsl:if>
                            <td><xsl:value-of select="patientId"/></td>
                            <td><xsl:value-of select="nomTest"/></td>
                            <td><xsl:value-of select="valeur"/></td>
                            <td><xsl:value-of select="unite"/></td>
                            <td><xsl:value-of select="valeurNormale"/></td>
                            <td><xsl:value-of select="laboratoire"/></td>
                            <td>
                                <xsl:choose>
                                    <xsl:when test="critique='true'">CRITIQUE</xsl:when>
                                    <xsl:otherwise>Non critique</xsl:otherwise>
                                </xsl:choose>
                            </td>
                        </tr>
                    </xsl:for-each>
                </table>
            </body>
        </html>
    </xsl:template>

</xsl:stylesheet>