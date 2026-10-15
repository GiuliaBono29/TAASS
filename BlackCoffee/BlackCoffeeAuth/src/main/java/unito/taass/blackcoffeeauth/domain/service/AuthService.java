package unito.taass.blackcoffeeauth.domain.service;
import unito.taass.blackcoffeeauth.domain.dto.*;
import unito.taass.blackcoffeeauth.domain.messaging.AuthEventPublisher;
import unito.taass.blackcoffeeauth.domain.model.User;
import unito.taass.blackcoffeeauth.domain.model.Role;
import unito.taass.blackcoffeeauth.domain.repository.UserRepository;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthEventPublisher authEventPublisher;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, AuthEventPublisher authEventPublisher) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authEventPublisher=authEventPublisher;
    }

    //REGISTRAZIONE

    public User register (SignupRequest signupRequest) {

        //Controllo email già esistente
        if (userRepository.existsByEmail(signupRequest.getEmail())) {
            throw new IllegalArgumentException("Email già registrata");
        }

        //Controllo password=conferma_password
        if (!signupRequest.getPassword().equals(signupRequest.getConfPassword())){
            throw new IllegalArgumentException("Password e conferma non corrispondono");
        }

        //Creazione utente
        User user = User.builder()
                .name(signupRequest.getName())
                .surname(signupRequest.getSurname())
                .tel(signupRequest.getTell())
                .email(signupRequest.getEmail())
                .role(Role.CLIENTE)     //con signup si iscrivono i clienti, altri metodi per i lavoratori
                .password(passwordEncoder.encode(signupRequest.getPassword()))      //password sicura
                .build();

        User savedUser = userRepository.save(user);

        authEventPublisher.publishLog(
                "REGISTRAZIONE_UTENTE",
                "Utente con id " + savedUser.getId() + " registrato",
                "INFO"
        );

        System.out.println("DEBUG: RUOLO: " + savedUser.getRole());

        return savedUser;
    }

    //LOGIN

    public User login(LoginRequest loginRequest){
        //Trovo l'utente tramite email
        Optional<User> userOptional = userRepository.findByEmail(loginRequest.getEmail());

        if (userOptional.isEmpty()){
            throw new IllegalArgumentException("Credenziali non valide : email");
        }

        User user = userOptional.get();

        //Verifica password
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())){
            throw new IllegalArgumentException("Credenziali non valide : password");
        }

        authEventPublisher.publishLog(
                "LOGIN",
                "Utente con id " + user.getId() +
                        " ha effettuato il login",
                      "INFO"
        );

        return user;
    }

    public User findOrCreateOAuthUser(String email, String name) {

        String randomPassword = UUID.randomUUID().toString();

        return userRepository.findByEmail(email)
                .orElseGet(() -> {
                    User newUser = User.builder()
                            .email(email)
                            .name(name != null ? name : email) // fallback
                            .surname("") // placeholder se obbligatorio
                            .role(Role.CLIENTE)
                            .password(passwordEncoder.encode(randomPassword))
                            .build();

                    authEventPublisher.publishLog(
                            "LOGIN_EXTERNAL",
                            "Utente con id " + newUser.getId() +
                                    " registrato",
                            "INFO"
                    );

                    return userRepository.save(newUser); // SALVATAGGIO qui!
                });
    }
}
