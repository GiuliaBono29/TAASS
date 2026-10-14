package unito.taass.blackcoffeeevent.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import unito.taass.blackcoffeeevent.domain.model.Slot;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReservationRequest {

    public LocalDate date;
    public Slot slot;
    public int seats;
    public UUID eventId;
    public String eventName;
    public String managerName;

}
