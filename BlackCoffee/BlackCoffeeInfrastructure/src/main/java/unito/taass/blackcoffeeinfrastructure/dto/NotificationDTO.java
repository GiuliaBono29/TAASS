package unito.taass.blackcoffeeinfrastructure.dto;

import lombok.*;
import unito.taass.blackcoffeeinfrastructure.model.NotificationType;
import unito.taass.blackcoffeeinfrastructure.model.RoleType;

import java.io.Serializable;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class NotificationDTO implements Serializable {
    private UUID recipientId; // L'ID dell'utente che deve ricevere il push
    private RoleType role;
    private String title;
    private String message;
    private NotificationType type; // Es: "EVENT_CREATED", "SPACE_CONFIRMED"
}
