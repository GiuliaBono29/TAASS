package unito.taass.blackcoffeeevent.domain.service;

import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class CateringMockService {

    public UUID getAvaiableMenus(String description) {
        return UUID.fromString("00000000-0000-0000-0000-000000000000");
    }


}
