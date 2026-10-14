package unito.taass.blackcoffeeclub.domain.messagging;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import unito.taass.blackcoffeeinfrastructure.message.RabbitContract;

@Configuration
public class RabbitConfig {

    @Bean
    public Jackson2JsonMessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(RabbitContract.EXCHANGE);
    }

    @Bean
    public Queue clubNotifyQueue() {
        return new Queue(RabbitContract.QUEUE_CLUB, true);
    }

    @Bean
    public Binding clubNotifyBinding(Queue clubNotifyQueue, TopicExchange exchange) {
        return BindingBuilder.bind(clubNotifyQueue)
                .to(exchange)
                .with(RabbitContract.ROUTING_CLUB_PREFIX + "pubblish");
    }
}