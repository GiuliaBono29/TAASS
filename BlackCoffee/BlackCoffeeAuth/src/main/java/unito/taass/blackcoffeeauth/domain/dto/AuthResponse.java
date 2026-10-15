package unito.taass.blackcoffeeauth.domain.dto;

import java.util.UUID;

public class AuthResponse {

    private String accessToken;
    private String tokenType = "Bearer";

    private UUID userId;
    private String role;
    private String name;

    public AuthResponse(String accessToken, UUID userId, String role, String name) {
        this.accessToken = accessToken;
        this.userId = userId;
        this.role = role;
        this.name = name;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public UUID getUserId() {
        return userId;
    }

    public String getRole() {
        return role;
    }

    public String getName(){ return name;}
}
