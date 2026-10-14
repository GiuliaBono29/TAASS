package unito.taass.blackcoffeenotification.messaging;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import unito.taass.blackcoffeeinfrastructure.message.BaseRabbitConfig;
import unito.taass.blackcoffeeinfrastructure.message.RabbitContract;


@Configuration
public class NotificationRabbitConfig{


    @Bean
    public Queue notificationQueue() {
        return new Queue(RabbitContract.QUEUE_NOTIFICATION, true);
    }

    @Bean
    public Binding notificationBinding(Queue notificationQueue, TopicExchange blackcoffeeExchange) {
        // Colleghiamo la coda dei log all'exchange comune usando la routing key specifica
        return BindingBuilder.bind(notificationQueue)
                .to(blackcoffeeExchange)
                .with(RabbitContract.ROUTING_NOTIFICATION_PREFIX + "*");
    }
}