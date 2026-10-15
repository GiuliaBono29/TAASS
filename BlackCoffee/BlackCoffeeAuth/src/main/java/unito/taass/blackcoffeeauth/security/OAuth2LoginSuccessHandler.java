package unito.taass.blackcoffeeauth.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import unito.taass.blackcoffeeauth.domain.model.User;
import unito.taass.blackcoffeeauth.domain.service.AuthService;
import org.springframework.http.HttpHeaders;
import unito.taass.blackcoffeeinfrastructure.security.JwtUtils;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Component
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final AuthService authService;
    private final JwtUtils jwtUtils;
    private final OAuth2AuthorizedClientService authorizedClientService;
    private final RestTemplate template;

    public OAuth2LoginSuccessHandler(AuthService authService, JwtUtils jwtUtils, OAuth2AuthorizedClientService authorizedClientService, RestTemplateBuilder template) {
        this.authService = authService;
        this.jwtUtils = jwtUtils;
        this.authorizedClientService = authorizedClientService;
        this.template = template.build();
    }

    @Override
    public void onAuthenticationSuccess (
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication)
            throws IOException {

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        OAuth2AuthenticationToken oAuth2Token = (OAuth2AuthenticationToken) authentication;

        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");

        if (name == null) {
            name = oAuth2User.getAttribute("login");
        }

        if (email == null){
            OAuth2AuthorizedClient client = authorizedClientService.loadAuthorizedClient(oAuth2Token.getAuthorizedClientRegistrationId(), oAuth2Token.getName());
            String accessToken = client.getAccessToken().getTokenValue();
            email = fetchingGitHubEmail(accessToken);
        }

        if (email == null) {
            throw new RuntimeException("impossibile recuperare l'email da GitHub");
        }

        // creo o recupero utente

        User user = authService.findOrCreateOAuthUser(email, name);

        //genera JWT
        String token = jwtUtils.generateJwtToken(
                user.getId(),
                user.getRole().name(),
                user.getName()
        );

        // redirect al front-end
        String redirectUrl =
                "/out.html#token=" + token;

        response.setStatus(HttpServletResponse.SC_FOUND);
        response.setHeader("Location", redirectUrl);
        response.flushBuffer();
    }

    private String fetchingGitHubEmail(String accessToken) {

        String url = "https://api.github.com/user/emails";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<List<Map<String, Object>>> response = template.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    new ParameterizedTypeReference<List<Map<String, Object>>>() {}
            );

            List<Map<String, Object>> emails = response.getBody();
                          
            if (emails != null) {
                return emails.stream()
                        .filter(entry -> Boolean.TRUE.equals(entry.get("primary")))
                        .map(entry -> (String) entry.get("email"))
                        .findFirst()
                        .orElse(null);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;

    }


}
