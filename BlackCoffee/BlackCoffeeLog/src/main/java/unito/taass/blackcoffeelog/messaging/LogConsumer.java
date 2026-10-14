package unito.taass.blackcoffeelog.messaging;


import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import unito.taass.blackcoffeeinfrastructure.message.RabbitContract;
import unito.taass.blackcoffeelog.dto.LogDTO;
import unito.taass.blackcoffeelog.service.LogService;
import unito.taass.blackcoffeelog.configuration.RabbitConfig;

//Questa classe si "attiva" quando arriva un messaggio nella coda di RabbtiMQ
//Intercetta il messaggio JSON, lo trasforma in un oggetto LogSTO e prepara la stringa per il file

@Service
public class LogConsumer {

    @Autowired
    private LogService logService;

    /**
     * Questo metodo viene attivato automaticamente ogni volta che un messaggio
     * viene pubblicato sulla coda "system_logs".
     */
    @RabbitListener(queues = RabbitContract.QUEUE_LOG)
    public void consumeLog(LogDTO logMessage) {

        // Creiamo la riga di testo formattata
        // Esempio: [2025-12-22 10:30:00] [EVENT-SERVICE] - ISCRIZIONE: Utente X si è iscritto a Evento Y
        String formattedLine = String.format("[%tF %tT] [%s] - %s: %s",
                logMessage.getTimestamp(), logMessage.getTimestamp(),
                logMessage.getServiceName(),
                logMessage.getOperation(),
                logMessage.getDetails());

        // Stampiamo in console per debug (opzionale)
        System.out.println("Ricevuto log da RabbitMQ: " + formattedLine);

        // Scriviamo fisicamente sul file tramite il Service
        logService.writeToLogFile(formattedLine);
    }
}
