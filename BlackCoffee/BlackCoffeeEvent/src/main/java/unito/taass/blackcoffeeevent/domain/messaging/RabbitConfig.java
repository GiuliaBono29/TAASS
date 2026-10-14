package unito.taass.blackcoffeeevent.domain.messaging;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import unito.taass.blackcoffeeinfrastructure.message.BaseRabbitConfig;
import unito.taass.blackcoffeeinfrastructure.message.RabbitContract;

//infrastruttura per creazione e collegamento code
@Configuration
public class RabbitConfig {
    @Bean
    public Queue eventQueue(){
        return new Queue(RabbitContract.QUEUE_EVENTO, true);
    }

    @Bean
    public Binding eventFromSpaceBind(Queue eventQueue, TopicExchange blackcoffeeExchange){
        return BindingBuilder
                .bind(eventQueue)
                .to(blackcoffeeExchange)
                .with(RabbitContract.bindAllFor(RabbitContract.ROUTING_EVENTO_PREFIX)); // "space.*"
    }

}
