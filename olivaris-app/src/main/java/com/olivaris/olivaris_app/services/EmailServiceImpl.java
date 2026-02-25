package com.olivaris.olivaris_app.services;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;

import com.olivaris.olivaris_app.exceptions.MailSenderException;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service 
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    private final String from;
    private static final String EMAIL_SUBJECT = "Confirmación de registro en la aplicación Olivaris";
    private static final String EMAIL_TEMPLATE_PATH = "templates/confirmation-email.html";

    public EmailServiceImpl(JavaMailSender mailSender,
                            @Value("${spring.mail.username}") String from 
    ) {
        this.mailSender = mailSender;
        this.from = from;
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

    
    private String emailBody(String url, String firstname) {
        try {
            ClassPathResource resource = new ClassPathResource(EMAIL_TEMPLATE_PATH);
            String template = StreamUtils.copyToString(
                resource.getInputStream(),
                StandardCharsets.UTF_8
            );

            return template
                    .replace("{{firstname}}", firstname)
                    .replace("{{confirmationUrl}}", url);
        } catch(IOException e) {
            throw new MailSenderException("Error al cargar la plantilla del email");
        }
    }
}
