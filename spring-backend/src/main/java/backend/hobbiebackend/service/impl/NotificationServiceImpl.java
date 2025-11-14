package backend.hobbiebackend.service.impl;

import backend.hobbiebackend.config.RabbitMQConfig;
import backend.hobbiebackend.model.entities.UserEntity;
import backend.hobbiebackend.service.NotificationService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

@Service
public class NotificationServiceImpl implements NotificationService {
    private final RabbitTemplate rabbitTemplate;

    @Autowired
    public NotificationServiceImpl(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void sendNotification(UserEntity userEntity) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("email", userEntity.getEmail());
        payload.put("userId", userEntity.getId());

        String resetLink = "http://localhost:4200/password/" + userEntity.getId();
        payload.put("resetLink", resetLink);

    try {
        String json = new ObjectMapper().writeValueAsString(payload);
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE_NAME,
                RabbitMQConfig.ROUTING_KEY,
                json
        );
    } catch (Exception e) {
        e.printStackTrace();
    }
        System.out.println("Message sent to MQ for email: " + userEntity.getEmail());
    }
}
