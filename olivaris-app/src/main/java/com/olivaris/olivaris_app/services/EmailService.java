package com.olivaris.olivaris_app.services;

public interface EmailService {
    void sendEmailToOthers(String to, String url, String firstname);
    void sendEmailToAdmins(String url, String entityNif, String userEmail);
}
