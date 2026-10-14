package unito.taass.blackcoffeeevent.domain.messaging;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import unito.taass.blackcoffeeevent.domain.dto.ReservationRequest;
import unito.taass.blackcoffeeinfrastructure.dto.LogDTO;
import unito.taass.blackcoffeeinfrastructure.dto.NotificationDTO;
import unito.taass.blackcoffeeinfrastructure.message.RabbitContract;
import unito.taass.blackcoffeeinfrastructure.model.NotificationType;

import java.time.LocalDate;
import java.util.UUID;
import java.util.HashMap;
import java.util.Map;

//Ha il compito di ricevere le informazioni da space riguardanti la prenotazione di stanze
//(unica parte di space implementata con rabbitmq) e inviarle tramite messaggio
@Component
public class EventPublisher {
    private final RabbitTemplate rabbitTemplate;

    public EventPublisher(RabbitTemplate rabbitTemplate){
        this.rabbitTemplate=rabbitTemplate;
    }

    public void publishEventReservation(ReservationRequest payload){
        rabbitTemplate.convertAndSend(
                RabbitContract.EXCHANGE,
                RabbitContract.ROUTING_SPACE_PREFIX + "created", // "space.created"
                payload
        );
    }

    public void publishEventDeletion(UUID eventId) {
        rabbitTemplate.convertAndSend(
                RabbitContract.EXCHANGE,
                RabbitContract.ROUTING_SPACE_PREFIX + "delete",
                eventId
        );
    }

    public void publishToClub(UUID clubId, String eventName, LocalDate date, UUID eventId) {
        Map<String, Object> message = new HashMap<>();
        message.put("clubId", clubId);
        message.put("eventName", eventName);
        message.put("date", date.toString());
        message.put("eventId", eventId);

        rabbitTemplate.convertAndSend(
                RabbitContract.EXCHANGE,
                RabbitContract.ROUTING_CLUB_PREFIX + "pubblish",
                message
        );
    }

    public void publishLog(String operation, String deteils, String level){
        LogDTO logDTO = new LogDTO(
                "Event Service",
                operation,
                deteils,
                level,
                System.currentTimeMillis()
        );

        rabbitTemplate.convertAndSend(
                RabbitContract.EXCHANGE,
                RabbitContract.ROUTING_LOG_PREFIX + "event",
                logDTO
        );
    }

    public void publishNotification(UUID id, String title, String message, NotificationType type) {

        NotificationDTO notificationDTO = new NotificationDTO();

        notificationDTO.setRecipientId(id);
        notificationDTO.setTitle(title);
        notificationDTO.setMessage(message);
        notificationDTO.setType(type);

        rabbitTemplate.convertAndSend(
                RabbitContract.EXCHANGE,
                RabbitContract.ROUTING_NOTIFICATION_PREFIX + "event",
                notificationDTO
        );
    }

}
