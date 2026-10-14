package unito.taass.blackcoffeespace.domain.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.*;

@Entity
@Table(name = "room")
@Getter
@Setter(AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;

    @Column(name = "name_room", columnDefinition = "VARCHAR(255)", nullable = false)
    private String name;

    @Column(name = "capacity_room")
    private int capacity;

    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @JsonManagedReference
    private List<Tables> tables = new ArrayList<>();

    public void modifica(String name, int capacity) {
        this.name = name;
        this.capacity = capacity;
    }
}
