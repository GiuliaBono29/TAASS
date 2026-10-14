package unito.taass.blackcoffeeclub.domain.messagging;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import unito.taass.blackcoffeeinfrastructure.dto.LogDTO;
import unito.taass.blackcoffeeinfrastructure.dto.NotificationDTO;
import unito.taass.blackcoffeeinfrastructure.message.RabbitContract;
import unito.taass.blackcoffeeinfrastructure.model.NotificationType;

import java.util.UUID;

@Component
public class ClubEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    public ClubEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publishLog(String operation, String details, String level) {
        LogDTO log = new LogDTO(
                "CLUB-SERVICE",
                operation,
                details,
                level,
                System.currentTimeMillis()
        );

        rabbitTemplate.convertAndSend(
                RabbitContract.EXCHANGE,
                RabbitContract.ROUTING_LOG_PREFIX + "club",
                log
        );
    }

    public void publishNotification(UUID recipientId, String title, String message){
        NotificationDTO notificationDTO = new NotificationDTO();
        notificationDTO.setRecipientId(recipientId);
        notificationDTO.setTitle(title);
        notificationDTO.setMessage(message);
        notificationDTO.setType(NotificationType.EVENT_CREATED);

        rabbitTemplate.convertAndSend(
                RabbitContract.EXCHANGE,
                RabbitContract.ROUTING_NOTIFICATION_PREFIX + "user",
                notificationDTO
        );
    }


}