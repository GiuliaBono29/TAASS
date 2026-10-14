package unito.taass.blackcoffeeclub.domain.messagging;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import unito.taass.blackcoffeeclub.domain.service.ClubService;
import unito.taass.blackcoffeeinfrastructure.message.RabbitContract;

import java.util.Map;
import java.util.UUID;

@Component
public class ClubConsumer {
    @Autowired
    private ClubService clubService;

    @RabbitListener(queues = RabbitContract.QUEUE_CLUB)
    public void eventPublished(Map<String, Object> data){
        try {
            System.out.println("Club-Service: Ricevuto messaggio pubblicazione evento");

            UUID clubId = UUID.fromString(data.get("clubId").toString());
            String eventName = (String) data.get("eventName");
            String date = data.get("date").toString();

            clubService.notifySubscribers(clubId, eventName, date);

            System.out.println("Club-Service: Notifica per l'evento " + eventName + " inviata");
        }catch (Exception e){
            System.err.println("Errore durante l'elaborazione della notifica club: " + e.getMessage());
        }
    }
}
