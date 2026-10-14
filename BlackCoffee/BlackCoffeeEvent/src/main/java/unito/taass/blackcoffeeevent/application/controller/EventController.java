package unito.taass.blackcoffeeevent.application.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import unito.taass.blackcoffeeevent.domain.dto.EventRequest;
import unito.taass.blackcoffeeevent.domain.dto.ReservationRequest;
import unito.taass.blackcoffeeevent.domain.model.Event;
import unito.taass.blackcoffeeevent.domain.service.EventService;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/event")
public class EventController {
    public final EventService eventService;


    public EventController(EventService eventService) {
        this.eventService = eventService;
    }


    @PreAuthorize("hasRole('ORGANIZZATORE')")
    @PostMapping("/create")
    public ResponseEntity<Event> createEvent(@RequestBody EventRequest eventRequest) {
        Event created = eventService.createEvent(eventRequest);

        System.out.println("Event ID: " + created.getId());
        // TODO: Inviare log al microservizio dedicato tramite RabbitMQ
        // Dati necessari: created.getId(), userId, System.currentTimeMillis()
        // Esempio: logProducer.sendEventLog(created.getId(), userId);

        return ResponseEntity.ok(created);
    }

    @PreAuthorize("hasRole('ORGANIZZATORE')")
    @PostMapping("/create/other_info")
    public ResponseEntity<?> createSpaceCatmodering(@RequestBody ReservationRequest reservationRequest, @RequestParam String description) {
        Boolean success = eventService.orchestrateSpaceCatering(reservationRequest, description);
        if (success) {
            // Restituisci una mappa o un DTO, non una stringa nuda
            return ResponseEntity.ok(Collections.singletonMap("message", "Richiesta inviata!"));
        }
        return ResponseEntity.badRequest().body(Collections.singletonMap("error", "Errore"));
    }

    @PreAuthorize("hasRole('ORGANIZZATORE')")
    @PostMapping("/create/{eventId}/club/{clubId}")
    public ResponseEntity<?> assignClub(@PathVariable UUID eventId, @PathVariable UUID clubId) {
        Boolean success = eventService.assignClub(eventId, clubId);
        if (success) {
            // Restituisci una mappa o un DTO, non una stringa nuda
            return ResponseEntity.ok(Collections.singletonMap("message", "Club collegato!"));
        }
        return ResponseEntity.badRequest().body(Collections.singletonMap("error", "Errore"));
    }

    @GetMapping("/all")
    public List<Event> getAllEvents() {
        return eventService.getAllEvents();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getEventByID(@PathVariable("id") UUID id) {
        Event event = eventService.getEventById(id);

        if (event == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(event);
    }

    @PreAuthorize("hasRole('ORGANIZZATORE')")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteEvent(@PathVariable("id") UUID id) {
        boolean deleted = eventService.deleteEvent(id);
        if (deleted) {

            // TODO: Inviare log al microservizio dedicato tramite RabbitMQ
            // Dati necessari: deleted.getId(), userId, System.currentTimeMillis()
            // Esempio: logProducer.sendEventLog(deleted.getId(), userId);

            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PreAuthorize("hasRole('ORGANIZZATORE')")
    @PutMapping("/modifyState/{id}/{state}")
    public ResponseEntity<?> modifyStateEvent(@PathVariable UUID id, @PathVariable String state) {

        try {
            Event updated = eventService.modifyStateEvent(id, state);

            // TODO: Inviare log al microservizio dedicato tramite RabbitMQ
            // Dati necessari: updated.getId(), action, userId, System.currentTimeMillis()
            // Esempio: logProducer.sendEventLog(updated.getId(), action, userId);

            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Errore interno");
        }
    }

    @PreAuthorize("hasRole('ORGANIZZATORE')")
    @PutMapping(value = "/modify/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> modifyEvent(@PathVariable UUID id, @RequestBody EventRequest eventRequest) {
        try {
            Event modifiedEvent = eventService.modifyEvent(id, eventRequest);

            // TODO: Inviare log al microservizio dedicato tramite RabbitMQ
            // Dati necessari: modifyEvent.getId(), userId, System.currentTimeMillis()
            // Esempio: logProducer.sendEventLog(modifyEvent.getId(), action, userId);

            return ResponseEntity.ok(modifiedEvent);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PreAuthorize("hasRole('CLIENTE')")
    @PutMapping("/event_sub/{action}/{eventid}")
    public ResponseEntity<?> subEvent(Authentication authentication, @PathVariable String action, @PathVariable UUID eventid) {

        UUID userId = UUID.fromString(authentication.getName());
        try {
            Event updated = eventService.subEvent(eventid, userId, action);

            // TODO: Inviare log al microservizio dedicato tramite RabbitMQ
            // Dati necessari: updated.getId(), action, userId, System.currentTimeMillis()
            // Esempio: logProducer.sendEventLog(updated.getId(), action, userId);

            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

}