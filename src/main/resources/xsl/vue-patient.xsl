<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

    <xsl:output method="html" encoding="UTF-8" indent="yes"/>

    <xsl:param name="patientId" select="'P001'"/>

    <xsl:template match="/dmp">
        <xsl:variable name="d" select="dossierPatient[patient/@id=$patientId]"/>
        <html>
            <head>
                <meta charset="UTF-8"/>
                <title>Mon dossier médical</title>
                <style>
                    body { font-family: Arial, sans-serif; margin: 30px; max-width: 700px; }
                    h1 { color: #117a65; }
                    h2 { color: #117a65; border-bottom: 2px solid #a3e4d7; }
                    li { margin-bottom: 6px; }
                </style>
            </head>
            <body>
                <xsl:choose>
                    <xsl:when test="$d">
                        <h1>
                            Bonjour <xsl:value-of select="$d/patient/prenom"/>
                        </h1>

                        <h2>Mes informations</h2>
                        <p>
                            Nom : <xsl:value-of select="$d/patient/nom"/><br/>
                            Né(e) le : <xsl:value-of select="$d/patient/dateNaissance"/>
                        </p>

                        <h2>Mes antécédents</h2>
                        <xsl:choose>
                            <xsl:when test="$d/antecedents">
                                <ul>
                                    <xsl:for-each select="$d/antecedents/antecedent">
                                        <li><xsl:value-of select="."/></li>
                                    </xsl:for-each>
                                </ul>
                            </xsl:when>
                            <xsl:otherwise><p>Aucun antécédent déclaré.</p></xsl:otherwise>
                        </xsl:choose>

                        <h2>Mes consultations</h2>
                        <ul>
                            <xsl:for-each select="$d/consultation">
                                <li>
                                    Le <xsl:value-of select="date"/>,
                                    avec <xsl:value-of select="medecin"/> :
                                    <xsl:value-of select="motif"/>.
                                    <br/>
                                    <em>Conclusion du médecin : </em>
                                    <xsl:choose>
                                        <xsl:when test="diagnostic">
                                            <xsl:value-of select="diagnostic"/>
                                        </xsl:when>
                                        <xsl:otherwise>en attente de vos résultats.</xsl:otherwise>
                                    </xsl:choose>
                                </li>
                            </xsl:for-each>
                        </ul>
                    </xsl:when>
                    <xsl:otherwise>
                        <p>Aucun dossier trouvé pour cet identifiant.</p>
                    </xsl:otherwise>
                </xsl:choose>
            </body>
        </html>
    </xsl:template>

</xsl:stylesheet>