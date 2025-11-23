package swp391.fa25.saleElectricVehicle.service;

import jakarta.mail.MessagingException;

public interface EmailService {
    void sendVerificationLink(String to, String verificationUrl, String fullname) throws MessagingException;
}
