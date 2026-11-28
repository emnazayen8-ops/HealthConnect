package securite;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

public class MotDePasse {

    private static final int ITERATIONS = 120_000;
    private static final int LONGUEUR_BITS = 256;

    public static String hasher(String motDePasse) {
        byte[] sel = new byte[16];
        new SecureRandom().nextBytes(sel);
        byte[] empreinte = calculer(motDePasse.toCharArray(), sel);

        return Base64.getEncoder().encodeToString(sel) + ":"
                + Base64.getEncoder().encodeToString(empreinte);
    }

    public static boolean verifier(String motDePasse, String stocke) {
        if (motDePasse == null || stocke == null) {
            return false;
        }

        String[] parties = stocke.split(":");
        if (parties.length != 2) {
            return false;
        }

        byte[] sel = Base64.getDecoder().decode(parties[0]);
        byte[] attendue = Base64.getDecoder().decode(parties[1]);
        byte[] calculee = calculer(motDePasse.toCharArray(), sel);

        return MessageDigest.isEqual(attendue, calculee);
    }

    private static byte[] calculer(char[] motDePasse, byte[] sel) {
        try {
            PBEKeySpec spec = new PBEKeySpec(motDePasse, sel, ITERATIONS, LONGUEUR_BITS);
            return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
                    .generateSecret(spec).getEncoded();
        } catch (Exception e) {
            throw new IllegalStateException("Hachage impossible : " + e.getMessage(), e);
        }
    }
}