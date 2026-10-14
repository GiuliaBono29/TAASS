package unito.taass.blackcoffeeclub.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClubResponse {
    private String nameClub;
    private UUID managerName;
    private String description;
    private int numSub;
}
