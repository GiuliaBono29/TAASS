package unito.taass.blackcoffeespace.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.*;

@Entity
@Table(name = "reservations")
@Data
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;

    @Column(name = "owner_id", columnDefinition = "BINARY(16)", nullable = false)
    private UUID owner;

    @Column(name = "reservation_date", nullable = false)
    private LocalDate date;

    @Enumerated(EnumType.STRING)
    @Column(name = "fascia_oraria", nullable = false)
    private Slot slot;

    @Column(name = "seats")
    private int seats;

    @Column(name = "event_id", columnDefinition = "BINARY(16)", nullable = true)
    private UUID event;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "table_id", columnDefinition = "BINARY(16)")
    private Tables table;

    public void modifica(LocalDate date, Slot slot, int seats, Tables table) {
        this.date=date;
        this.slot=slot;
        this.seats=seats;
        this.table=table;
    }
}
