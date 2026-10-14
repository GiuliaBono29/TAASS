package unito.taass.blackcoffeeinfrastructure.message;

import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BaseRabbitConfig {
    //bean da implementare in base alla logica
    @Bean
    public TopicExchange blackcoffeeExchange(){
        //configuro l'exchange: nome exchange, se è durabile allo spegnimento e se si deve autocancellare se nessuno sta ascoltando
        //con questa configurazione ho massima persistenza
        return new TopicExchange(RabbitContract.EXCHANGE, true, false);
    }

    //vari bean di servizio
    @Bean
    public MessageConverter messageConverter(){
        return new Jackson2JsonMessageConverter();
    }

    //connectionfactory configurazione x inviare i messaggi
    //NB perché in rosso connectionFactoy? Perché l'infrastruttura non è eseguibile e quindi intellij non
    //riconosce le dipendenze giuste. Però si compila senza problemi con maven install
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, MessageConverter messageConverter){
        RabbitTemplate rabbitTemplate=new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter);
        return rabbitTemplate;
    }

    //connectionfactory configurazione x il listener
    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory, MessageConverter messageConverter){
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(messageConverter);

        //configurazione se c'è un errore ->
        // se c'è un messaggio che porta a un errore del servizio, Rabbit non rimette in coda il messaggio
        factory.setDefaultRequeueRejected(false);

        //i riceventi dei messaggi di Rabbit devono inviare un messaggio di ricevimento positivo o negativo
        //con AcknowledgeMode.AUTO è Spring che scrive gli acknowledge (altrimenti o si disabilita o si scrive a mano da noi)
        factory.setAcknowledgeMode(AcknowledgeMode.AUTO);

        //numero di messaggi che possono essere aperti dentro al consumer e non acknowledge senza che Rabbit pensi che il servizio
        //non sta rispondendo -> se non risponde Rabbit pensa che ci sia un problema
        factory.setPrefetchCount(1);
        return factory;
    }
}
