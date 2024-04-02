package Menus;


import keys.KeyGeneration;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.*;

import com.google.gson.Gson;

import static files.blockchain_files.Pending_Requests_path;
import static files.blockchain_files.Verified_Requests_path;
import static files.blockchain_files.Users_data_Path;
import static files.Blockchain_Access.*;

import blockchain.Block;
import blockchain.Blockchain;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
public abstract class AuthUsers extends AccessMenu {

    public static void displayMenu(String role,String username, String phoneno, String name,Scanner scanner) {
    	boolean exit = false;
    	
    	 while (!exit) {
        switch (role) {
            case "Admin":
            	System.out.println("");
                System.out.println("Welcome, Admin!");
                System.out.println("");
                System.out.println("Admin Menu");
                System.out.println("----------");
                System.out.println("1. Create Account");
                System.out.println("2. Manage User Accounts");
                System.out.println("3. Update Account Details");
                System.out.println("4. Access System Log");
                System.out.println("5. Logout");
                
                System.out.print("Enter your choice: ");
                int choice = scanner.nextInt();
                switch (choice) {
                    case 1:
                    	Blockchain blockchain = MainMenu.getUserCredsBlockchain();
                    	AccountManager.createAccount(scanner,blockchain);
                        break;
                    case 2:
                    	displayDirectory();
                        break;
                    case 3:
                        UpdateAccount.displayAndUpdateUser(username,scanner);
                        break;
                    case 4:
                    	Blockchain blockchain1 = MainMenu.getSystemBlockchain();
                    	MainMenu.retrieveSystemLog(blockchain1);
                        break;
                    case 5:
                    	Blockchain blockchain2 = MainMenu.getSystemBlockchain();
                   	 	MainMenu.addSystemLog(blockchain2, username, role, name,"Logout");
                    	exit=true;
                        break;
                    default:
                        System.out.println("Invalid choice.");
                }
                break;
            case "Staff":
            	System.out.println("");
                System.out.println("Welcome, "+name);
                System.out.println("");
                System.out.println("Medical Staff Menu");
                System.out.println("-------------------");
                System.out.println("1. Apply For Medical Credential Approval");
                System.out.println("2. Check Status of Medical Credential Approval Request");
                System.out.println("3. Update Account Details");
                System.out.println("4. Logout");
                System.out.print("Enter your choice: ");
                int choice2 = scanner.nextInt();
                switch (choice2) {
                    case 1:
                        Blockchain blockchain = MainMenu.getBlockchain();
                        MedicalCredentialForm.fillCredentialForm(blockchain,username,phoneno,name,scanner);
                        break;
                    case 2:
                    	searchCredentialRequests(username);
                        break;
                    case 3:
                        UpdateAccount.displayAndUpdateUser(username,scanner);
                        break;
                    case 4:
                   	 	Blockchain blockchain1 = MainMenu.getSystemBlockchain();
                   	 	MainMenu.addSystemLog(blockchain1, username, role, name,"Logout");
                    	exit=true;
                        break;
                    default:
                        System.out.println("Invalid choice.");
                }
                break;
            case "Guest":
            	System.out.println("");
                System.out.println("Welcome, "+name);
                System.out.println("");
                System.out.println("Guest Menu");
                System.out.println("----------");
                System.out.println("1. View Staff Directory");
                System.out.println("2. Update Account Details");
                System.out.println("3. Logout");
                System.out.print("Enter your choice: ");
                int choice3 = scanner.nextInt();
                switch (choice3) {
                    case 1:
                    	displayStaffDirectory();
                        break;
                    case 2:
                        UpdateAccount.displayAndUpdateUser(username,scanner);
                        break;
                    case 3:
                   	 	Blockchain blockchain = MainMenu.getSystemBlockchain();
                   	 	MainMenu.addSystemLog(blockchain, username, role, name,"Logout");
                   	 	exit = true;
                        break;
                    default:
                        System.out.println("Invalid choice.");
                }
                break;
            case "MedicalBoard":
            	 System.out.println("");
            	 System.out.println("Welcome, "+name);
                 System.out.println("");
                 System.out.println("Medical Board Verifier Menu");
                 System.out.println("---------------------------");
                 System.out.println("1. View Pending Medical Credential Requests");
                 System.out.println("2. View Approved Medical Credential Requests");
                 System.out.println("3. View Rejected Medical Credential Requests");
                 System.out.println("4. Manage Requests");
                 System.out.println("5. Update Account Details");
                 System.out.println("6. Logout");
                 System.out.print("Enter your choice: ");
                 int choice4 = scanner.nextInt();
                 switch (choice4) {
                     case 1:
                    	 handleViewPendingRequests();
                         break;
                     case 2:
                    	 handleViewApprovedRequests();
                         break;
                     case 3:
                    	 handleViewRejectedRequests();
                         break;
                     case 4:
                    	 handleManageRequests();
                         break;
                     case 5:
                        UpdateAccount.displayAndUpdateUser(username,scanner);
                         break;
                     case 6:
                    	 Blockchain blockchain = MainMenu.getSystemBlockchain();
                    	 MainMenu.addSystemLog(blockchain, username, role, name,"Logout");
                    	 exit = true;
                         break;
                     default:
                         System.out.println("Invalid choice.");
                 }
                 break;
            default:
                System.out.println("Unknown role.");
        }
    }
    }
    
