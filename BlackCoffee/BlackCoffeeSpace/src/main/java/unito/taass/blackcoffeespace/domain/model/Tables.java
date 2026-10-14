package unito.taass.blackcoffeespace.domain.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "tables")
@Getter
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Tables {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;

    @Column(name = "table_number")
    private int number;

    @Column(name = "table_capacity")
    private int capacity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", columnDefinition = "BINARY(16)")
    @JsonBackReference
    private Room room;

    @OneToMany(mappedBy = "table", cascade = CascadeType.ALL)
    @Builder.Default
    @JsonIgnore
    private List<Reservation> reservations = new ArrayList<>();
}
