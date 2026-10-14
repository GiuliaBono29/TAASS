package unito.taass.blackcoffeeinfrastructure.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.UUID;

@Component
public class JwtUtils {

    private final String jwtSecret = "ChiaveSegretaBlackCoffeeTAASS2025";
    private final Key key = Keys.hmacShaKeyFor(jwtSecret.getBytes());

    private final long jwtExpirationMs = 3600_000; //un'ora

    public String generateJwtToken(UUID userId, String role, String name){
        return Jwts.builder()
                .setSubject(userId.toString())
                .claim("role", role)
                .claim("name", name)
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean validateJwtToken (String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }

    public Claims getClaimsFromJwtToken (String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
