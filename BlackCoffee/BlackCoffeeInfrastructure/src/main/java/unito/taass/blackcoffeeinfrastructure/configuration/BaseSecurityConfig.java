package unito.taass.blackcoffeeinfrastructure.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import unito.taass.blackcoffeeinfrastructure.security.JwtAuthenticationFilter;

public abstract class BaseSecurityConfig {

    protected final JwtAuthenticationFilter jwtAuthenticationFilter;

    protected BaseSecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(Customizer.withDefaults())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .httpBasic(basic -> basic.disable())
                .formLogin(form -> form.disable());

        // Chiamata al metodo che ogni microservizio deve implementare
        configureAuthorizations(http);

        return http.build();
    }

    // Metodo astratto per definire i permessi specifici del servizio
    protected abstract void configureAuthorizations(HttpSecurity http) throws Exception;
}