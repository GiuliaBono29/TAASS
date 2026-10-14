package unito.taass.blackcoffeespace.domain.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

// Questo dice a Spring di rispondere con 404 se l'eccezione viene lanciata
@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class NoTableAvaibleException extends RuntimeException {
    public NoTableAvaibleException(String message) {
        super(message);
    }
}
