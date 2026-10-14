package unito.taass.blackcoffeeinfrastructure.message;

public class RabbitContract {
    public static final String EXCHANGE = "blackcoffee.events";
    //prefissi perché i cambiamenti saranno
    //prefisso.creato / prefisso.in_modifica / prefisso.confermato / prefisso.cancellato ecc
    //routing key con binari di uscita
    public static final String ROUTING_EVENTO_PREFIX = "evento.";
    public static final String ROUTING_SPACE_PREFIX = "space.";
    public static final String ROUTING_CATERING_PREFIX = "catering.";
    public static final String ROUTING_LOG_PREFIX = "log.";
    public static final String ROUTING_CLUB_PREFIX = "club.";
    public static final String ROUTING_NOTIFICATION_PREFIX = "notification.";

    // Nomi delle code
    public static final String QUEUE_SPACE = "queue_space";
    public static final String QUEUE_EVENTO = "queue_evento";
    public static final String QUEUE_LOG = "queue_log";
    public static final String QUEUE_CLUB = "queue_club";
    public static final String QUEUE_NOTIFICATION = "queue_notification";

    public static String bindAllFor(String prefix){
        return prefix + "*";
    }
}
