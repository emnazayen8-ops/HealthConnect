<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

    <xsl:output method="html" encoding="UTF-8" indent="yes"/>

    <xsl:template match="/dmp">
        <html>
            <head>
                <meta charset="UTF-8"/>
                <title>Dossiers patients - Vue médecin</title>
                <style>
                    body { font-family: Arial, sans-serif; margin: 30px; background: #f4f6f8; }
                    h1 { color: #1a5276; }
                    .dossier { background: white; padding: 15px 20px; margin-bottom: 20px;
                    border-left: 5px solid #1a5276; }
                    .badge { background: #e67e22; color: white; padding: 2px 8px;
                    border-radius: 4px; font-size: 12px; }
                    table { border-collapse: collapse; width: 100%; margin-top: 10px; }
                    th, td { border: 1px solid #ccc; padding: 6px 10px; text-align: left; }
                    th { background: #d6eaf8; }
                </style>
            </head>
            <body>
                <h1>Dossiers patients</h1>
                <xsl:apply-templates select="dossierPatient"/>
            </body>
        </html>
    </xsl:template>

    <xsl:template match="dossierPatient">
        <div class="dossier">
            <h2>
                <xsl:value-of select="patient/prenom"/>
                <xsl:text> </xsl:text>
                <xsl:value-of select="patient/nom"/>
                <xsl:text> (</xsl:text>
                <xsl:value-of select="patient/@id"/>
                <xsl:text>) </xsl:text>
                <xsl:if test="antecedents/antecedent='Diabète'">
                    <span class="badge">Diabétique</span>
                </xsl:if>
            </h2>
            <p>
                <strong>Date de naissance : </strong>
                <xsl:value-of select="patient/dateNaissance"/>
                <strong> | Sexe : </strong>
                <xsl:value-of select="patient/sexe"/>
                <strong> | Téléphone : </strong>
                <xsl:choose>
                    <xsl:when test="patient/telephone">
                        <xsl:value-of select="patient/telephone"/>
                    </xsl:when>
                    <xsl:otherwise>non renseigné</xsl:otherwise>
                </xsl:choose>
            </p>
            <p>
                <strong>Antécédents : </strong>
                <xsl:choose>
                    <xsl:when test="antecedents">
                        <xsl:for-each select="antecedents/antecedent">
                            <xsl:value-of select="."/>
                            <xsl:if test="position() != last()">, </xsl:if>
                        </xsl:for-each>
                    </xsl:when>
                    <xsl:otherwise>Aucun</xsl:otherwise>
                </xsl:choose>
            </p>
            <table>
                <tr>
                    <th>Date</th>
                    <th>Médecin</th>
                    <th>Clinique</th>
                    <th>Motif</th>
                    <th>Diagnostic</th>
                </tr>
                <xsl:for-each select="consultation">
                    <tr>
                        <td><xsl:value-of select="date"/></td>
                        <td><xsl:value-of select="medecin"/></td>
                        <td><xsl:value-of select="clinique"/></td>
                        <td><xsl:value-of select="motif"/></td>
                        <td>
                            <xsl:choose>
                                <xsl:when test="diagnostic">
                                    <xsl:value-of select="diagnostic"/>
                                </xsl:when>
                                <xsl:otherwise><em>En attente des résultats</em></xsl:otherwise>
                            </xsl:choose>
                        </td>
                    </tr>
                </xsl:for-each>
            </table>
        </div>
    </xsl:template>

</xsl:stylesheet>