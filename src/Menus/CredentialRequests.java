package Menus;


import keys.KeyGeneration;
import blockchain.Block;
import blockchain.Blockchain;
import digitalSignature.Sign;

import java.io.File;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.*;

import static files.blockchain_files.Pending_Requests_path;
import static files.blockchain_files.Verified_Requests_path;
import static digitalSignature.Sign.generateDigitalSignature;
import static files.Blockchain_Access.*;

public class CredentialRequests {
    private static final Scanner scanner = new Scanner(System.in);
    private static final List<String> approvedRequests = new ArrayList<>();
    private static final List<String> rejectedRequests = new ArrayList<>();
    private static boolean hasVerificationRequests = false;

    public static void menuInterface(Blockchain requestsBlockchain, Blockchain verificationBlockchain) {
        System.out.println("");
        System.out.println("================================================================================");
        System.out.println("-----------------VERIFICATION PAGE FOR MEDICAL CREDENTIAL REQUESTS--------------");
        System.out.println("================================================================================");
        for (Block block : verificationBlockchain.blocks) {
            HashMap<String, String> tranxLst = block.getTranxLst();
            String verificationStatus = tranxLst.get("verification_status");
            if (verificationStatus != null) {
                if (verificationStatus.equalsIgnoreCase("APPROVED")) {
                    approvedRequests.add(tranxLst.get("credentialverID"));
                } else if (verificationStatus.equalsIgnoreCase("REJECTED")) {
                    rejectedRequests.add(tranxLst.get("credentialverID"));
                }
            }
        }
        viewRequestList(requestsBlockchain, verificationBlockchain);
    }

    private static void viewRequestList(Blockchain requestsBlockchain, Blockchain verificationBlockchain) {
        displayPendingRequests(requestsBlockchain);

        boolean continueVerification = true;
        if (!hasVerificationRequests) {
            System.out.println("There are no Credential Verification Requests as of now!");
            // Back to Access Menu
        } else {
            while (continueVerification) {
                System.out.println("Would you like to proceed to verification process (y/n): ");
                String verifyResponse = getUserInput();

                switch (verifyResponse) {
                    case "y":
                        System.out.println("Please enter the Medical Credential Request Number: ");
                        String verReqNum = getUserInput();
                        verificationProcess(requestsBlockchain, verificationBlockchain, verReqNum);
                        break;
                    case "n":
                        System.out.println("Verification Process End");
                        continueVerification = false;
                        // Back to Access Menu
                        break;
                    default:
                        System.out.println("Please input a valid response...");
                }
            }
        }
    }

    private static void verificationProcess(Blockchain requestsBlockchain, Blockchain verificationBlockchain, String verReqNum) {
        KeyGeneration KeyGeneration = new KeyGeneration();
        KeyPair keyPair = KeyGeneration.getKeyPair();
        PrivateKey privateKey = keyPair.getPrivate();

        String credentialverID = "";
        String name = "";
        String phone_num = "";
        String username = "";
        String credential_type="";
        String user_qualification="";
        
        String verification_status = "";
        for (Block block : requestsBlockchain.blocks) {
            HashMap<String, String> tranxLst = block.getTranxLst();

            if (!Objects.equals(block.getUUID(), verReqNum)) {
                continue;
            }
            System.out.println("\nMedical Credential Verification Request Number: " + block.getUUID());
            System.out.println("Timestamp: " + block.getTimestamp());
            System.out.println("Name: " + tranxLst.get("userName"));
            System.out.println("Phone Number: " + tranxLst.get("userNumber"));
            System.out.println("Username: " + tranxLst.get("data"));
            System.out.println("Credential Request Type: " + tranxLst.get("userCredentialType")); 
            System.out.println("User Qualifications: " + tranxLst.get("userQualification")); 
            
            System.out.println("Verification Status: " + tranxLst.get("verification_status").toUpperCase());
            System.out.println();

            credentialverID = block.getUUID();
            name = tranxLst.get("userName");
            phone_num = tranxLst.get("userNumber");
            username = tranxLst.get("data");
            credential_type= tranxLst.get("userCredentialType"); 
            user_qualification= tranxLst.get("userQualification"); 
        }   
        String verificationData = credentialverID + name + phone_num+credential_type + verification_status;
        byte[] signature = generateDigitalSignature(verificationData, privateKey);

        System.out.println("Would you like to approve this application? (y/n): ");
        String response = getUserInput();

        switch (response) {
            case "y":
                verification_status = "APPROVED";
                approvedRequests.add(verReqNum);
                break;
            case "n":
                verification_status = "REJECTED";
                rejectedRequests.add(verReqNum);
                break;
            default:
                verification_status = "PENDING";
        }
        verificationBlockDataInput(verificationBlockchain, requestsBlockchain, credentialverID, name, phone_num, username,credential_type,user_qualification, verification_status, signature);
    }

