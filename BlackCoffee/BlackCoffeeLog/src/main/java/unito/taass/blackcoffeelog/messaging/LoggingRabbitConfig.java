package unito.taass.blackcoffeelog.messaging;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import unito.taass.blackcoffeeinfrastructure.message.BaseRabbitConfig;
import unito.taass.blackcoffeeinfrastructure.message.RabbitContract;


@Configuration
public class LoggingRabbitConfig extends BaseRabbitConfig{


    @Bean
    public Queue loggingQueue() {
        return new Queue(RabbitContract.QUEUE_LOG, true);
    }

    @Bean
    public Binding loggingBinding(Queue loggingQueue, TopicExchange blackcoffeeExchange) {
        // Colleghiamo la coda dei log all'exchange comune usando la routing key specifica
        return BindingBuilder.bind(loggingQueue)
                .to(blackcoffeeExchange)
                .with(RabbitContract.ROUTING_LOG_PREFIX + "*");
    }
}