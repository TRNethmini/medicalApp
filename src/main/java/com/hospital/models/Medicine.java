package com.hospital.models;

import java.time.LocalDate;

public class Medicine {
    private int id;
    private String name;
    private String description;
    private double price;
    private int quantity;
    private String manufacturer;
    private LocalDate expiryDate;
    private String category;
    private String location; // shelf/storage location
    private int minimumStock;

    public Medicine(int id, String name, String description, double price, int quantity, 
                   String manufacturer, LocalDate expiryDate, String category, 
                   String location, int minimumStock) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.quantity = quantity;
        this.manufacturer = manufacturer;
        this.expiryDate = expiryDate;
        this.category = category;
        this.location = location;
        this.minimumStock = minimumStock;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public String getManufacturer() { return manufacturer; }
    public void setManufacturer(String manufacturer) { this.manufacturer = manufacturer; }

    public LocalDate getExpiryDate() { return expiryDate; }
    public void setExpiryDate(LocalDate expiryDate) { this.expiryDate = expiryDate; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public int getMinimumStock() { return minimumStock; }
    public void setMinimumStock(int minimumStock) { this.minimumStock = minimumStock; }
}