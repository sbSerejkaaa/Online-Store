package com.example.notificationService.service;

import com.example.core.UserRegisteredEvent;
import com.example.notificationService.model.NotificationLog;
import com.example.notificationService.repository.NotificationRepository;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
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
        log.info("=== НАЧАЛО ОТПРАВКИ EMAIL ===");
        log.info("Получатель: {}", event.getEmail());
        log.info("Username в конфиге: testprojectonlinestore@gmail.com");

        NotificationLog notificationLog = NotificationLog.builder()
                .userId(event.getUserId())
                .email(event.getEmail())
                .type("WELCOME_EMAIL")
                .status("SENDING")
                .build();

        notificationRepository.save(notificationLog);

        try {
            log.info("Пытаемся создать MimeMessage...");
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(event.getEmail());
            helper.setSubject("Добро пожаловать в наш сервис!");
            helper.setText(
                    "<h1>Добро пожаловать!</h1>" +
                            "<p>Спасибо за регистрацию, " + event.getEmail() + "!</p>" +
                            "<p>Мы рады приветствовать вас в нашем сервисе.</p>",
                    true
            );

            log.info("Пытаемся отправить письмо через JavaMailSender...");
            javaMailSender.send(message);
            // 👆 ПИСЬМО ОТПРАВЛЕНО НА РЕАЛЬНУЮ ПОЧТУ 👆
            log.info("✅ Письмо отправлено успешно!");

            notificationLog.setStatus("SENT");

        } catch (Exception e) {
            log.error("❌ ПОЛНАЯ ОШИБКА:", e);  // 👈 ВАЖНО: с полным stacktrace!
            log.error("Тип ошибки: {}", e.getClass().getName());
            log.error("Сообщение: {}", e.getMessage());
            notificationLog.setStatus("FAILED");
            notificationLog.setError(e.getMessage());
        }

        notificationRepository.save(notificationLog);
        log.info("=== КОНЕЦ ОТПРАВКИ EMAIL ===");
    }


}
