package com.example.notificationService.service;

import com.example.core.event.users.UserRegisteredEvent;
import com.example.core.exception.NonRetryableException;
import com.example.core.exception.RetryableException;
import com.example.notificationService.model.NotificationLog;
import com.example.notificationService.repository.NotificationRepository;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.messaging.MessagingException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final NotificationRepository notificationRepository;
    private final JavaMailSender javaMailSender;

    @Transactional
    public void sendWelcomeEmail(UserRegisteredEvent event){
        NotificationLog notificationLog = NotificationLog.builder()
                .userId(event.getUserId())
                .email(event.getEmail())
                .type("WELCOME_EMAIL")
                .status("SENDING")
                .build();

        notificationRepository.save(notificationLog);

        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(event.getEmail());
            helper.setSubject("Добро пожаловать в наш сервис!");
            helper.setText(buildEmailContent(event.getEmail()),true);


            javaMailSender.send(message);
            notificationLog.setStatus("SENT");
            notificationRepository.save(notificationLog);
            log.info("Письмо отправлено успешно!");


        } catch (MailAuthenticationException | MailSendException | MessagingException e) {
            handleError(notificationLog, "FAILED", e);
            throw new NonRetryableException("Ошибка отправки email: " + e.getMessage());
        }  catch (Exception e) {
            handleError(notificationLog, "RETRYING", e);
            throw new RetryableException("Временная ошибка почтового сервиса", e);
        }

        notificationRepository.save(notificationLog);
    }

    private String buildEmailContent(String email) {
        return """
            <html>
                <body>
                    <h1>Добро пожаловать!</h1>
                    <p>Спасибо за регистрацию, %s!</p>
                    <p>Мы рады приветствовать вас в нашем сервисе.</p>
                    <br>
                    <p>С уважением,<br>Команда сервиса</p>
                </body>
            </html>
            """.formatted(email);
    }
    private void handleError(NotificationLog notificationLog, String status, Exception e) {
        notificationLog.setStatus(status);
        notificationLog.setError(e.getMessage());
        notificationRepository.save(notificationLog);

        log.error("Ошибка отправки email для {}: {}",
                notificationLog.getEmail(), e.getMessage());
    }
}