    private static void verificationBlockDataInput(Blockchain verificationBlockchain, Blockchain requestsBlockchain, String credentialverID, String name, String phone_num, String username,String credential_type,String user_qualification, String verification_status, byte[] signature) {
    	KeyGeneration KeyGeneration = new KeyGeneration();
        KeyPair keyPair = KeyGeneration.getKeyPair();
        PublicKey publicKey = keyPair.getPublic();

        HashMap<String, String> tranxLst = new HashMap<>();
        tranxLst.put("credentialverID", credentialverID);
        tranxLst.put("userName", name);
        tranxLst.put("userNumber", phone_num);
        tranxLst.put("userQualification", user_qualification);
        tranxLst.put("userCredentialType", credential_type);   
        tranxLst.put("data", username);   
        tranxLst.put("verification_status", verification_status);
        tranxLst.put("digital_signature", Base64.getEncoder().encodeToString(signature));
        tranxLst.put("verified_timestamp", String.valueOf(System.currentTimeMillis()));

        // Add blocks to the blockchain
        UUID uuid = UUID.randomUUID();
        verificationBlockchain.addBlock(new Block(uuid.toString(), System.currentTimeMillis(), verificationBlockchain.getLatestBlock().getHash(), tranxLst));

        // Save blockchain to ciphertext file
        saveBlockchainToEncryptedText(verificationBlockchain, publicKey, Verified_Requests_path);
        System.out.println("Verification Request Response Successfully Stored!");
        menuInterface(requestsBlockchain, verificationBlockchain);
    }

    public static void displayPendingRequests(Blockchain blockchain) {
        boolean hasPendingRequests = false;
        for (Block block : blockchain.blocks) {
            HashMap<String, String> tranxLst = block.getTranxLst();
            String data = tranxLst.get("data");
            String verification_status = tranxLst.get("verification_status");

            // Skip the block if data is "Genesis blockchain.Block"
            if ("Genesis blockchain.Block".equals(data) || approvedRequests.contains(block.getUUID()) || rejectedRequests.contains(block.getUUID()) || !"PENDING".equalsIgnoreCase(verification_status)) {
                continue;
            }
            System.out.println("");
            System.out.println("Medical Credential Verification Request ID Number: " + block.getUUID());
            System.out.println("Timestamp: " + block.getTimestamp());
            System.out.println("Name: " + tranxLst.get("userName"));
            System.out.println("Phone Number: " + tranxLst.get("userNumber"));
            System.out.println("Credential Type: " + tranxLst.get("userCredentialType"));
            System.out.println("User Qualification: " + tranxLst.get("userQualification"));
            System.out.println("Username: " + tranxLst.get("data"));

            System.out.println("Verification Status: " + verification_status.toUpperCase());
            System.out.println("--------------------");
            System.out.println();
            hasPendingRequests = true;
        }
        hasVerificationRequests = hasPendingRequests;
    }
    
