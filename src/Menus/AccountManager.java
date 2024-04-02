package Menus;

import static files.Blockchain_Access.saveBlockchainToEncryptedText;
import static files.blockchain_files.Pending_Requests_path;
import static files.blockchain_files.User_Details;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.KeyPair;
import java.security.PublicKey;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;
import java.util.UUID;

import com.google.gson.Gson;

import blockchain.Block;
import blockchain.Blockchain;
import keys.KeyGeneration;

public class AccountManager {

    public static void createAccount(Scanner scanner,Blockchain blockchain) {
    	
    	KeyGeneration KeyGeneration = new KeyGeneration();
        KeyPair keyPair = KeyGeneration.getKeyPair();
        PublicKey publicKey = keyPair.getPublic();
        HashMap<String, String> tranxLst = new HashMap<>();

        System.out.println("");
        System.out.println("	SIGN UP FORM			 ");
        System.out.println("========================");
        System.out.println("");

        String username = generateUsername();

        System.out.println("");
        System.out.println("Your Unique Username is Generating");
        System.out.println("");


        System.out.println("Create Your Password:");
        scanner.nextLine();
        String password = scanner.nextLine();

        System.out.println("Choose Your Role:");
        System.out.println("1. Admin");
        System.out.println("2. Staff");
        System.out.println("3. MedicalBoard");
        System.out.println("4. Guest");
        int roleChoice = scanner.nextInt();
        String role;
        switch (roleChoice) {
            case 1:
                role = "Admin";
                break;
            case 2:
                role = "Staff";
                break;
            case 3:
                role = "MedicalBoard";
                break;
            case 4:
                role = "Guest";
                break;
            default:
                System.out.println("Invalid role choice. Setting role to 'Guest'.");
                role = "Guest";
                break;
        }

        scanner.nextLine(); // Consume newline character

        System.out.print("Enter Your Full Name:");
        String name = scanner.nextLine();

        System.out.print("Enter Your Phone Number:");
        String phone_no = scanner.nextLine();

        System.out.print("Enter Your Email Address:");
        String email_address = scanner.nextLine();

        String date_of_joining = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        User user = new User(username, role, name, email_address, phone_no, date_of_joining);
        saveUserToJson(user);
        
        tranxLst.put("AccUsername", username);
        tranxLst.put("AccPass", password);


        // Add blocks to the blockchain
        UUID uuid = UUID.randomUUID();
        blockchain.addBlock(new Block(uuid.toString(), System.currentTimeMillis(), blockchain.getLatestBlock().getHash(), tranxLst));

        // Save blockchain to ciphertext file
        saveBlockchainToEncryptedText(blockchain, publicKey, User_Details);

        System.out.println("Account Created Successfully!!!");
        System.out.println("");
        System.out.println("Your Generated Username: " + username);
        System.out.println("Your Password: " + password);

    }

    public static void createGuestAccount(Scanner scanner,Blockchain blockchain) {
    	KeyGeneration KeyGeneration = new KeyGeneration();
        KeyPair keyPair = KeyGeneration.getKeyPair();
        PublicKey publicKey = keyPair.getPublic();
        HashMap<String, String> tranxLst = new HashMap<>();

        System.out.println("");
        System.out.println("================================");
        System.out.println("   Guest and Staff Sign UP Form			 ");
        System.out.println("================================");
        System.out.println("");

        String username = generateUsername();

        System.out.println("");
        System.out.println("Your Unique Username is Generating");
        System.out.println("");


        System.out.println("Create Your Password:");
        scanner.nextLine();
        String password = scanner.nextLine();

        System.out.println("Choose Your Role:");
        System.out.println("1. Staff");
        System.out.println("2. Guest");
        int roleChoice = scanner.nextInt();
        String role;
        switch (roleChoice) {
            case 1:
                role = "Staff";
                break;
            case 2:
                role = "Guest";
                break;
            default:
                System.out.println("Invalid role choice. Setting role to 'Guest'.");
                role = "Guest";
                break;
        }

        scanner.nextLine(); // Consume newline character

        System.out.print("Enter Your Full Name:");
        String name = scanner.nextLine();

        System.out.print("Enter Your Phone Number:");
        String phone_no = scanner.nextLine();

        System.out.print("Enter Your Email Address:");
        String email_address = scanner.nextLine();

        String date_of_joining = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        User user = new User(username, role, name, email_address, phone_no, date_of_joining);
        saveUserToJson(user);
        
        tranxLst.put("AccUsername", username);
        tranxLst.put("AccPass", password);


        // Add blocks to the blockchain
        UUID uuid = UUID.randomUUID();
        blockchain.addBlock(new Block(uuid.toString(), System.currentTimeMillis(), blockchain.getLatestBlock().getHash(), tranxLst));

        // Save blockchain to ciphertext file
        saveBlockchainToEncryptedText(blockchain, publicKey, User_Details);

        System.out.println("Account Created Successfully!!!");
        System.out.println("");
        System.out.println("Your Generated Username: " + username);
        System.out.println("Your Password: " + password);

    }
    private static String generateUsername() {
        StringBuilder username = new StringBuilder();
        for (int i = 0; i < 3; i++) {
            char letter = (char) ('a' + Math.random() * ('z' - 'a' + 1));
            username.append(letter);
        }
        for (int i = 0; i < 3; i++) {
            char digit = (char) ('0' + Math.random() * ('9' - '0' + 1));
            username.append(digit);
        }
        return username.toString();
    }

    private static void saveUserToJson(User user) {
        Gson gson = new Gson();

        User[] existingUsers = loadUsersFromJSON("user_data.json");
        User[] updatedUsers;
        if (existingUsers != null) {
            updatedUsers = Arrays.copyOf(existingUsers, existingUsers.length + 1);
            updatedUsers[existingUsers.length] = user;
        } else {
            updatedUsers = new User[]{user};
        }

        try (FileWriter writer = new FileWriter("user_data.json")) {
            gson.toJson(updatedUsers, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static User[] loadUsersFromJSON(String filename) {
        Gson gson = new Gson();
        try (FileReader reader = new FileReader(filename)) {
            return gson.fromJson(reader, User[].class);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