    private static void handleViewPendingRequests() {
        Blockchain requestsBlockchain;
        KeyGeneration keyGeneration = new KeyGeneration();
        KeyPair keyPair = keyGeneration.getKeyPair();
        PrivateKey privateKey = keyPair.getPrivate();

        File requestsFile = new File(Pending_Requests_path);
        if (requestsFile.exists() && requestsFile.length() > 0) {
            requestsBlockchain = retrieveBlockchainFromEncryptedText(privateKey, false, Pending_Requests_path);
            CredentialRequests.displayPendingRequests(requestsBlockchain);
            if (requestsBlockchain == null) {
                System.out.println("There are no Pending Verification Requests");
            }
        }
    }
    
    private static void handleViewApprovedRequests() {
        Blockchain verificationBlockchain;
        KeyGeneration keyGeneration = new KeyGeneration();
        KeyPair keyPair = keyGeneration.getKeyPair();
        PrivateKey privateKey = keyPair.getPrivate();

        File requestsFile = new File(Verified_Requests_path);
        if (requestsFile.exists() && requestsFile.length() > 0) {
        	verificationBlockchain = retrieveBlockchainFromEncryptedText(privateKey, false, Verified_Requests_path);
            CredentialRequests.displayApprovedRequests(verificationBlockchain);
        } else {
        	System.out.println("");
            System.out.println("There are no Verification Requests Approved as of now!");
        }
    }
    
    private static void handleViewRejectedRequests() {
        Blockchain verificationBlockchain;
        KeyGeneration keyGeneration = new KeyGeneration();
        KeyPair keyPair = keyGeneration.getKeyPair();
        PrivateKey privateKey = keyPair.getPrivate();

        File requestsFile = new File(Verified_Requests_path);
        if (requestsFile.exists() && requestsFile.length() > 0) {
        	verificationBlockchain = retrieveBlockchainFromEncryptedText(privateKey, false, Verified_Requests_path);
            CredentialRequests.displayRejectedRequests(verificationBlockchain);
        } else {
        	System.out.println("");
            System.out.println("There are no Verification Requests Rejected as of now!");
        }
    }
    
    private static void searchCredentialRequests(String username) {
        Blockchain verificationBlockchain;
        KeyGeneration keyGeneration = new KeyGeneration();
        KeyPair keyPair = keyGeneration.getKeyPair();
        PrivateKey privateKey = keyPair.getPrivate();

        File requestsFile = new File(Verified_Requests_path);
        if (requestsFile.exists() && requestsFile.length() > 0) {
        	verificationBlockchain = retrieveBlockchainFromEncryptedText(privateKey, false, Verified_Requests_path);
        	CredentialRequests.searchRequests(verificationBlockchain, username);
        	if (!CredentialRequests.searchRequests(verificationBlockchain, username)) {
            	System.out.println("");
        	    System.out.println("Your Credential Request is Still Under Process!");
        	}
        }
    }
    
    public static void displayStaffDirectory() {
        // Load users from JSON file
        User[] users = loadUsersFromJSON("user_data.json");
        if (users == null) {
            System.out.println("Error: Unable to load user data.");
            return;
        }

        // Filter staff users
        User[] staffUsers = Arrays.stream(users)
                .filter(user -> user.getRole().equals("Staff"))
                .toArray(User[]::new);

        // Display staff directory
        System.out.println("");
        System.out.println("Staff Directory:");
        System.out.printf("%-20s %-30s %-15s%n", "Name", "Email", "Phone Number");
        for (User user : staffUsers) {
            System.out.printf("%-20s %-30s %-15s%n", user.getName(), user.getEmailAddress(), user.getPhoneNo());
        }
    }
    
    public static void displayDirectory() {
        // Load users from JSON file
        User[] users = loadUsersFromJSON("user_data.json");
        if (users == null) {
            System.out.println("Error: Unable to load user data.");
            return;
        }

        // Display all users
        System.out.println("");
        System.out.println("System Users:");
        System.out.printf("%-20s %-25s %-35s %-20s %-15s%n", "Username", "Name", "Email", "Role", "Phone Number");
        for (User user : users) {
            System.out.printf("%-20s %-25s %-35s %-20s %-15s%n", user.getUsername(), user.getName(), user.getEmailAddress(), user.getRole(), user.getPhoneNo());
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
       
    
    private static void handleManageRequests() {
        Blockchain requestsBlockchain;
        Blockchain verificationBlockchain;
        KeyGeneration keyGeneration = new KeyGeneration();
        KeyPair keyPair = keyGeneration.getKeyPair();
        PrivateKey privateKey = keyPair.getPrivate();

        File requestsFile = new File(Pending_Requests_path);
        File verificationFile = new File(Verified_Requests_path);
        if (requestsFile.exists() && requestsFile.length() > 0) {
            requestsBlockchain = retrieveBlockchainFromEncryptedText(privateKey, false, Pending_Requests_path);
            if (verificationFile.exists() && verificationFile.length() > 0) {
                verificationBlockchain = retrieveBlockchainFromEncryptedText(privateKey, false, Verified_Requests_path);
            } else {
                verificationBlockchain = new Blockchain(true);
            }
            CredentialRequests.menuInterface(requestsBlockchain, verificationBlockchain);
        } else {
            System.out.println("There are no Verification Requests as of now!");
        }
    }
}

