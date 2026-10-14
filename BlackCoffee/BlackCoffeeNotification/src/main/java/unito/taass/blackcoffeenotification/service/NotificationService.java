package unito.taass.blackcoffeenotification.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import unito.taass.blackcoffeeinfrastructure.dto.NotificationDTO;
import unito.taass.blackcoffeeinfrastructure.model.RoleType;


@Service
public class NotificationService {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    public void sendNotificationToUser(NotificationDTO notificationDTO) {

        System.out.println("tentativo invio a user: " + notificationDTO.getRecipientId());

        messagingTemplate.convertAndSendToUser(
                String.valueOf(notificationDTO.getRecipientId()),
                "/queue/notifications",
                notificationDTO
        );

        System.out.println("Push inviato a: " + notificationDTO.getRecipientId());
    }

    public void sendNotificationToRole(RoleType role, NotificationDTO notificationDTO){
        messagingTemplate.convertAndSend("/topic/role/" + role, notificationDTO);
        System.out.println("push inviato al ruolo " + role);
    }

    public void broadcastNotification(NotificationDTO notificationDTO) {
        messagingTemplate.convertAndSend("/topic/all", notificationDTO);
    }
}