    public static void displayApprovedRequests(Blockchain blockchain) {
        boolean hasPendingRequests = false;
        for (Block block : blockchain.blocks) {
            HashMap<String, String> tranxLst = block.getTranxLst();
            String data = tranxLst.get("data");
            String verification_status = tranxLst.get("verification_status");

            // Skip the block if data is "Genesis blockchain.Block"
            if("Genesis blockchain.Block".equals(data) || rejectedRequests.contains(block.getUUID()) || "REJECTED".equalsIgnoreCase(verification_status)) {
                continue;
            }
            System.out.println("");
            System.out.println("Medical Credential Verification Request ID Number: " + block.getUUID());
            System.out.println("Timestamp: " + block.getTimestamp());                                   
            System.out.println("Name: " + tranxLst.get("userName"));                                    
            System.out.println("Phone Number: " + tranxLst.get("userNumber"));                          
            System.out.println("Credential Type: " + tranxLst.get("userCredentialType"));               
            System.out.println("User Qualification: " + tranxLst.get("userQualification"));             
            System.out.println("Username: " + tranxLst.get("data"));                                    
            System.out.println("Signature: " + tranxLst.get("digital_signature"));                      
            System.out.println("Verification Status: " + verification_status.toUpperCase());            
            System.out.println("Verification Timestamp: " + tranxLst.get("verified_timestamp"));        
            System.out.println();                                                                       
        }
        hasVerificationRequests = hasPendingRequests;
    }
    
    public static void displayRejectedRequests(Blockchain blockchain) {
        boolean hasPendingRequests = false;
        for (Block block : blockchain.blocks) {
            HashMap<String, String> tranxLst = block.getTranxLst();
            String data = tranxLst.get("data");
            String verification_status = tranxLst.get("verification_status");

            // Skip the block if data is "Genesis blockchain.Block"
            if("Genesis blockchain.Block".equals(data) || approvedRequests.contains(block.getUUID()) || "APPROVED".equalsIgnoreCase(verification_status)) {
                continue;
            }
        	System.out.println("");
            System.out.println("Medical Credential Verification Request ID Number: " + block.getUUID());
            System.out.println("Timestamp: " + block.getTimestamp());                                   
            System.out.println("Name: " + tranxLst.get("userName"));                                    
            System.out.println("Phone Number: " + tranxLst.get("userNumber"));                          
            System.out.println("Credential Type: " + tranxLst.get("userCredentialType"));               
            System.out.println("User Qualification: " + tranxLst.get("userQualification"));             
            System.out.println("Username: " + tranxLst.get("data"));                                    
            System.out.println("Signature: " + tranxLst.get("digital_signature"));                      
            System.out.println("Verification Status: " + verification_status.toUpperCase());            
            System.out.println("Verification Timestamp: " + tranxLst.get("verified_timestamp"));        
            System.out.println();                                                                       
        }
        hasVerificationRequests = hasPendingRequests;
    }
    
    public static boolean searchRequests(Blockchain blockchain, String username) {
        boolean hasPendingRequests = false;
        for (Block block : blockchain.blocks) {
            HashMap<String, String> tranxLst = block.getTranxLst();
            String data = tranxLst.get("data");

            // Skip the block if data is "Genesis blockchain.Block" or username doesn't match
            if ("Genesis blockchain.Block".equals(data) || !username.equals(tranxLst.get("data"))) {
                continue;
            }
            System.out.println("");
            // Display the information for the matching username
            System.out.println("Medical Credential Verification Request ID Number: " + block.getUUID());
            System.out.println("Name: " + tranxLst.get("userName"));
            System.out.println("Credential Type: " + tranxLst.get("userCredentialType"));
            System.out.println("User Qualification: " + tranxLst.get("userQualification"));
            System.out.println("Verification Status: " + tranxLst.get("verification_status").toUpperCase());
            System.out.println();

            // Set flag to indicate there are pending requests
            hasPendingRequests = true;
        }
        return hasPendingRequests;
    }


    private static String getUserInput() {
        return scanner.nextLine();
    }

}