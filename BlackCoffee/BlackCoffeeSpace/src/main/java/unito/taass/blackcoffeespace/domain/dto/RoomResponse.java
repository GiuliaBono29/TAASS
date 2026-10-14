package unito.taass.blackcoffeespace.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoomResponse {

    private UUID roomId;
    private String roomName;
    private int roomCapacity;
    private List<TableResponse> availableTables;

}
