package backend.hobbiebackend.service.consumer;

import backend.hobbiebackend.config.RabbitMQConfig;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class NotificationConsumer {

    private final JavaMailSender mailSender;
    private final ObjectMapper objectMapper;

    @Autowired
    public NotificationConsumer(JavaMailSender mailSender, ObjectMapper objectMapper) {
        this.mailSender = mailSender;
        this.objectMapper = objectMapper;
    }

    @RabbitListener(queues = RabbitMQConfig.QUEUE_NAME)
    public void handleNotification(String messageJson) {
        try {
            Map<String, Object> message =
                    objectMapper.readValue(messageJson, new TypeReference<Map<String, Object>>() {});

            String type = (String) message.getOrDefault("type", "PASSWORD_RESET");

            if ("BUSINESS_CAMPAIGN".equals(type)) {
                handleBusinessCampaign(message);
                return;
            }

            // Default: PASSWORD_RESET (giữ logic cũ)
            handlePasswordReset(message);

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("❌ Raw messageJson: " + messageJson);
        }
    }

    private void handlePasswordReset(Map<String, Object> message) {
        String email = (String) message.get("email");
        String resetLink = (String) message.get("resetLink");

        if (email == null || email.isBlank()) {
            System.out.println("⚠️ Skip PASSWORD_RESET because email is null/blank. message=" + message);
            return;
        }

        System.out.println("📩 Consumer received PASSWORD_RESET for email: " + email);

        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setTo(email);
        mail.setFrom("findyourhobbie@gmail.com");
        mail.setSubject("Change your password");
        mail.setText("Click this link to reset your password: " + (resetLink == null ? "" : resetLink));

        mailSender.send(mail);
        System.out.println("✅ Email sent successfully to: " + email);
    }

    @SuppressWarnings("unchecked")
    private void handleBusinessCampaign(Map<String, Object> message) {
        Object emailsObj = message.get("emails");
        String subject = (String) message.getOrDefault("subject", "(No subject)");
        String content = (String) message.getOrDefault("content", "");

        if (!(emailsObj instanceof List<?>)) {
            System.out.println("⚠️ BUSINESS_CAMPAIGN missing 'emails' array. message=" + message);
            return;
        }

        List<?> emails = (List<?>) emailsObj;
        System.out.println("📣 Consumer received BUSINESS_CAMPAIGN to " + emails.size() + " emails");

        int sent = 0;
        for (Object e : emails) {
            if (!(e instanceof String)) continue;
            String email = ((String) e).trim();

            if (email.isBlank()) continue;

            try {
                SimpleMailMessage mail = new SimpleMailMessage();
                mail.setTo(email);
                mail.setFrom("findyourhobbie@gmail.com");
                mail.setSubject(subject);
                mail.setText(content);

                mailSender.send(mail);
                sent++;
            } catch (Exception ex) {
                ex.printStackTrace();
                System.out.println("❌ Failed sending campaign to: " + e);
            }
        }

        System.out.println("✅ BUSINESS_CAMPAIGN sent successfully: " + sent + "/" + emails.size());
    }
}
