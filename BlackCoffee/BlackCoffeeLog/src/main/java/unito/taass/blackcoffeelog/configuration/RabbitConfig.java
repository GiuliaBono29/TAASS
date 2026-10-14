package unito.taass.blackcoffeelog.configuration;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//Configurazione collegamento con RabbitMQ
//1.Definizione Coda (system_logs)
//2.Trasformo automaticamente i miei oggetti Java in formato JSON

@Configuration
public class RabbitConfig {

    public static final String QUEUE_NAME = "system_logs";

    @Bean
    public Queue logQueue() {
        return new Queue(QUEUE_NAME, true); // true = durevole
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
