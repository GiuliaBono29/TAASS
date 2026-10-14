package unito.taass.blackcoffeenotification.configuration;

import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import unito.taass.blackcoffeeinfrastructure.security.JwtUtils;

import java.util.ArrayList;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Autowired
    private JwtUtils jwtUtils;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {

        //messaggi che iniziano per /topic -> broadcast
        //messaggi che iniziano per /queue -> utente specifico
        config.enableSimpleBroker("/topic", "/queue");

        //prefisso messaggi dal cliente al server
        config.setApplicationDestinationPrefixes("/app");

        //prefisso code private
        config.setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        //endpoint per connessione
        registry.addEndpoint("/ws-notifications")
                .setAllowedOriginPatterns("*")  //connessione da ogni origine
                .withSockJS();
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration){
        registration.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

                if(StompCommand.CONNECT.equals(accessor.getCommand())){
                    String outHeader = accessor.getFirstNativeHeader("Authorization");

                    if (outHeader != null && outHeader.startsWith("Bearer ")){
                        String tocken = outHeader.substring(7);
                        try {
                            if(jwtUtils.validateJwtToken(tocken)){
                                Claims claims = jwtUtils.getClaimsFromJwtToken(tocken);
                                String userId = claims.getSubject();

                                if (userId != null){
                                    UsernamePasswordAuthenticationToken auth =
                                            new UsernamePasswordAuthenticationToken(userId, null, new ArrayList<>());

                                    accessor.setUser(auth);

                                    System.out.println("DEBUG WEBSOCKET: UTENTE IDENTIFICATO " + userId);
                                }
                            }
                        } catch (Exception e) {
                            System.err.println("ERRORE VALIDAZIONE TOCKEN WEBSOCKET: " + e.getMessage());
                        }
                    }
                }

                return message;
            }
        });
    }

}
