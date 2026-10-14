package unito.taass.blackcoffeeevent.domain.messaging;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import unito.taass.blackcoffeeevent.domain.dto.ReservationResponse;
import unito.taass.blackcoffeeevent.domain.service.EventService;
import unito.taass.blackcoffeeinfrastructure.message.RabbitContract;

@Component
public class RabbitSubscriber {
    private final EventService eventService;

    public RabbitSubscriber(EventService eventService){
        this.eventService = eventService;
    }

    @RabbitListener(queues = RabbitContract.QUEUE_EVENTO)
    public void onMessage(ReservationResponse payload,
                          @Header(AmqpHeaders.RECEIVED_ROUTING_KEY) String key) {

        // Ora ascoltiamo i messaggi che Space ci invia con prefisso "evento."
        if (key.startsWith(RabbitContract.ROUTING_EVENTO_PREFIX)) {
            String action = key.replace(RabbitContract.ROUTING_EVENTO_PREFIX, "");
            eventService.handleSpaceResponse(action, payload);
        }
    }
}
