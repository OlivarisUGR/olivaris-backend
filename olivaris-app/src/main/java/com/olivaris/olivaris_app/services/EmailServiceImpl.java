package com.olivaris.olivaris_app.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.olivaris.olivaris_app.exceptions.MailSenderException;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service 
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    private final String from;
    private static final String EMAIL_SUBJECT = "Confirmación de registro en la aplicación Olivaris";

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
        return """
            <!DOCTYPE html>
            <html lang="es">
            <head>
                <meta charset="UTF-8">
                <title>Confirmación de cuenta</title>
            </head>
            <body style="margin:0; padding:0; font-family: Arial, sans-serif; background-color:#f4f4f4;">
                <table width="100%%" cellpadding="0" cellspacing="0">
                    <tr>
                        <td align="center" style="padding: 40px 0;">
                            <table width="600" cellpadding="0" cellspacing="0" style="background-color:#ffffff; border-radius:8px; overflow:hidden;">
                                
                                <tr>
                                    <td style="padding: 30px; text-align:center; background-color:#1B6E21; color:#ffffff;">
                                        <h1 style="margin:0;">Bienvenido a Olivaris</h1>
                                    </td>
                                </tr>

                                <tr>
                                    <td style="padding: 30px; color:#333333;">
                                        <p>Hola, %s</p>

                                        <p>
                                            Gracias por registrarte. Para activar tu cuenta, por favor confirma tu
                                            dirección de correo electrónico haciendo clic en el siguiente botón:
                                        </p>

                                        <p style="text-align:center; margin: 40px 0;">
                                            <a href="%s"
                                            style="
                                                background-color:#33CC3E;
                                                color:#ffffff;
                                                padding:15px 25px;
                                                text-decoration:none;
                                                border-radius:5px;
                                                display:inline-block;
                                                font-weight:bold;
                                            ">
                                                Confirmar cuenta
                                            </a>
                                        </p>

                                        <p>
                                            Si no has creado una cuenta, puedes ignorar este mensaje.
                                        </p>

                                        <p style="font-size:12px; color:#777777;">
                                            Este enlace expirará en 24 horas.
                                        </p>
                                    </td>
                                </tr>

                                <tr>
                                    <td style="padding: 20px; text-align:center; background-color:#f0f0f0; font-size:12px; color:#777;">
                                        © 2026 Olivaris. Todos los derechos reservados.
                                    </td>
                                </tr>

                            </table>
                        </td>
                    </tr>
                </table>
            </body>
            </html>
        """.formatted(firstname, url);
    }
}
