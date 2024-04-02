package Menus;

import java.awt.Window.Type;
import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

import com.google.gson.Gson;
import com.google.gson.JsonArray;

import blockchain.Block;
import blockchain.Blockchain;

import keys.KeyGeneration;
import static files.blockchain_files.Pending_Requests_path;

import static files.blockchain_files.Users_data_Path;
import static files.blockchain_files.User_Details;
import static files.blockchain_files.System_Logs;
import static files.Blockchain_Access.retrieveBlockchainFromEncryptedText;
import static files.Blockchain_Access.saveBlockchainToEncryptedText;;


public class MainMenu {
    

	public void displaymenu(Scanner scanner) {
	    while (true) {
	        System.out.println("\n|================================================================================|");
	        System.out.println("|================================================================================|");
	        System.out.println("|			MEDICAL CREDENTIAL VERIFICATION SYSTEM			 |");
	        System.out.println("|================================================================================|");
	        System.out.println("|================================================================================|");
	        String[] options = {"Login", "SignUp as a Staff/Guest", "Exit"};

	        int choice;

	        do {
	            System.out.println("");
	            System.out.println("Welcome to the Main Menu:");
	            for (int i = 0; i < options.length; i++) {
	                System.out.println((i + 1) + ". " + options[i]);
	            }
	            System.out.print("Please enter your choice: ");
	            while (!scanner.hasNextInt()) {
	                scanner.next(); // Consume the invalid input
	            }
	            choice = scanner.nextInt();

	            if (choice >= 1 && choice <= options.length) {
	                switch (choice) {
	                    case 1:
	                        boolean loggedIn = false;
	                        User currentUser = null;
	                        while (!loggedIn) {
	                            System.out.println("");
	                            System.out.print("Enter username: ");
	                            String username = scanner.next(); // Read username directly from the scanner
	                            System.out.print("Enter password: ");
	                            String password = scanner.next(); // Read password directly from the scanner
	                            Blockchain blockchain1 = getUserCredsBlockchain();
	                            // Validate login credentials
	                            currentUser = validateLogin(username, password,blockchain1);
	                            if (currentUser != null) {
	                                loggedIn = true;
	                                Blockchain blockchain = getSystemBlockchain();
	                                addSystemLog(blockchain,currentUser.getUsername(),currentUser.getRole(),currentUser.getName(),"Login");
	                            } else {
	                                System.out.println("");
	                                System.out.println("Invalid username or password. Please try again.");
	                            }
	                        }

	                        String role = currentUser.getRole();
	                        String name = currentUser.getName();
	                        String phoneno = currentUser.getPhoneNo();
	                        String username = currentUser.getUsername();
	                        AuthUsers.displayMenu(role, username, phoneno, name,scanner);
	                        break;

	                    case 2:
	                    	 Blockchain blockchain = MainMenu.getUserCredsBlockchain();
	                        AccountManager.createGuestAccount(scanner,blockchain); 
	                        break;

	                    case 3:
	                        System.out.println("Exiting...");
	                        System.exit(0);
	                }
	            } else {
	                System.out.println("Invalid choice. Please enter a number between 1 and " + (options.length + 1) + ".");
	            }
	        } while (choice != options.length + 1);
	    }
	}
	
