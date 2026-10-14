package unito.taass.blackcoffeeevent.domain.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import unito.taass.blackcoffeeevent.domain.dto.EventRequest;
import unito.taass.blackcoffeeevent.domain.dto.ReservationRequest;
import unito.taass.blackcoffeeevent.domain.dto.ReservationResponse;
import unito.taass.blackcoffeeevent.domain.messaging.EventPublisher;
import unito.taass.blackcoffeeevent.domain.model.*;
import unito.taass.blackcoffeeevent.domain.repository.*;
import lombok.RequiredArgsConstructor;
import unito.taass.blackcoffeeinfrastructure.model.NotificationType;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class EventService {

    private final EventRepository repository;

    private final EventPublisher eventPublisher;    //necessario per RabbitMQ

    private final CateringMockService cateringMockService;

    public Event createEvent(EventRequest eventRequest) {

        UUID currentUserID = getCurrentUserID();

        Event event = Event.builder()
                .nomeEvento(eventRequest.nameEvent)
                .date(eventRequest.dateEvent)
                .tipo(EventType.valueOf(eventRequest.typeEvent)) // converti stringa in enum
                .descrizione(eventRequest.description)
                .managerId(currentUserID)
                .stato(StateEvent.IN_CREAZIONE) // di default
                .iscritti(new ArrayList<>()) // lista vuota
                .build();

        Event savedEvent = repository.save(event);

        eventPublisher.publishLog(
                "CREAZIONE_EVENTO",
                "Creato evento: " + savedEvent.getNomeEvento() +
                        " con id: " + savedEvent.getId() +
                        ", da Organizzatore: " + savedEvent.getManagerId(),
                "INFO"
                );

        return savedEvent;
    }

    public boolean deleteEvent(UUID id) {
        UUID currentUserID = getCurrentUserID();
        Event event = repository.findById(id)
                .orElseThrow(() -> new IllegalStateException("Eveto inesistente"));

        if(repository.existsById(id) && currentUserID.equals(event.getManagerId())){
            try {
                repository.deleteById(id);
                eventPublisher.publishEventDeletion(id);

                eventPublisher.publishLog(
                        "ELIMINAZIONE_EVENTO",
                        "Eliminato evento: " + event.getNomeEvento() +
                                " con id: " + event.getId() +
                                ", da utente: " + currentUserID,
                        "WARN"
                );
                return true;
            } catch (Exception e) {

                eventPublisher.publishLog(
                        "ERRORE_ELIMINAZIONE_EVENTO",
                        "L'evento: " + event.getNomeEvento() +
                                " con id: " + event.getId() +
                                ", ha presentato un errore durante l'eliminazione: " + e.getMessage(),
                        "ERROR"
                );

                return false;
            }
        }

        return false;
    }

    public List<Event> getAllEvents() {
        return repository.findAll();
    }

    public Event getEventById(UUID id) {
        return repository.findById(id).orElse(null);
    }

    public void verifySpaceAvailability(LocalDate date, UUID spaceID){
        //implementa
    }

    public Event modifyStateEvent (UUID eventID,  String state) {

        UUID currentUserID = getCurrentUserID();
        Event e = repository.findById(eventID)
                .orElseThrow(() -> new IllegalStateException("Evento inesistente"));

        if(!currentUserID.equals(e.getManagerId())){
            throw new RuntimeException("Permessi non disponibili per la modifica");
        }

        if(state.equals("pubblicato")) {
            e.pubblica();

            if (e.getClubId() != null) {
                eventPublisher.publishToClub(e.getClubId(), e.getNomeEvento(), e.getDate(), e.getId());
            }

            eventPublisher.publishLog(
                    "PUBBLICAZIONE_EVENTO",
                    "Evento: " + e.getNomeEvento() +
                            " con id: " + e.getId() +
                            ", è stato pubblicato dall'utente: " + currentUserID,
                    "INFO"
            );
        } else if (state.equals("cancellato")){
            e.cancella();

            eventPublisher.publishLog(
                    "PUBBLICAZIONE_EVENTO",
                    "Evento: " + e.getNomeEvento() +
                            " con id: " + e.getId() +
                            ", è stato cancellato dall'utente: " + currentUserID,
                    "WARN"
            );
        }

        return repository.save(e);
    }

    public Event modifyEvent (UUID eventId, EventRequest eventRequest) {
        UUID currentUserID = getCurrentUserID();
        Event e = repository.findById(eventId)
                .orElseThrow(() -> new IllegalStateException("Evento inesistente"));

        if(!currentUserID.equals(e.getManagerId())){
            throw new RuntimeException("Permessi non disponibili per la modifica");
        }
        e.modifica(
                eventRequest.nameEvent,
                eventRequest.dateEvent,
                eventRequest.typeEvent,
                eventRequest.description
        );

        Event savedEvent = repository.save(e);

        eventPublisher.publishLog(
                "MODIFICA_EVENTO",
                "Evento modificato: " + savedEvent.getNomeEvento() +
                        " con id: " + savedEvent.getId() +
                        ", da Organizzatore: " + savedEvent.getManagerId(),
                "INFO"
        );

        return savedEvent;
    }

    @Transactional
    public Event subEvent(UUID eventId, UUID userId, String action) {
        if (userId == null) throw new IllegalArgumentException("User ID mancante nel token");

        Event e = repository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("Evento inesistente"));

        if ("addUser".equals(action)) {

            if (e.getClubId() != null) {
                RestTemplate restTemplate = new RestTemplate();
                String url = "http://club-service:8083/api/club/is-member/" + e.getClubId() + "/" + userId;
                Boolean isMember = null;
                try {
                    isMember = restTemplate.getForObject(url, Boolean.class);
                } catch (org.springframework.web.client.HttpClientErrorException err) {
                    throw new RuntimeException("Il club service ha restituito errore: " + err.getStatusCode());
                } catch (Exception error) {
                    throw new RuntimeException("Errore di comunicazione");
                }
                if (Boolean.FALSE.equals(isMember) || isMember == null) {
                    throw new IllegalArgumentException("Devi essere iscritto al club per partecipare a questo evento");
                }
            }

            e.iscrivi(userId);

            eventPublisher.publishLog(
                    "ISCRIZIONE_EVENTO",
                    "Utente iscritto: " + userId +
                            ", all'evento " + e.getNomeEvento() +
                            " con id: " + e.getId(),
                    "INFO"
            );

        } else if ("rmUser".equals(action)) {
            e.disiscrivi(userId);

            eventPublisher.publishLog(
                    "ISCRIZIONE_EVENTO",
                    "Utente disiscritto: " + userId +
                            ", dall'evento " + e.getNomeEvento() +
                            " con id: " + e.getId(),
                    "INFO"
            );
        }

        return repository.saveAndFlush(e);
    }

    private UUID getCurrentUserID() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if(authentication == null || !authentication.isAuthenticated()){
            throw new RuntimeException("Utente non autenticato");
        }

        String userId = authentication.getName();
        return UUID.fromString(userId);
    }

    //FUNZIONI GESTIONE SPAZI
    @Transactional
    public boolean orchestrateSpaceCatering(ReservationRequest request, String description){
        Boolean spaceSuccess = createSpaceEvent(request);
        Boolean cateringSuccess = createCateringEvent(request.getEventId(), description);

        return spaceSuccess && cateringSuccess;
    }

    private Boolean createCateringEvent(UUID eventId, String description) {
        UUID cateringId = cateringMockService.getAvaiableMenus(description);
        Event e = repository.findById(eventId)
                .orElseThrow(() -> new IllegalStateException("Evento inesistente"));
        e.aggiungiCatering(cateringId);
        if(e.getStato() == StateEvent.IN_CREAZIONE){
            e.attesastanza();
        }else if(e.getStato() == StateEvent.ATTESA_CATERING){
            e.attesapubb();
        }

        repository.save(e);

        eventPublisher.publishLog(
                "ASSEGNAMENTO_CATERING",
                "All'evento: " + e.getNomeEvento() +
                        " con id: " + e.getId() +
                        ", è stato assegnato il catering: " + cateringId,
                "INFO"
        );

        return true;
    }

    public Boolean createSpaceEvent(ReservationRequest reservationRequest) {
        //Recupero evento dal database
        Event evento = repository.findById(reservationRequest.getEventId())
                .orElse(null);

        if (evento == null) {
            return false;
        }

        //Invio messaggio RabbitMQ
        try {
            eventPublisher.publishEventReservation(reservationRequest);

            eventPublisher.publishLog(
                    "ASSEGNAMENTO_SPACE",
                    "All'evento: " + evento.getNomeEvento() +
                            " con id: " + evento.getId() +
                            ", è stata inviata la richiesta per space",
                    "INFO"
            );

            return true;
        } catch (Exception e) {

            eventPublisher.publishLog(
                    "ERRORE_ASSEGNAMENTO_SPACE",
                    "L'evento: " + evento.getNomeEvento() +
                            " con id: " + evento.getId() +
                            ", ha presentato un errore durante la richiesta di space: " +
                            e.getMessage(),
                    "ERROR"
            );

            return false;
        }
    }

    @Transactional
    public Boolean assignClub(UUID eventId, UUID clubId) {

        Event event = repository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Evento non trovato"));

        try {
            event.assignToClub(clubId);

            eventPublisher.publishLog(
                    "ASSEGNAMENTO_EVENT",
                    "L'evento: " + event.getNomeEvento() +
                            " con id: " + event.getId() +
                            ", è indicato come 'Serata Club' del club con id: " +
                            clubId,
                    "INFO"
            );
            return true;
        } catch (Exception e) {

            eventPublisher.publishLog(
                    "ERRORE_ASSEGNAMENTO_EVENT",
                    "L'evento: " + event.getNomeEvento() +
                            " con id: " + event.getId() +
                            ", ha presentato un errore durante l'assegnamento del club: " +
                            e.getMessage(),
                    "ERROR"
            );

            return false;
        }
    }

    @Transactional
    public void handleSpaceResponse(String action, ReservationResponse response) {
        // Cerchiamo l'evento nel database di EventService
        Event evento = repository.findById(response.getEventId())
                .orElseThrow(() -> new RuntimeException("Evento non trovato"));

        if ("accepted".equalsIgnoreCase(action)) {
            StateEvent statoAttuale = evento.getStato();

            if (statoAttuale == StateEvent.IN_CREAZIONE) {
                evento.attesacatering();
            }
            else if (statoAttuale == StateEvent.ATTESA_STANZA) {
                evento.attesapubb();
            }

            evento.aggiungiStanza(response.getRoomId());

            eventPublisher.publishNotification(
                    evento.getManagerId(),
                    "Spazio Confermato",
                    "la stanza " + response.getRoomName() + " è stata assegnata al tuo evento: " + evento.getNomeEvento(),
                    NotificationType.SPACE_ASSIGNED
            );

            eventPublisher.publishLog(
                    "ASSEGNAMENTO_SPACE",
                    "All'evento: " + evento.getNomeEvento() +
                            " con id: " + evento.getId() +
                            ", è stato assegnato lo spazio: " + evento.getSpazioId(),
                    "INFO"
            );

        } else if ("rejected".equalsIgnoreCase(action)){
            evento.cancella();

            eventPublisher.publishNotification(
                    evento.getManagerId(),
                    "Spazio Rifiutato",
                    "Spiacenti, la richiesta di spazio per l'evento " +
                            evento.getNomeEvento() +
                            " è stata rifiutata. Provare con una data diversa",
                    NotificationType.ERROR
            );

            eventPublisher.publishLog(
                    "EVENT_REJECTED",
                    "La richiesta di spazio per l'evento " +
                            evento.getId() +
                            " è stata rifiutata",
                    "WARN"
            );
        } else {
            System.out.println("Azione non riconosciuta da Space: " + action);
            return;
        }

        repository.save(evento);
    }


}
