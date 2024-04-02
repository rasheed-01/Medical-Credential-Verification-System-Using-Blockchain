package Menus;

import blockchain.Block;
import blockchain.Blockchain;
import java.security.PublicKey;
import java.util.HashMap;
import java.util.Scanner;
import java.util.UUID;
import keys.KeyGeneration;
import java.security.KeyPair;


import static files.blockchain_files.Pending_Requests_path;
import static files.Blockchain_Access.saveBlockchainToEncryptedText;

public class MedicalCredentialForm {
    public static void fillCredentialForm(Blockchain blockchain,String username,String phoneno,String name,Scanner scanner) {
    	KeyGeneration KeyGeneration = new KeyGeneration();
        KeyPair keyPair = KeyGeneration.getKeyPair();
        PublicKey publicKey = keyPair.getPublic();
        HashMap<String, String> tranxLst = new HashMap<>();
        
        System.out.println("");
        System.out.println("Your Information");
        System.out.println("");
        
        System.out.println("Name: "+name);
        System.out.println("Phone No. : "+phoneno);
        System.out.println("Username: "+username);
        
        scanner.nextLine();
        System.out.println("Your Highest Quaification:");
        String qualification =  scanner.nextLine();

        
        System.out.println("Choose Credential Type:");
        System.out.println("1. Physician And Surgeon License");
        System.out.println("2. Nursing Assistant Certification");
        System.out.println("3. Dietitian Certification");
        System.out.println("4. Registered Nurse Temporary Practice Permit");
        System.out.println("5. Veterinary License");
        System.out.println("6. Massage Therapist License");
        System.out.println("7. Other");
        System.out.print("Enter your choice (1-7): ");
        int choice = scanner.nextInt();
        scanner.nextLine();

        String credentialType = "";
        switch (choice) {
            case 1:
                credentialType = "Physician And Surgeon License";
                break;
            case 2:
                credentialType = "Nursing Assistant Certification";
                break;
            case 3:
                credentialType = "Dietitian Certification";
                break;
            case 4:
                credentialType = "Registered Nurse Temporary Practice Permit";
                break;
            case 5:
                credentialType = "Veterinary License";
                break;
            case 6:
                credentialType = "Massage Therapist License";
                break;
            case 7:
                credentialType = "Other";
                break;
            default:
                System.out.println("Invalid choice. Choosing Other.");
                credentialType = "Other";
        }


        tranxLst.put("data", username);
        
        tranxLst.put("userName",name);
        tranxLst.put("userNumber", phoneno);
        tranxLst.put("userQualification", qualification);
        tranxLst.put("userCredentialType", credentialType);
        tranxLst.put("verification_status", "Pending");
        tranxLst.put("digi_signature", "0");
        tranxLst.put("verification_timestamp", "0");


        // Add blocks to the blockchain
        UUID uuid = UUID.randomUUID();
        blockchain.addBlock(new Block(uuid.toString(), System.currentTimeMillis(), blockchain.getLatestBlock().getHash(), tranxLst));

        // Save blockchain to ciphertext file
        saveBlockchainToEncryptedText(blockchain, publicKey, Pending_Requests_path);
        System.out.println("Medical Credential Verification Request Sent Successfully ");
        
    }

   
}
