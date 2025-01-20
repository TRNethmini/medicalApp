package com.hospital.services;

import jakarta.mail.*;
import jakarta.mail.internet.*;
import java.util.Properties;

public class EmailService {
    private final String username = "your.email@gmail.com"; // Replace with your email
    private final String password = "your-app-password"; // Replace with your app password
    private final Properties prop;

    public EmailService() {
        prop = new Properties();
        prop.put("mail.smtp.host", "smtp.gmail.com");
        prop.put("mail.smtp.port", "587");
        prop.put("mail.smtp.auth", "true");
        prop.put("mail.smtp.starttls.enable", "true");
    }

    public boolean sendEmail(String recipientEmail, String subject, String body) {
        try {
            Session session = Session.getInstance(prop, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(username, password);
                }
            });

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
            message.setSubject(subject);
            message.setText(body);

            Transport.send(message);
            return true;
        } catch (MessagingException e) {
            e.printStackTrace();
            return false;
        }
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