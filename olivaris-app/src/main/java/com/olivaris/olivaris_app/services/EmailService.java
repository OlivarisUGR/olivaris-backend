package com.olivaris.olivaris_app.services;

public interface EmailService {
    void sendEmail(String to, String url, String firstname);
    void sendEmailToAdmins(String url, String entityNif);
}
