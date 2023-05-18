package ru.bankonline.project.services;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.mockito.Mockito.verify;

@Slf4j
@ExtendWith(MockitoExtension.class)
class MailSenderTest {

    @Mock
    private JavaMailSender mailSender;
    @InjectMocks
    private MailSender mailSenderService;
    private static final String emailTo = "test@mail.ru";
    private static final String subject = "Test Subject";
    private static final String message = "Test Message";

    @Test
    void shouldSendAnEmail() {
        mailSenderService.sendEmail(emailTo, subject, message);
        // Перехват и сохранение объекта SimpleMailMessage, который был отправлен с помощью mailSender
        ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(messageCaptor.capture());

        SimpleMailMessage mailMessage = messageCaptor.getValue();
        Assertions.assertArrayEquals(new String[]{emailTo}, mailMessage.getTo());
        Assertions.assertEquals(subject, mailMessage.getSubject());
        Assertions.assertEquals(message, mailMessage.getText());
        log.info("Отправка электронного письма");
    }
}