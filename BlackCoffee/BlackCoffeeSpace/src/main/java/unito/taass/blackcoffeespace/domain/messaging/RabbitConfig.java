package unito.taass.blackcoffeespace.domain.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.DefaultClassMapper;
import org.springframework.amqp.support.converter.Jackson2JavaTypeMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import unito.taass.blackcoffeeinfrastructure.message.BaseRabbitConfig;
import org.springframework.amqp.core.Queue;
import unito.taass.blackcoffeeinfrastructure.message.RabbitContract;

import java.util.HashMap;
import java.util.Map;

//infrastruttura per creazione e collegamento code
@Configuration
public class RabbitConfig {
    @Bean
    public Queue spaceQueue(){
        // Usiamo la costante appena aggiunta
        return new Queue(RabbitContract.QUEUE_SPACE, true);
    }

    @Bean
    public Binding spaceBind(Queue spaceQueue, TopicExchange blackcoffeeExchange) {
        // Space si mette in ascolto sul binario "space.*"
        return BindingBuilder
                .bind(spaceQueue)
                .to(blackcoffeeExchange)
                .with(RabbitContract.bindAllFor(RabbitContract.ROUTING_SPACE_PREFIX));
    }

    @Bean
    @Primary
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        // Importante per gestire correttamente UUID e date
        objectMapper.findAndRegisterModules();

        Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter(objectMapper);

        // CAMBIA DA TYPE_ID A INFERRED
        converter.setTypePrecedence(Jackson2JavaTypeMapper.TypePrecedence.INFERRED);

        converter.setClassMapper(classMapper());
        return converter;
    }

    @Bean
    public DefaultClassMapper classMapper() {
        DefaultClassMapper typeMapper = new DefaultClassMapper();
        typeMapper.setTrustedPackages("*");

        // Invece di mappare l'intero pacchetto, diciamo che quel "TypeId"
        // corrisponde alla nostra classe locale.
        Map<String, Class<?>> idClassMapping = new HashMap<>();
        idClassMapping.put("unito.taass.blackcoffeeevent.domain.dto.ReservationRequest",
                unito.taass.blackcoffeespace.domain.dto.ReservationRequest.class);

        typeMapper.setIdClassMapping(idClassMapping);
        return typeMapper;
    }
}
