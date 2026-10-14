package unito.taass.blackcoffeeevent.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import unito.taass.blackcoffeeevent.domain.model.Slot;

import java.time.LocalDate;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReservationResponse {

    private UUID roomId;
    private String roomName;
    private int roomCapacity;
    private TableResponse table;
    private UUID reservationId;
    private LocalDate date;
    private Slot slot;
    private int seats;
    private UUID eventId; //opzionale, valido solo per prenotazioni per evento

}
