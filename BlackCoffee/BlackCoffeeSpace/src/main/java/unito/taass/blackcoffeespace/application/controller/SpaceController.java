package unito.taass.blackcoffeespace.application.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import unito.taass.blackcoffeeinfrastructure.model.RoleType;
import unito.taass.blackcoffeespace.domain.dto.*;
import unito.taass.blackcoffeespace.domain.exception.NoTableAvaibleException;
import unito.taass.blackcoffeespace.domain.exception.ReservationNotFoundException;
import unito.taass.blackcoffeespace.domain.model.*;
import unito.taass.blackcoffeespace.domain.service.SpaceService;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import org.springframework.security.core.Authentication;

@RestController
@RequestMapping("/api/space")
public class SpaceController {
    public final SpaceService spaceService;


    public SpaceController(SpaceService spaceService) {
        this.spaceService = spaceService;
    }

    //GESTIONE STANZE E TAVOLI

    @PreAuthorize("hasRole('LIBRAIO')")
    @PostMapping("/create")
    public ResponseEntity<Room> createRoom(@RequestBody RoomRequest roomRequest) {
        Room created = spaceService.createRoom(roomRequest);

        // TODO: Inviare log al microservizio dedicato tramite RabbitMQ
        // Dati necessari: created.getId(), userId, System.currentTimeMillis()
        // Esempio: logProducer.sendEventLog(created.getId(), userId);

        return ResponseEntity.ok(created);
    }

