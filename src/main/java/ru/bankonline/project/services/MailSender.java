package ru.bankonline.project.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/***
 * Класс MailSender для отправки электронных писем
 */
@Service
public class MailSender {

    @Value("${spring.mail.username}")
    private String username;

    private final JavaMailSender sender;

    @Autowired
    public MailSender(JavaMailSender mailSender) {
        this.sender = mailSender;
    }

    /***
     * Реализует отправку электронного письма
     * @param emailTo адрес электронной почты, на который следует отправить письмо
     * @param subject тема письма
     * @param message сообщение
     */
    public void sendEmail(String emailTo, String subject, String message) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();

        mailMessage.setFrom(username);
        mailMessage.setTo(emailTo);
        mailMessage.setSubject(subject);
        mailMessage.setText(message);

        sender.send(mailMessage);
    }
}