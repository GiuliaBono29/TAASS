package unito.taass.blackcoffeespace.domain.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Data
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;

    @Column(name = "event_id", columnDefinition = "BINARY(16)", nullable = false)
    private UUID eventId;

    @Column(name = "event_name", columnDefinition = "VARCHAR(255)", nullable = false)
    private String eventName;

    @Column(name = "manager_name", columnDefinition = "VARCHAR(255)", nullable = false)
    private String managerName;

    @Column(name = "reservation_date", nullable = false)
    private LocalDate date;

    @Enumerated(EnumType.STRING)
    @Column(name = "fascia_oraria", nullable = false)
    private Slot slot;

    @Column(name = "seats")
    private int seats;

    @Enumerated(EnumType.STRING)
    @Column(name = "state", nullable = false)
    private State state;

    public void modifyState(State state) {
        this.state = state;
    }
}
