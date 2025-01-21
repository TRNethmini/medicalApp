package com.hospital.services;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

public class EmailService {
    private final String username;
    private final String password;
    private final Properties properties;

    public EmailService() {
        Properties envProps = loadEnvironmentProperties();
        
        // Get email configuration from environment variables with debug logging
        this.username = getConfigValue("EMAIL_USERNAME", envProps);
        this.password = getConfigValue("EMAIL_PASSWORD", envProps);
        String host = getConfigValue("EMAIL_HOST", envProps, "smtp.gmail.com");
        String port = getConfigValue("EMAIL_PORT", envProps, "587");

        System.out.println("Email Configuration:");
        System.out.println("Username: " + (username != null ? "Found" : "Not found"));
        System.out.println("Password: " + (password != null ? "Found" : "Not found"));
        System.out.println("Host: " + host);
        System.out.println("Port: " + port);

        // Set up mail properties
        properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", port);
    }

    private Properties loadEnvironmentProperties() {
        Properties props = new Properties();
        
        // Try to load from .env file first
        try {
            FileInputStream input = new FileInputStream(".env");
            props.load(input);
            input.close();
            System.out.println("Loaded properties from .env file");
        } catch (IOException e) {
            System.out.println("No .env file found, checking system environment variables");
        }

        // Add system environment variables (will override .env file if duplicates)
        System.getenv().forEach((key, value) -> {
            props.setProperty(key, value);
        });

        return props;
    }

    private String getConfigValue(String key, Properties props) {
        return getConfigValue(key, props, null);
    }

    private String getConfigValue(String key, Properties props, String defaultValue) {
        // Try environment variable first
        String value = System.getenv(key);
        
        // If not found in environment, try properties file
        if (value == null || value.isEmpty()) {
            value = props.getProperty(key);
        }
        
        // If still not found, use default value
        if (value == null || value.isEmpty()) {
            value = defaultValue;
        }

        return value;
    }

    public boolean sendEmail(String recipient, String subject, String content) {
        // Check if email configuration exists
        if (!isConfigured()) {
            System.out.println("Email configuration is incomplete");
            return false;
        }

        try {
            // Create session with authentication
            Session session = Session.getInstance(properties, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(username, password);
                }
            });

            // Create message
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient));
            message.setSubject(subject);
            message.setText(content);

            // Send message
            Transport.send(message);
            System.out.println("Email sent successfully to: " + recipient);
            return true;
        } catch (MessagingException e) {
            e.printStackTrace();
            System.out.println("Failed to send email: " + e.getMessage());
            return false;
        }
    }

    public boolean isConfigured() {
        boolean configured = username != null && !username.isEmpty() 
                           && password != null && !password.isEmpty();
        System.out.println("Email service configured: " + configured);
        return configured;
    }

    public boolean sendAppointmentReminder(String recipientEmail, String patientName, 
                                         String doctorName, String dateTime, String notes) {
        String subject = "Appointment Reminder - Hospital Management System";
        String body = String.format("""
            Dear %s,
            
            This is a reminder for your upcoming appointment:
            
            Doctor: %s
            Date and Time: %s
            
            Additional Notes: %s
            
            Please arrive 10 minutes before your scheduled time.
            If you need to reschedule, please contact us as soon as possible.
            
            Best regards,
            Hospital Management Team
            """, patientName, doctorName, dateTime, notes);

        return sendEmail(recipientEmail, subject, body);
    }
}