package unito.taass.blackcoffeespace.domain.messaging;

import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import unito.taass.blackcoffeeinfrastructure.message.RabbitContract;
import unito.taass.blackcoffeespace.domain.dto.ReservationRequest;
import unito.taass.blackcoffeespace.domain.service.SpaceService;

import java.util.UUID;

@Component
@RabbitListener(queues = RabbitContract.QUEUE_SPACE)
public class RabbitSubscriber {
    private final SpaceService spaceService;

    public RabbitSubscriber(SpaceService spaceService){
        this.spaceService = spaceService;
    }

    @RabbitHandler
    public void onReservationRequest(ReservationRequest request,
                                     @Header(AmqpHeaders.RECEIVED_ROUTING_KEY) String key) {

        System.out.println("DEBUG SPACE: Ricevuto messaggio con chiave: " + key);

        // Se la chiave è "space.created", processiamo la richiesta di prenotazione
        if (key.endsWith("created")) {
            spaceService.handleIncomingRequest(request);
        }
    }

    @RabbitHandler
    public void onDeleteEvent(UUID eventID,
                              @Header(AmqpHeaders.RECEIVED_ROUTING_KEY) String key){

        System.out.println("DEBUG SPACE: Ricevuto messaggio con chiave: " + key);

        if (key.endsWith("delete")){
            spaceService.rejectMessageByEventId(eventID);
        }
    }
}
