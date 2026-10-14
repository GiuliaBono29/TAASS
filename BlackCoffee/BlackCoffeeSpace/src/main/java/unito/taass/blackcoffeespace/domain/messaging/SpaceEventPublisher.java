package unito.taass.blackcoffeespace.domain.messaging;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import unito.taass.blackcoffeeinfrastructure.dto.LogDTO;
import unito.taass.blackcoffeeinfrastructure.dto.NotificationDTO;
import unito.taass.blackcoffeeinfrastructure.message.RabbitContract;
import unito.taass.blackcoffeeinfrastructure.model.NotificationType;
import unito.taass.blackcoffeeinfrastructure.model.RoleType;
import unito.taass.blackcoffeespace.domain.dto.ReservationResponse;

import java.util.UUID;

//Ha il compito di ricevere le informazioni da space riguardanti la prenotazione di stanze
//(unica parte di space implementata con rabbitmq) e inviarle tramite messaggio
@Component
public class SpaceEventPublisher {
    private final RabbitTemplate rabbitTemplate;

    public SpaceEventPublisher(RabbitTemplate rabbitTemplate){
        this.rabbitTemplate=rabbitTemplate;
    }

    //Cos'è SpaceEvent
    public void publishSpaceEvent(String reservationState, ReservationResponse payload){
        // USARE ROUTING_EVENTO_PREFIX ("evento.") invece di SPACE_PREFIX
        rabbitTemplate.convertAndSend(
                RabbitContract.EXCHANGE,
                RabbitContract.ROUTING_EVENTO_PREFIX + reservationState, // Esempio: "evento.confirmed"
                payload
        );
        System.out.println("DEBUG SPACE: Risposta inviata a Event con chiave: " +
                RabbitContract.ROUTING_EVENTO_PREFIX + reservationState);
    }

    public void publishLog(String operation, String details, String level) {
        LogDTO log = new LogDTO(
                "SPACE-SERVICE",
                operation,
                details,
                level,
                System.currentTimeMillis()
        );

        rabbitTemplate.convertAndSend(
                RabbitContract.EXCHANGE,
                RabbitContract.ROUTING_LOG_PREFIX + "space",
                log
        );
    }

    public void publishNotification(RoleType roleType, String title, String message, NotificationType type) {

        NotificationDTO notificationDTO = new NotificationDTO();

        notificationDTO.setRole(roleType);
        notificationDTO.setTitle(title);
        notificationDTO.setMessage(message);
        notificationDTO.setType(type);

        rabbitTemplate.convertAndSend(
                RabbitContract.EXCHANGE,
                RabbitContract.ROUTING_NOTIFICATION_PREFIX + "space",
                notificationDTO
        );
    }

    public void rejectReservation(String reservationState, UUID eventId){
        ReservationResponse payload = new ReservationResponse();
        payload.setEventId(eventId);
        rabbitTemplate.convertAndSend(
                RabbitContract.EXCHANGE,
                RabbitContract.ROUTING_EVENTO_PREFIX + reservationState,
                payload
        );
        System.out.println("DEBUG SPACE: Risposta inviata a Event con chiave: " +
                                RabbitContract.ROUTING_EVENTO_PREFIX + reservationState);
    }
}
