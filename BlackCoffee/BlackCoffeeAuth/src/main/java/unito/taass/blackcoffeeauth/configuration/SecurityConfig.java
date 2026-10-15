package unito.taass.blackcoffeeauth.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import unito.taass.blackcoffeeauth.security.OAuth2LoginSuccessHandler;
import unito.taass.blackcoffeeinfrastructure.configuration.BaseSecurityConfig;
import unito.taass.blackcoffeeinfrastructure.security.JwtAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig extends BaseSecurityConfig {

    private final OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter,
                          OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler) {
        // Passiamo il filtro all'infrastruttura (BaseSecurityConfig)
        super(jwtAuthenticationFilter);
        this.oAuth2LoginSuccessHandler = oAuth2LoginSuccessHandler;
    }

    @Override
    protected void configureAuthorizations(HttpSecurity http) throws Exception {
        // 1. Sovrascriviamo la session policy dell'infrastruttura
        // OAuth2 ha bisogno di sessioni per gestire l'handshake con GitHub
        http.sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED));

        // 2. Definiamo i permessi specifici per l'autenticazione
        http.authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/oauth2/**").permitAll()
                .requestMatchers("/login/**").permitAll()
                // Rimosso /api/event/all: ogni servizio deve gestire i suoi permessi
                .anyRequest().authenticated()
        );

        // 3. Configurazione specifica per il login OAuth2
        http.oauth2Login(oauth -> oauth
                .successHandler(oAuth2LoginSuccessHandler)
                .loginPage("/login.html")
                .authorizationEndpoint(authorization -> authorization
                        .baseUri("/api/auth/oauth2/authorization"))
                .redirectionEndpoint(redirection -> redirection
                        .baseUri("/api/auth/login/oauth2/code/*"))
        );

        // 4. Header di sicurezza (opzionali ma consigliati)
        http.headers(headers -> headers
                .contentSecurityPolicy(csp -> csp
                        .policyDirectives("default-src 'self'; script-src 'self' 'unsafe-inline' https://cdn.jsdelivr.net; style-src 'self' 'unsafe-inline' https://cdn.jsdelivr.net")));
    }
}