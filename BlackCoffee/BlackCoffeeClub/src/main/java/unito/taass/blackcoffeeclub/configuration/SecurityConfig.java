package unito.taass.blackcoffeeclub.configuration;

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

                // Permettiamo la visualizzazione pubblica dei club
                .requestMatchers(HttpMethod.GET, "/api/club/**").permitAll()

                // Tutto il resto richiede autenticazione
                .anyRequest().authenticated()
        );
    }
}