package backend.hobbiebackend.service.impl;

import backend.hobbiebackend.model.entities.UserEntity;
import backend.hobbiebackend.service.NotificationService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;


@Service
public class NotificationServiceImpl implements NotificationService {
    private final JavaMailSender javaMailSender;

    public NotificationServiceImpl(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    @Override
    @CircuitBreaker(name = "emailService", fallbackMethod = "emailFallback")
    public void sendNotification(UserEntity userEntity) {
        SimpleMailMessage mail = new SimpleMailMessage();
        String mailBody = "http://localhost:4200/password/" + userEntity.getId();
        mail.setTo(userEntity.getEmail());
        mail.setFrom("findyourhobbie@gmail.com");
        mail.setSubject("Change your password");
        mail.setText("Click the link to reset your password: " + mailBody);

        javaMailSender.send(mail);
    }

    public void emailFallback(UserEntity userEntity, Throwable t) {
        System.out.println("Fallback triggered for user: " + userEntity.getEmail());
        System.out.println("Reason: " + t.getMessage());
    }
}
