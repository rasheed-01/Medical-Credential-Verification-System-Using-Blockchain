package blockchain;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


class Hasher {
    public static String sha512(String data) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-512");
            byte[] hash = digest.digest(data.getBytes());

            // Convert byte array to a hexadecimal string
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }

            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            // Handle exception (e.g., log it or throw a runtime exception)
            e.printStackTrace();
            return null;
        }
    }
}
