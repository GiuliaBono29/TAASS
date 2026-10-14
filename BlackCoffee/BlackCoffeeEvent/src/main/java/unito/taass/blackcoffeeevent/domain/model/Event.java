package unito.taass.blackcoffeeevent.domain.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.util.*;

@Entity
@Table(name = "event")
@Getter
@Setter(AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;

    @Column(name = "manager_id", nullable = false, columnDefinition = "BINARY(16)")
    private UUID managerId;

    @Column(name = "date_event", nullable = false)
    private LocalDate date;

    @Enumerated(EnumType.STRING)
    @Column(name = "type_event", nullable = false, length = 20)
    private EventType tipo;

    @Column(columnDefinition = "TEXT")
    private String descrizione;

    @Column(name = "space_id", columnDefinition = "BINARY(16)", nullable = true)
    private UUID spazioId;

    @Column(name = "catering_id", columnDefinition = "BINARY(16)", nullable = true)
    private UUID cateringId;

    @Column(name = "club_id", columnDefinition = "BINARY(16)", nullable = true)
    private UUID clubId;

    @Enumerated(EnumType.STRING)
    @Column(name = "state_event", nullable = false, length = 25)
    @Builder.Default
    private StateEvent stato = StateEvent.IN_CREAZIONE;

    @Column(name = "name_event", columnDefinition = "VARCHAR(255)")
    private String nomeEvento;

    @ElementCollection(fetch = FetchType.EAGER) // Eager assicura che la lista sia caricata subito
    @CollectionTable(
            name = "event_subscriber", // Nota: correggi 'subcriber' in 'subscriber' se vuoi
            joinColumns = @JoinColumn(name = "event_id")
    )
    @Column(name = "user_id", columnDefinition = "BINARY(16)")
    @Builder.Default
    private List<UUID> iscritti = new ArrayList<>();

    //Domain Logic
    public void aggiungiIscritto(UUID utenteid){
        if(stato!= StateEvent.PUBBLICATO){
            throw new IllegalStateException("Non ci si può iscrivere a un evento non pubblicato");
        }
        this.iscritti.add(utenteid);
    }

    public void pubblica(){
        if(this.getStato() == StateEvent.ATTESA_PUBB){
            this.stato= StateEvent.PUBBLICATO;
        }else {
            throw new IllegalStateException("L'evento non può essere pubblicato perché non è in attesa di pubblicazione (Stato attuale: " + this.stato + ")");
        }
    }

    public void cancella(){
        this.stato= StateEvent.CANCELLATO;
    }

    public void attesastanza(){
        this.stato=StateEvent.ATTESA_STANZA;
    }
    public void attesacatering(){
        this.stato=StateEvent.ATTESA_CATERING;
    }
    public void attesapubb(){
        this.stato=StateEvent.ATTESA_PUBB;
    }

    public void aggiungiStanza(UUID stanzaId){
        this.spazioId =stanzaId;
    }

    public void modifica(String name, LocalDate date, String type, String description) {
        this.nomeEvento = name;
        this.date = date;
        this.descrizione = description;

        // Convertiamo la stringa nell'Enum corrispondente
        if (type != null) {
            try {
                this.tipo = EventType.valueOf(type.toUpperCase());
            } catch (IllegalArgumentException e) {
                // Opzionale: gestire l'errore se la stringa non esiste nell'Enum
                throw new RuntimeException("Tipo evento non valido: " + type);
            }
        }
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

    public void aggiungiCatering(UUID cateringId) {
        if (cateringId == null) {
            throw new IllegalArgumentException("Errore Id Nullo");
        }

        this.cateringId = cateringId;
    }

    public void assignToClub(UUID clubId) {
        if (clubId == null) {
            throw new IllegalArgumentException("Il clubId non può essere nullo");
        }
        this.clubId = clubId;
    }
}
