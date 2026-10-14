package unito.taass.blackcoffeeevent.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import unito.taass.blackcoffeeinfrastructure.configuration.BaseSecurityConfig;
import unito.taass.blackcoffeeinfrastructure.security.JwtAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig extends BaseSecurityConfig {

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        super(jwtAuthenticationFilter);
    }

    @Override
    protected void configureAuthorizations(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                // Rendiamo pubblica la lista di tutti gli eventi
                .requestMatchers("/api/event/all").permitAll()
                // Qualsiasi altra operazione richiede l'autenticazione
                .anyRequest().authenticated()
        );
    }
}