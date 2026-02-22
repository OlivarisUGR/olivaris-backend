package com.olivaris.olivaris_app.exceptions;

public class MailSenderException extends RuntimeException {
    public MailSenderException(String emailTo) {
        super("Error al enviar el mensaje de confirmación a " + emailTo);
    }
}
