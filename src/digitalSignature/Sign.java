package digitalSignature;


import java.security.*;

public class Sign {
    static final String ALGORITHM = "SHA256withRSA";
    private static Signature sig;

    static {
        try {
            sig = Signature.getInstance(ALGORITHM);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public static byte[] generateDigitalSignature(String data, PrivateKey privateKey) {
        try {
            sig.initSign(privateKey);
            sig.update(data.getBytes());
            return sig.sign();
        } catch (InvalidKeyException | SignatureException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean verifySignature(String data, byte[] signature, PublicKey key) {
        try {
            sig.initVerify(key);
            sig.update(data.getBytes());
            return sig.verify(signature);
        } catch (InvalidKeyException | SignatureException e) {
            throw new RuntimeException(e);
        }
    }
}