package backend.hobbiebackend.service.impl;

import backend.hobbiebackend.config.RabbitMQConfig;
import backend.hobbiebackend.model.entities.AppClient;
import backend.hobbiebackend.model.entities.BusinessOwner;
import backend.hobbiebackend.model.entities.Hobby;
import backend.hobbiebackend.model.entities.UserEntity;
import backend.hobbiebackend.model.repostiory.AppClientRepository;
import backend.hobbiebackend.service.NotificationService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class NotificationServiceImpl implements NotificationService {

    private final RabbitTemplate rabbitTemplate;
    private final AppClientRepository appClientRepository;

    @Autowired
    public NotificationServiceImpl(RabbitTemplate rabbitTemplate,
                                   AppClientRepository appClientRepository) {
        this.rabbitTemplate = rabbitTemplate;
        this.appClientRepository = appClientRepository;
    }

    /**
     * Hàm dùng chung để đẩy payload ra RabbitMQ
     */
    private void publish(Map<String, Object> payload) {
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
    }

    /**
     * VD: gửi email reset password – payload type: PASSWORD_RESET
     */
    @Override
    public void sendNotification(UserEntity userEntity) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("type", "PASSWORD_RESET");
        payload.put("email", userEntity.getEmail());
        payload.put("userId", userEntity.getId());

        String resetLink = "http://localhost:4200/password/" + userEntity.getId();
        payload.put("resetLink", resetLink);

        publish(payload);

        System.out.println("Message sent to MQ for email: " + userEntity.getEmail());
    }

    /**
     * Hàm bạn cần: hobby được business update
     * -> lấy tất cả AppClient đã save hobby này
     * -> push message HOBBY_UPDATED ra MQ để service gửi email.
     */
    @Override
    public void notifyBusinessCampaign(BusinessOwner owner, String subject, String content) {
        Set<Hobby> hobbies = owner.getHobby_offers();
        if (hobbies == null || hobbies.isEmpty()) return;

        List<String> emails = hobbies.stream()
                .flatMap(h -> appClientRepository.findAllClientsWhoSavedHobby(h.getId()).stream())
                .map(AppClient::getEmail)
                .filter(e -> e != null && !e.isBlank())
                .map(String::trim)
                .map(String::toLowerCase)
                .distinct()
                .collect(Collectors.toList());

        if (emails.isEmpty()) return;

        Map<String, Object> payload = new HashMap<>();
        payload.put("type", "BUSINESS_CAMPAIGN");
        payload.put("businessUsername", owner.getUsername());
        payload.put("businessName", owner.getBusinessName());
        payload.put("emails", emails);
        payload.put("subject", subject);
        payload.put("content", content);

        publish(payload);
    }

}