    @PreAuthorize("hasRole('LIBRAIO')")
    @PostMapping("/room/{roomId}/table/create")
    public ResponseEntity<Tables> createTables(@PathVariable("roomId") UUID roomId, @RequestBody TableRequest tableRequest) {
        Tables created = spaceService.createTable(roomId, tableRequest);

        if (created == null) {
            return ResponseEntity.notFound().build();
        }

        // TODO: Inviare log al microservizio dedicato tramite RabbitMQ
        // Dati necessari: created.getId(), userId, System.currentTimeMillis()
        // Esempio: logProducer.sendEventLog(created.getId(), userId);

        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/room/{roomId}")
    public ResponseEntity<?> getRoomById(@PathVariable("roomId") UUID roomId) {
        Room room = spaceService.getRoomById(roomId);

        if(room == null){
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(room);
    }

    @GetMapping("/rooms")
    public List<Room> getAllRooms() {
        return spaceService.getAllRooms();
    }

    @GetMapping("/room/{roomId}/tables")
    public ResponseEntity<List<Tables>> getAllTables(@PathVariable("roomId") UUID roomId) {
        List<Tables> tables = spaceService.getRoomById(roomId).getTables();

        if (tables == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(tables);
    }

    @PreAuthorize("hasRole('LIBRAIO')")
    @PutMapping("/room/modify/{roomId}")
    public ResponseEntity<Room> modifyRoom(@PathVariable("roomId") UUID roomId, @RequestBody RoomRequest roomRequest) {
        try {
            Room modified = spaceService.modifyRoom(roomId, roomRequest);
            return ResponseEntity.ok(modified);
        } catch (IllegalStateException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PreAuthorize("hasRole('LIBRAIO')")
    @DeleteMapping("/room/delete/{roomId}")
    public ResponseEntity<?> deleteRoom(@PathVariable("roomId") UUID roomId) {
        boolean deleted = spaceService.deleteRoom(roomId);
        if (deleted) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PreAuthorize("hasRole('LIBRAIO')")
    @DeleteMapping("/room/{roomId}/table/delete/{tableId}")
    public ResponseEntity<?> deleteTable(@PathVariable("roomId") UUID roomId, @PathVariable("tableId") UUID tableId) {
        boolean deleted = spaceService.deleteTable(roomId, tableId);
        if (deleted) {

            // TODO: Inviare log al microservizio dedicato tramite RabbitMQ
            // Dati necessari: deleted.getId(), userId, System.currentTimeMillis()
            // Esempio: logProducer.sendEventLog(deleted.getId(), userId);

            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    //GESTIONE PRENOTAZIONI

    //trova i tavoli disponibili
    @PostMapping("/disponibility/{role}")
    public ResponseEntity<List<RoomResponse>> searchAvailableRoom(@RequestBody ReservationRequest request, @PathVariable RoleType role) {

        List<RoomResponse> rooms;

        if(role.equals(RoleType.LIBRAIO)) {
            rooms = spaceService.findAvailableRooms(request);
        } else {
            rooms = spaceService.findAvailableTables(request);
        }

        if (rooms.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(rooms);
    }

    //trova le mie prenotazioni
    @PreAuthorize("hasAnyRole('CLIENTE', 'LIBRAIO')")
    @GetMapping("/reservation")
    public ResponseEntity<List<ReservationResponse>> searchMyReservations(Authentication authentication) {
        if(authentication == null){
            return ResponseEntity.status(401).build();
        }

        String userIdStr = authentication.getName();
        UUID userId = UUID.fromString(userIdStr);

        List<ReservationResponse> reservations = spaceService.findMyReservations(userId);

        if (reservations.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(reservations);
    }

    @PreAuthorize("hasAnyRole('CLIENTE', 'LIBRAIO')")
    @GetMapping("/reservation/{reservationId}")
    public ResponseEntity<ReservationResponse> searchReservationById(@PathVariable UUID reservationId) {
        ReservationResponse reservation = spaceService.findReservationById(reservationId);

        return ResponseEntity.ok(reservation);
    }

    @PreAuthorize("hasAnyRole('CLIENTE', 'ORGANIZZATORE', 'LIBRAIO')")
    @DeleteMapping("/reservation/delete/{reservationId}")
    public ResponseEntity<?> deleteMyReservations(@PathVariable("reservationId") UUID reservationId) {
        boolean deleted = spaceService.deleteReservation(reservationId);
        if (deleted) {

            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PreAuthorize("hasAnyRole('CLIENTE', 'LIBRAIO')")
    @PostMapping("/room/tavolo/{tableId}/prenota")
    public ResponseEntity<?> reserveTable(@PathVariable UUID tableId, @RequestBody ReservationRequest request){
        boolean success = spaceService.reserveTable(tableId, request);

        if (success) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.badRequest().body("Tavolo non disponibile");
        }
    }

    @PreAuthorize("hasAnyRole('CLIENTE', 'LIBRAIO')")
    @PutMapping("/reservation/modify/{reservationId}")
    public ResponseEntity<?> modifyReservation(@PathVariable UUID reservationId, @RequestBody ReservationRequest request){
        System.out.println("DEBUG CONTROLLER: Data: " + request.getDate() + ", Slot: " + request.getSlot() + ", Seats: " + request.getSeats());
        try{
            Reservation updated = spaceService.modifyReservation(reservationId, request);
            return ResponseEntity.ok(updated);
        }catch(ReservationNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }catch (NoTableAvaibleException e){
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }catch (Exception e){
            return ResponseEntity.internalServerError().body("Errore durante la modifica");
        }
    }

    //GESTIONE PRENOTAZIONI PER EVENTI

    @PreAuthorize("hasRole('LIBRAIO')")
    @GetMapping("/room/request")
    public ResponseEntity<List<Message>> request(Authentication authentication){
        List<Message> reservations = spaceService.findRequest();

        if (reservations.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(reservations);
    }

    @PreAuthorize("hasRole('LIBRAIO')")
    @GetMapping("/room/request/{action}")
    public ResponseEntity<List<Message>> requestByState(@PathVariable State action){
        List<Message> reservations = spaceService.findRequestByState(action);

        if (reservations.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(reservations);
    }

    @PreAuthorize("hasRole('LIBRAIO')")
    @DeleteMapping("/room/message/delete")
    public ResponseEntity<?> deleteRejectMessages(){
        try {
            spaceService.deleteRejectedMessages();
            return ResponseEntity.ok("Tutti i messaggi rifiutati eliminati con successo");
        } catch (Exception e){
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @PreAuthorize("hasRole('LIBRAIO')")
    @PostMapping("/room/{roomId}/prenota")
    public ResponseEntity<?> reserveRoom(@PathVariable UUID roomId, @RequestBody ReservationRequest request){
        System.out.println("DEBUG CONTROLLER: Ricevuto eventId = " + request.getEventId());
        boolean success = spaceService.reserveRoom(roomId, request);

        if (success) {
            spaceService.closeRequest(request.getEventId(), State.ACCEPTED);
            // Restituiamo una mappa che Spring trasforma in {"message": "..."}
            return ResponseEntity.ok(Collections.singletonMap("message", "Prenotazione effettuata e risposta inviata!"));
        } else {
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", "Stanza non più disponibile"));
        }
    }

    @PreAuthorize("hasRole('LIBRAIO')")
    @GetMapping("/room/message/{id}")
    public ResponseEntity<Message> getMessageDetails(@PathVariable UUID id) {
        return spaceService.getMessageById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasRole('LIBRAIO')")
    @PostMapping("/room/reject")
    public ResponseEntity<?> rejectRequest(@RequestBody ReservationRequest request){
        Boolean success = spaceService.rejectReservation(request);

        if (success) {
            spaceService.closeRequest(request.getEventId(), State.REJECTED);
            return ResponseEntity.ok("Prenotazione rifiutata");
        } else {
            return ResponseEntity.badRequest().body("Errore");
        }
    }

}