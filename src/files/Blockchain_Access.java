package files;

import keys.KeyGeneration;
import blockchain.Block;
import blockchain.Blockchain;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.security.*;
import java.util.HashMap;
import java.util.Base64;
import java.util.Scanner;
import java.util.UUID;

public class Blockchain_Access {

    public static Blockchain retrieveBlockchainFromEncryptedText(PrivateKey privateKey, boolean createGenesisBlock, String BlockchainFile) {
        try (FileInputStream fis = new FileInputStream(BlockchainFile);
        ObjectInputStream ois = new ObjectInputStream(fis)) {


            byte[] encryptedData = (byte[]) ois.readObject();

            String ciphertext = new String(encryptedData);


            byte[] decryptedData = decrypt(ciphertext, privateKey);


            String decryptedJsonString = new String(decryptedData);
            JSONObject blockchainJson = (JSONObject) JSONValue.parse(decryptedJsonString);


            Blockchain blockchain = new Blockchain(createGenesisBlock);
            JSONArray blocksArray = (JSONArray) blockchainJson.get("blocks");
            for (Object blockObj : blocksArray) {
                JSONObject blockJson = (JSONObject) blockObj;
                String uuid = (String) blockJson.get("uuid");
                long timestamp = (Long) blockJson.get("timestamp");
                String previousHash = (String) blockJson.get("previousHash");
                JSONObject dataJson = (JSONObject) blockJson.get("tranxLst");
                HashMap<String, String> data = new HashMap<>();
                for (Object key : dataJson.keySet()) {
                    data.put((String) key, (String) dataJson.get(key));
                }
                blockchain.addBlock(new Block(uuid, timestamp, previousHash, data));
            }

            return blockchain;
        } catch (IOException | GeneralSecurityException | ClassNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException("Reading and Decryption of blockchain from encrypted file Unsuccessful", e);
        }
    }


    private static byte[] decrypt(String ciphertext, PrivateKey privateKey) throws NoSuchPaddingException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
        // Parse ciphertext to JSON object
        JSONObject ciphertextJson = (JSONObject) JSONValue.parse(ciphertext);
        String encryptedDataBase64 = (String) ciphertextJson.get("encryptedData");
        String encryptedSymmetricKeyBase64 = (String) ciphertextJson.get("encryptedSymmetricKey");

        // Decode Base64 strings
        byte[] encryptedData = Base64.getDecoder().decode(encryptedDataBase64);
        byte[] encryptedSymmetricKey = Base64.getDecoder().decode(encryptedSymmetricKeyBase64);

        // Decrypt encrypted symmetric key
        Cipher rsaCipher = Cipher.getInstance("RSA");
        rsaCipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] decryptedSymmetricKey = rsaCipher.doFinal(encryptedSymmetricKey);

        // Reconstruct symmetric key
        SecretKeySpec secretKey = new SecretKeySpec(decryptedSymmetricKey, "AES");

        // Decrypt encrypted data
        Cipher aesCipher = Cipher.getInstance("AES");
        aesCipher.init(Cipher.DECRYPT_MODE, secretKey);
        byte[] decryptedData = aesCipher.doFinal(encryptedData);

        return decryptedData;
    }

    public static void saveBlockchainToEncryptedText(Blockchain blockchain, PublicKey publicKey, String BlockchainFile) {
        try {
            // Convert blockchain to JSON string
            String jsonString = blockchainToJsonString(blockchain);

            JSONObject ciphertextJson = encrypt(jsonString, publicKey);

            byte[] encryptedData = ciphertextJson.toJSONString().getBytes();

            // Save ciphertext to text file
            try (FileOutputStream fos = new FileOutputStream(BlockchainFile);
                ObjectOutputStream oos = new ObjectOutputStream(fos)) {
                oos.writeObject(encryptedData);
            }
        } catch (IOException | GeneralSecurityException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to save encrypted blockchain", e);
        }
    }
    
    private static JSONObject encrypt(String jsonString, PublicKey publicKey) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        // Generate symmetric key
        KeyGenerator KeyGeneration = KeyGenerator.getInstance("AES");
        KeyGeneration.init(256);
        SecretKey secretKey = KeyGeneration.generateKey();

        // Encrypt JSON string with symmetric key
        Cipher aesCipher = Cipher.getInstance("AES");
        aesCipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] encryptedData = aesCipher.doFinal(jsonString.getBytes());

        // Encrypt symmetric key with RSA public key
        Cipher rsaCipher = Cipher.getInstance("RSA");
        rsaCipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] encryptedSymmetricKey = rsaCipher.doFinal(secretKey.getEncoded());

        // Encode encrypted data and encrypted symmetric key as Base64 strings
        String encryptedDataBase64 = Base64.getEncoder().encodeToString(encryptedData);
        String encryptedSymmetricKeyBase64 = Base64.getEncoder().encodeToString(encryptedSymmetricKey);

        // Construct ciphertext in JSON format
        JSONObject ciphertextJson = new JSONObject();
        ciphertextJson.put("encryptedData", encryptedDataBase64);
        ciphertextJson.put("encryptedSymmetricKey", encryptedSymmetricKeyBase64);

        return ciphertextJson;
    }

    private static String blockchainToJsonString(Blockchain blockchain) {
        JSONObject blockchainJson = new JSONObject();
        JSONArray blocksArray = new JSONArray();
        for (Block block : blockchain.blocks) {
            JSONObject blockJson = new JSONObject();
            blockJson.put("uuid", block.getUUID());
            blockJson.put("timestamp", block.getTimestamp());
            blockJson.put("previousHash", block.getPreviousHash());
            blockJson.put("hash", block.getHash());
            blockJson.put("merkleRootHash", block.getMerkleRootHash());
            JSONObject dataJson = new JSONObject(block.getTranxLst());
            blockJson.put("tranxLst", dataJson);
            blocksArray.add(blockJson);
        }
        blockchainJson.put("blocks", blocksArray);

        return blockchainJson.toString();
    }

    public static void viewledger(Blockchain blockchain) {
        for (Block block : blockchain.blocks) {
            System.out.println("blockchain.Block UUID: " + block.getUUID());
            System.out.println("Timestamp: " + block.getTimestamp());
            System.out.println("Previous Hash: " + block.getPreviousHash());
            System.out.println("TranxLst: " + block.getTranxLst());
            System.out.println("Merkle Root Hash: " + block.getMerkleRootHash());
            System.out.println("Hash: " + block.getHash());
            System.out.println();
        }
    }
}