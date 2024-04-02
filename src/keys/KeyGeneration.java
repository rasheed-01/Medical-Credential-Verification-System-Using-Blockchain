package keys;

import java.io.*;
import java.security.*;

public class KeyGeneration {
    private static final String Algorithm = "RSA";
    static final String Private_Key_File = "PrivateKey.key";
    static final String Public_Key_File = "PublicKey.key";
    private java.security.KeyPairGenerator keygen;

    public KeyGeneration() {
        try {
            keygen = java.security.KeyPairGenerator.getInstance(Algorithm);
            keygen.initialize(2048);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            throw new RuntimeException("Key pair generator initialization failed", e);
        }
    }

    public java.security.KeyPair getKeyPair() {
        if (keysExist()) {
            PublicKey publicKey = loadPublicKey(Public_Key_File);
            PrivateKey privateKey = loadPrivateKey(Private_Key_File);
            return new java.security.KeyPair(publicKey, privateKey);
        } else {
            java.security.KeyPair keyPair = keygen.generateKeyPair();
            storeKeys(keyPair.getPublic(), Public_Key_File);
            storeKeys(keyPair.getPrivate(), Private_Key_File);
            return keyPair;
        }
    }

    private boolean keysExist() {
        File publicKeyFile = new File(Public_Key_File);
        File privateKeyFile = new File(Private_Key_File);
        return publicKeyFile.exists() && privateKeyFile.exists();
    }

    private PublicKey loadPublicKey(String path) {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(path))) {
            return (PublicKey) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to load public key from file", e);
        }
    }

    private PrivateKey loadPrivateKey(String path) {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(path))) {
            return (PrivateKey) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to load private key from file", e);
        }
    }

    public static void createKeyPair() {
        KeyGeneration keyPair = new KeyGeneration();
        java.security.KeyPair keyPairs = keyPair.keygen.generateKeyPair();
        PublicKey publicKey = keyPairs.getPublic();
        PrivateKey privateKey = keyPairs.getPrivate();
        // Store keys
        storeKeys(publicKey, Private_Key_File);
        storeKeys(privateKey, Public_Key_File);
    }

    private static void storeKeys(Key key, String path) {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(path))) {
            out.writeObject(key);
        } catch (IOException e) {
            e.printStackTrace();
            // Handle IOException appropriately, e.g., by throwing a runtime exception
            throw new RuntimeException("Failed to store key to file", e);
        }
    }
}