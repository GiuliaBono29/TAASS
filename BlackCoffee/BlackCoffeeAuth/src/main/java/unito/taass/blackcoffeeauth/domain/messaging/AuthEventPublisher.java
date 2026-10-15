package unito.taass.blackcoffeeauth.domain.messaging;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import unito.taass.blackcoffeeinfrastructure.dto.LogDTO;
import unito.taass.blackcoffeeinfrastructure.message.RabbitContract;

@Component
public class AuthEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    public AuthEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publishLog(String operation, String details, String level) {
        LogDTO log = new LogDTO(
                "AUTH-SERVICE",
                operation,
                details,
                level,
                System.currentTimeMillis()
        );

        rabbitTemplate.convertAndSend(
                RabbitContract.EXCHANGE,
                RabbitContract.ROUTING_LOG_PREFIX + "auth",
                log
        );
    }
}
