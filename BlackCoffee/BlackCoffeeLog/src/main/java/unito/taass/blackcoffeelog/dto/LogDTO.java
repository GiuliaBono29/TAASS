package unito.taass.blackcoffeelog.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LogDTO implements Serializable {
    private String serviceName; // "EVENT-SERVICE", "AUTH-SERVICE", etc.
    private String operation;   // "ISCRIZIONE", "LOGIN", "CREAZIONE_SPAZIO"
    private String details;     // Qui metti i dettagli (ID evento, ID utente, ecc.)
    private String level;       // "INFO", "WARN", "ERROR"
    private long timestamp;
}
