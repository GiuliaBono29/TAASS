package unito.taass.blackcoffeeauth.application.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import unito.taass.blackcoffeeauth.domain.dto.AuthResponse;
import unito.taass.blackcoffeeauth.domain.dto.LoginRequest;
import unito.taass.blackcoffeeauth.domain.dto.SignupRequest;
import unito.taass.blackcoffeeauth.domain.model.User;
import unito.taass.blackcoffeeauth.domain.service.AuthService;
import unito.taass.blackcoffeeinfrastructure.security.JwtUtils;


@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final JwtUtils jwtUtils;


    public AuthController(AuthService authService, JwtUtils jwtUtils) {
        this.authService = authService;
        this.jwtUtils = jwtUtils;
    }

    //SIGNUP
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody SignupRequest signupRequest){
        try {
            User user = authService.register(signupRequest);

            String jwtToken = jwtUtils.generateJwtToken(
                    user.getId(),
                    user.getRole().name(),
                    user.getName()
            );

            return ResponseEntity.ok(new AuthResponse(
                    jwtToken,
                    user.getId(),
                    user.getRole().name(),
                    user.getName()
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }catch (Exception e) {
            return ResponseEntity.internalServerError().body("Errore interno");
        }
    }

    //LOGIN
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest){
        try {
            User user = authService.login(loginRequest);

            String jwtToken = jwtUtils.generateJwtToken(
                    user.getId(),
                    user.getRole().name(),
                    user.getName()
            );

            AuthResponse authResponse = new AuthResponse(
                    jwtToken,
                    user.getId(),
                    user.getRole().name(),
                    user.getName()
            );

            return ResponseEntity.ok(authResponse);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(401).body(e.getMessage());
        }catch (Exception e) {
            return ResponseEntity.internalServerError().body("Errore interno");
        }
    }
}
