package unito.taass.blackcoffeespace.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TableResponse {
    private UUID id;
    private int number;
    private int capacity;
}
