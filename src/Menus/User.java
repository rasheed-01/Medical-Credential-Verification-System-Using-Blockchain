package Menus;

public class User {
    private String username;
    private String role;
    private String name;
    private String emailAddress;
    private String phoneNo;
    private String dateOfJoining;

    // Constructor
    public User(String username, String role, String name, String emailAddress, String phoneNo, String dateOfJoining) {
        this.username = username;
        this.role = role;
        this.name = name;
        this.emailAddress = emailAddress;
        this.phoneNo = phoneNo;
        this.dateOfJoining = dateOfJoining;
    }

    // Getters
    public String getUsername() {
        return username;
    }

    public String getRole() {
        return role;
    }

    public String getName() {
        return name;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public String getDateOfJoining() {
        return dateOfJoining;
    }

    // Setters
    public void setUsername(String username) {
        this.username = username;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public void setDateOfJoining(String dateOfJoining) {
        this.dateOfJoining = dateOfJoining;
    }
}
