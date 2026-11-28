package securite;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dao.Utilisateur;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;

public class JwtService {

    public static final long DUREE_SECONDES = 3600;

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final byte[] SECRET = chargerSecret();

    private static byte[] chargerSecret() {
        String valeur = System.getenv("HEALTHCONNECT_SECRET");
        if (valeur == null || valeur.isBlank()) {
            valeur = "cle-de-demonstration-a-remplacer-en-production-0123456789";
        }
        return valeur.getBytes(StandardCharsets.UTF_8);
    }

    public static String creer(Utilisateur u) throws Exception {
        Map<String, Object> entete = new LinkedHashMap<>();
        entete.put("alg", "HS256");
        entete.put("typ", "JWT");

        Map<String, Object> contenu = new LinkedHashMap<>();
        contenu.put("sub", u.login());
        contenu.put("uid", u.id());
        contenu.put("role", u.role());
        if (u.patientId() != null) {
            contenu.put("patientId", u.patientId());
        }
        if (u.medecinId() != null) {
            contenu.put("medecinId", u.medecinId());
        }
        if (u.pharmacieId() != null) {
            contenu.put("pharmacieId", u.pharmacieId());
        }
        contenu.put("exp", Instant.now().getEpochSecond() + DUREE_SECONDES);

        String partie1 = encoder(MAPPER.writeValueAsBytes(entete));
        String partie2 = encoder(MAPPER.writeValueAsBytes(contenu));
        String signature = encoder(signer(partie1 + "." + partie2));

        return partie1 + "." + partie2 + "." + signature;
    }

    public static Map<String, Object> verifier(String jeton) {
        try {
            String[] parties = jeton.split("\\.");
            if (parties.length != 3) {
                return null;
            }

            byte[] attendue = signer(parties[0] + "." + parties[1]);
            byte[] recue = Base64.getUrlDecoder().decode(parties[2]);

            if (!MessageDigest.isEqual(attendue, recue)) {
                return null;
            }

            Map<String, Object> contenu = MAPPER.readValue(
                    Base64.getUrlDecoder().decode(parties[1]),
                    new TypeReference<Map<String, Object>>() { });

            long expiration = ((Number) contenu.get("exp")).longValue();
            if (Instant.now().getEpochSecond() > expiration) {
                return null;
            }
            return contenu;

        } catch (Exception e) {
            return null;
        }
    }

    private static byte[] signer(String donnees) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(SECRET, "HmacSHA256"));
        return mac.doFinal(donnees.getBytes(StandardCharsets.UTF_8));
    }

    private static String encoder(byte[] octets) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(octets);
    }
}