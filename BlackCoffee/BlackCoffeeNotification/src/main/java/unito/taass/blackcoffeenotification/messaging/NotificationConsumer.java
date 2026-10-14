package unito.taass.blackcoffeenotification.messaging;


import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import unito.taass.blackcoffeeinfrastructure.dto.NotificationDTO;
import unito.taass.blackcoffeeinfrastructure.message.RabbitContract;
import unito.taass.blackcoffeenotification.service.NotificationService;

//Questa classe si "attiva" quando arriva un messaggio nella coda di RabbtiMQ
//Intercetta il messaggio JSON, lo trasforma in un oggetto LogSTO e prepara la stringa per il file

@Service
public class NotificationConsumer {

    @Autowired
    private NotificationService notificationService;

    /**
     * Questo metodo viene attivato automaticamente ogni volta che un messaggio
     * viene pubblicato sulla coda "system_logs".
     */
    @RabbitListener(queues = RabbitContract.QUEUE_NOTIFICATION)
    public void consume(NotificationDTO notification) {

        System.out.println("Ricevuta Notifica: " + notification.getTitle());

        if (notification.getRecipientId() != null){
            System.out.println("Notifica ricevuta per l'utente: " + notification.getRecipientId());

            notificationService.sendNotificationToUser(notification);
        } else if (notification.getRole()!= null) {
            System.out.println("Notifica ricevuta per il ruolo: " + notification.getRole());

            notificationService.sendNotificationToRole(notification.getRole(), notification);
        } else {
            System.out.println("Invio messaggio Broadcast");

            notificationService.broadcastNotification(notification);
        }
    }
}
