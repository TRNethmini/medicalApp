# Project Description

**Hospital Management System** is a group project developed as part of the **Object-Oriented Programming (OOP)** module for our Higher Diploma in Information Technology. The system is designed to efficiently manage various hospital operations, including patient records, doctor schedules, appointment booking, and pharmacy inventory management.

This project was developed collaboratively by the group **Code Pioneers** to demonstrate our understanding and application of OOP principles, Java programming, and JavaFX for GUI design.

---

## Group Members

| Name                | Registration Number |
|---------------------|---------------------|
| A.N Herath          | SA22493842          |
| N.S.K Ranasinghe    | SA22492852          |
| K.A.Y.T.P.Gunawardena  | SA22446794   |
| Placeholder Name 4  | Placeholder Reg 4   |
| Placeholder Name 5  | Placeholder Reg 5   |

---
# 🏥 Hospital Management System

A comprehensive JavaFX application for managing hospital operations, including patient records, doctor schedules, appointments, and pharmacy inventory.

---

## ✨ Features

- **📊 Dashboard**: Real-time statistics
- **👥 Patient Management**:
  - Add/Edit/Delete patients
  - View patient history
  - Search patients
- **👨‍⚕️ Doctor Management**:
  - Add/Edit/Delete doctors
  - Manage specializations
  - Search doctors
- **📅 Appointment Scheduling**:
  - Schedule/Reschedule/Cancel appointments
  - View daily/weekly schedules
  - Email notifications
- **💊 Pharmacy/Medicine Inventory**:
  - Track medicine stock
  - Low stock alerts
  - Expiry date tracking
- **📧 Email Notifications**:
  - Appointment reminders
  - Prescription notifications
- **📄 Report Generation** (PDF):
  - Patient reports
  - Appointment summaries
  - Inventory reports

---

## 🛠 Technologies Used

- **Java 17**
- **JavaFX**
- **MySQL 8.0**
- **Maven**
- **iText PDF** (for report generation)
- **Jakarta Mail** (for email notifications)
- **Scene Builder** (for UI design)

---

## 📋 Prerequisites

Before running this application, make sure you have:

1. **JDK 17** or higher installed
2. **MySQL 8.0** or higher installed
3. **Maven 3.6** or higher installed
4. **Git** (optional, for cloning)

---

## 🗂 Database Setup

1. Open MySQL Workbench or MySQL Command Line.
2. Create the database:



```
   
   CREATE DATABASE hospital_db;
   -- Create users table
   CREATE TABLE users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    role VARCHAR(20) NOT NULL
   );

CREATE TABLE patients (
id INT PRIMARY KEY AUTO_INCREMENT,
name VARCHAR(100) NOT NULL,
date_of_birth DATE,
gender VARCHAR(10),
contact_number VARCHAR(20),
email VARCHAR(100),
address TEXT,
registration_date DATE
);

CREATE TABLE doctors (
id INT PRIMARY KEY AUTO_INCREMENT,
name VARCHAR(100) NOT NULL,
specialization VARCHAR(100),
contact_number VARCHAR(20),
email VARCHAR(100)
);

CREATE TABLE appointments (
id INT PRIMARY KEY AUTO_INCREMENT,
patient_id INT,
doctor_id INT,
appointment_datetime DATETIME,
notes TEXT,
status VARCHAR(20),
FOREIGN KEY (patient_id) REFERENCES patients(id),
FOREIGN KEY (doctor_id) REFERENCES doctors(id)
);

CREATE TABLE medicines (
id INT PRIMARY KEY AUTO_INCREMENT,
name VARCHAR(100) NOT NULL,
description TEXT,
price DECIMAL(10,2) NOT NULL,
quantity INT NOT NULL DEFAULT 0,
manufacturer VARCHAR(100),
expiry_date DATE NOT NULL,
category VARCHAR(50),
location VARCHAR(50),
minimum_stock INT DEFAULT 10
);

```



# 🚀 Installation & Setup

1.Clone the repository:

```


git clone https://github.com/nisalherath/medicalApp.git
cd medicalApp

```


2.Update database configuration:

Open src/main/java/com/hospital/utils/DatabaseConnection.java

Update the following values:


```

private static final String URL = "jdbc:mysql://localhost:3306/hospital_db";
private static final String USERNAME = "root";
private static final String PASSWORD = " ";

```



3.Configure email settings:

Open src/main/java/com/hospital/services/EmailService.java

Update email credentials:

```
private final String username = "your.email@gmail.com";
private final String password = "your-app-specific-password";

```



4.Build the project:


```
mvn clean install

```



5.Run the application:


```
mvn javafx:run

```





# 📧 Contact

Nisal Herath - nisal@nisal.lk

