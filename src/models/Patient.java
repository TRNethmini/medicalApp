package models;

public class Patient {
    private int id;
    private String name;
    private int age;
    private String gender;
    private String address;
    private String contactNumber;
    private String email;
    private String medicalHistory;
    
    // Constructor
    public Patient(int id, String name, int age, String gender, String address, 
                  String contactNumber, String email, String medicalHistory) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.gender = gender;
        this.address = address;
        this.contactNumber = contactNumber;
        this.email = email;
        this.medicalHistory = medicalHistory;
    }
    
    // Getters and Setters
    // ... (implement all getters and setters)
}