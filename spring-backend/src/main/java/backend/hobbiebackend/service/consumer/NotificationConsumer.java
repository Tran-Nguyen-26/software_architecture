package backend.hobbiebackend.service.consumer;

import backend.hobbiebackend.config.RabbitMQConfig;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;

@Service
public class NotificationConsumer {

    private final JavaMailSender mailSender;

    @Autowired
    public NotificationConsumer(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @RabbitListener(queues = RabbitMQConfig.QUEUE_NAME)
    public void handleNotification(String messageJson) {

    try {
            Map<String, Object> message =
                    new ObjectMapper().readValue(messageJson, HashMap.class);

            String email = (String) message.get("email");
            String resetLink = (String) message.get("resetLink");

            System.out.println("📩 Consumer received message for email: " + email);

            SimpleMailMessage mail = new SimpleMailMessage();
            mail.setTo(email);
            mail.setFrom("findyourhobbie@gmail.com");
            mail.setSubject("Change your password");
            mail.setText("Click this link to reset your password: " + resetLink);

            mailSender.send(mail);
            System.out.println("✅ Email sent successfully to: " + email);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
