package com.olivaris.olivaris_app.services;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;

import com.olivaris.olivaris_app.exceptions.MailSenderException;
import com.olivaris.olivaris_app.models.enums.EntityRoleTypes;
import com.olivaris.olivaris_app.models.enums.RoleTypes;
import com.olivaris.olivaris_app.repositories.UserRepository;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service 
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    private final String from;
    private static final String EMAIL_SUBJECT = "Confirmación de registro en la aplicación Olivaris";
    private static final String EMAIL_TEMPLATE_PATH = "templates/confirmation-email.html";
    private final UserRepository userRep;

    public EmailServiceImpl(
        JavaMailSender mailSender,
        @Value("${spring.mail.username}") String from,
        UserRepository userRep
    ) {
        this.mailSender = mailSender;
        this.from = from;
        this.userRep = userRep;
    }
    
    @Override
    @Async
    public void sendEmail(String to, String url, String firstname) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setText(emailBody(url, firstname), true);
            helper.setFrom(from != null ? from : "");
            helper.setTo(to != null ? to : "");
            helper.setSubject(EMAIL_SUBJECT);
            mailSender.send(mimeMessage);
        } catch(MessagingException e) {
            throw new MailSenderException(to);
        }
    }

    @Override
    @Async
    public void sendEmailToAdmins(String url, String entityNif) {
        // Send an email to all admin users 
        List<String> adminEmails = userRep.getEmailByRol(RoleTypes.ROLE_ADMIN.toString());

        adminEmails.stream()
            .forEach(e -> this.sendEmail(e, url, null));

        // Send an email to all entity admin that belong to the same entity as an admin
        List<String> entityAdminEmails = userRep.getEmailEntityAdmins(
            RoleTypes.ROLE_ENTITY_ADMIN.toString(), 
            EntityRoleTypes.ROLE_ADMIN.toString(), 
            entityNif
        );

        if(!entityAdminEmails.isEmpty()) {
            entityAdminEmails.stream()
                .forEach(e -> this.sendEmail(e, url, null));
        }
    }
    
    private String emailBody(String url, String firstname) {
        try {
            ClassPathResource resource = new ClassPathResource(EMAIL_TEMPLATE_PATH);
            String template = StreamUtils.copyToString(
                resource.getInputStream(),
                StandardCharsets.UTF_8
            );

            String name = firstname != null ? firstname : "Administrador";

            return template
                    .replace("{{firstname}}", name)
                    .replace("{{confirmationUrl}}", url);
        } catch(IOException e) {
            throw new MailSenderException("Error al cargar la plantilla del email");
        }
    }
}
