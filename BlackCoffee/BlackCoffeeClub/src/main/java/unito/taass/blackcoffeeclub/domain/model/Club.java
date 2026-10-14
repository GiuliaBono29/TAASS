package unito.taass.blackcoffeeclub.domain.model;

import jakarta.persistence.*;
import lombok.*;
import unito.taass.blackcoffeeclub.domain.dto.ClubRequest;

import java.util.*;

@Entity
@Table(name = "club")
@Getter
@Setter(AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Club {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "manager_club", columnDefinition = "BINARY(16)", nullable = false)
    private UUID managerid;

    @Column(name="club_name", columnDefinition = "VARCHAR(255)", nullable=false)
    private String nomeclub;

    // Salviamo gli iscritti come lista di UUID
    @Builder.Default
    @ElementCollection
    @CollectionTable(name = "club_iscritti", joinColumns = @JoinColumn(name = "club_id"))
    @Column(name = "utente_id", columnDefinition = "BINARY(16)")
    private List<UUID> iscritti = new ArrayList<>();

    public void modifyClub(String nomeclub, String description) {
        this.nomeclub=nomeclub;
        this.description=description;
    }

    public void iscrivi(UUID userId) {
        if (userId == null) {
            throw new IllegalArgumentException("Impossibile iscrivere un utente con ID nullo");
        }

        if (this.iscritti == null) {
            this.iscritti = new ArrayList<>();
        }

        if (!this.iscritti.contains(userId)) {
            this.iscritti.add(userId);
            System.out.println("Utente aggiunto alla lista iscritti: " + userId);
        } else {
            System.out.println("Utente non aggiunto alla lista iscritti: " + userId);
        }
    }

    public void disiscrivi(UUID userId) {
        if (this.iscritti != null) {
            this.iscritti.remove(userId);
        }
    }
}

