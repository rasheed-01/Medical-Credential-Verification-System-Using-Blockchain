package Menus;

import com.google.gson.Gson;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;

public class UpdateAccount {

    public static void displayAndUpdateUser(String username,Scanner scanner) {
        // Load users from JSON file
        User[] users = loadUsersFromJSON("user_data.json");
        if (users == null) {
            System.out.println("Error: Unable to load user data.");
            return;
        }

        for (int i = 0; i < users.length; i++) {
            User user = users[i];
            if (user.getUsername().equals(username)) {
                // Display user details
                System.out.println("");
                System.out.println("Your Account Details:");
                System.out.println("Username: " + user.getUsername());
                System.out.println("Name: " + user.getName());
                System.out.println("Role: " + user.getRole());
                System.out.println("Email: " + user.getEmailAddress());
                System.out.println("Contact Info: " + user.getPhoneNo());
                System.out.println("");

                // Offer options to update user information
                System.out.println("Options:");
                System.out.println("1. Update Name");
                System.out.println("2. Update Email");
                System.out.println("3. Update Number");
                System.out.println("4. Return to Main Menu");

                // Get user choice
                System.out.print("Enter option: ");
                int choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline

                switch (choice) {
                    case 1:
                        System.out.print("Enter Your New Name: ");
                        String newName = scanner.nextLine();
                        user.setName(newName);
                        break;
                    case 2:
                        System.out.print("Enter New Email: ");
                        String newEmail = scanner.nextLine();
                        user.setEmailAddress(newEmail);
                        break;
                    case 3:
                        System.out.print("Enter New Contact No.: ");
                        String newNumber = scanner.nextLine();
                        user.setPhoneNo(newNumber);
                        break;
                    case 4:
                        return;
                    default:
                        System.out.println("Invalid option!");
                        break;
                }

                // Save updated user details to JSON file
                saveUsersToJSON(users, "user_data.json");
                System.out.println("Your Details Have Been Updated Successfully!");
                return;
            }
        }

        // If user data not found
        System.out.println("User account not found.");
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

    private static void saveUsersToJSON(User[] users, String filename) {
        Gson gson = new Gson();
        User[] existingUsers = loadUsersFromJSON(filename);
        User[] updatedUsers;
        
        // Check if there are existing users
        if (existingUsers != null) {
            // Iterate through existing users to find and update the matching user
            for (int i = 0; i < existingUsers.length; i++) {
                for (User newUser : users) {
                    if (existingUsers[i].getUsername().equals(newUser.getUsername())) {
                        existingUsers[i] = newUser; // Update the existing user
                        break;
                    }
                }
            }
            updatedUsers = existingUsers; // Set updated users to existing users with updated records
        } else {
            updatedUsers = users; // If no existing users, use the provided users
        }
        
        // Write the updated users to the file
        try (FileWriter writer = new FileWriter(filename)) {
            gson.toJson(updatedUsers, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