	public static void addSystemLog(Blockchain blockchain,String username,String role,String name,String action) {
		KeyGeneration KeyGeneration = new KeyGeneration();
        KeyPair keyPair = KeyGeneration.getKeyPair();
        PublicKey publicKey = keyPair.getPublic();
        HashMap<String, String> tranxLst = new HashMap<>();
        
        String date_of_joining = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));
        
        tranxLst.put("AccountSystemUsername", username);
        tranxLst.put("AccountSystemName",name);
        tranxLst.put("AccountSystemRole", role);
        tranxLst.put("AccountSystemAction", action);
        tranxLst.put("AccountSystemTimeStamp", date_of_joining);

        
        // Add blocks to the blockchain
        UUID uuid = UUID.randomUUID();
        blockchain.addBlock(new Block(uuid.toString(), System.currentTimeMillis(), blockchain.getLatestBlock().getHash(), tranxLst));

        // Save blockchain to ciphertext file
        saveBlockchainToEncryptedText(blockchain, publicKey, System_Logs);
	}
	
	public static void retrieveSystemLog(Blockchain blockchain) {  
        System.out.println("");
		 System.out.printf("%-20s %-20s %-20s %-20s %-20s%n", "Username", "Name", "Role", "Action", "Timestamp");
		    System.out.println("---------------------------------------------------------------------------------------");
        for (Block block : blockchain.blocks) {
            HashMap<String, String> tranxLst = block.getTranxLst();
            
            String accusername = tranxLst.get("AccountSystemUsername");
            String accname = tranxLst.get("AccountSystemName");
            String accrole = tranxLst.get("AccountSystemRole");
            String accaction = tranxLst.get("AccountSystemAction");
            String acctimestamp = tranxLst.get("AccountSystemTimeStamp");
            
            if (accusername == null || accname == null || accrole == null || accaction == null || acctimestamp == null) {
                continue;
            }
           

            // Print log entry
            System.out.printf("%-20s %-20s %-20s %-20s %-20s%n", accusername, accname, accrole, accaction, acctimestamp);
        }
        }

    
    public static Blockchain getBlockchain() {
        KeyGeneration KeyGeneration = new KeyGeneration();
        KeyPair keyPair = KeyGeneration.getKeyPair();
        PrivateKey privateKey = keyPair.getPrivate();
        Blockchain blockchain;

        File cipherFile = new File(Pending_Requests_path); // or VERIFICATION_BLOCKCHAIN_FILE_PATH
        if (cipherFile.exists() && cipherFile.length() > 0) {
            blockchain = retrieveBlockchainFromEncryptedText(privateKey, false, Pending_Requests_path);
        } else {
            blockchain = new Blockchain(true);
        }
        return blockchain;
      }
    
    public static Blockchain getSystemBlockchain() {
        KeyGeneration KeyGeneration = new KeyGeneration();
        KeyPair keyPair = KeyGeneration.getKeyPair();
        PrivateKey privateKey = keyPair.getPrivate();
        Blockchain blockchain;

        File cipherFile = new File(System_Logs); // or VERIFICATION_BLOCKCHAIN_FILE_PATH
        if (cipherFile.exists() && cipherFile.length() > 0) {
            blockchain = retrieveBlockchainFromEncryptedText(privateKey, false, System_Logs);
        } else {
            blockchain = new Blockchain(true);
        }
        return blockchain;
      }
    
    public static Blockchain getUserCredsBlockchain() {
        KeyGeneration KeyGeneration = new KeyGeneration();
        KeyPair keyPair = KeyGeneration.getKeyPair();
        PrivateKey privateKey = keyPair.getPrivate();
        Blockchain blockchain;

        File cipherFile = new File(User_Details); // or VERIFICATION_BLOCKCHAIN_FILE_PATH
        if (cipherFile.exists() && cipherFile.length() > 0) {
            blockchain = retrieveBlockchainFromEncryptedText(privateKey, false, User_Details);
        } else {
            blockchain = new Blockchain(true);
        }
        return blockchain;
      }
  
    
 
    public User validateLogin(String username, String password, Blockchain blockchain) {
        for (Block block : blockchain.blocks) {
            HashMap<String, String> tranxLst = block.getTranxLst();
            String blockUsername = tranxLst.get("AccUsername");
            String blockPassword = tranxLst.get("AccPass");

            // Check if the username and password match the data in the current block
            if (username.equals(blockUsername) && password.equals(blockPassword)) {
                // If the credentials are found in the blockchain, return the user
                return getUserFromUsername(username);
            }
        }

        // If the credentials are not found in the blockchain, return null
        return null;
    }
    
    private User getUserFromUsername(String username) {
    	Gson gson = new Gson();
        try (Reader reader = new FileReader(Users_data_Path)) {
            JsonArray jsonArray = gson.fromJson(reader, JsonArray.class);
            User[] users = gson.fromJson(jsonArray, User[].class);

            // Traverse through the users array to find the matching username and password
            for (User user : users) {
                if (user.getUsername().equals(username) ) {
                    // Return the user if username and password match
                    return user;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Return null if no matching user found
        return null;
    }
}
