package unito.taass.blackcoffeespace.domain.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import unito.taass.blackcoffeeinfrastructure.model.NotificationType;
import unito.taass.blackcoffeeinfrastructure.model.RoleType;
import unito.taass.blackcoffeespace.domain.dto.*;
import unito.taass.blackcoffeespace.domain.exception.NoTableAvaibleException;
import unito.taass.blackcoffeespace.domain.exception.ReservationNotFoundException;
import unito.taass.blackcoffeespace.domain.messaging.SpaceEventPublisher;
import unito.taass.blackcoffeespace.domain.model.*;
import lombok.RequiredArgsConstructor;
import unito.taass.blackcoffeespace.domain.repository.MessageRepository;
import unito.taass.blackcoffeespace.domain.repository.ReservationRepository;
import unito.taass.blackcoffeespace.domain.repository.RoomRepository;
import unito.taass.blackcoffeespace.domain.repository.TablesRepository;

import java.lang.module.ResolutionException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class SpaceService {

    private final RoomRepository roomRepository;
    private final TablesRepository tablesRepository;
    private final ReservationRepository reservationRepository;
    private final MessageRepository messageRepository;
    private final SpaceEventPublisher spaceEventPublisher;    //necessario per RabbitMQ

    //METODI GESTIONE STANZE E TAVOLI
    public Room createRoom(RoomRequest roomRequest) {
        Room room = Room.builder()
                .name(roomRequest.name)
                .capacity(roomRequest.capacity)
                .tables(new ArrayList<>()) // lista vuota
                .build();

        spaceEventPublisher.publishLog(
                "CREAZIONE_STANZA",
                "E' stata creata la stanza " + room.getName() +
                        " con id " + room.getId() +
                        " e con capacità " + room.getCapacity(),
                "INFO");

        return roomRepository.save(room);
    }

    public Tables createTable(UUID roomId, TableRequest tableRequest) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResolutionException("Stanza non trovata"));

        Tables tables = Tables.builder()
                .number(tableRequest.number)
                .capacity(tableRequest.capacity)
                .room(room)
                .reservations(new ArrayList<>()) // lista vuota
                .build();

        room.getTables().add(tables);

        spaceEventPublisher.publishLog(
                "CREAZIONE_TAVOLO",
                "E' stato creato il tavolo " + tables.getNumber() +
                        " con id " + tables.getId() +
                        " con capacità " + tables.getCapacity(),
                "INFO");

        return tablesRepository.save(tables);
    }

    @Transactional
    public boolean deleteRoom(UUID roomId) {
        Optional<Room> roomOpt = roomRepository.findById(roomId);

        if (roomOpt.isPresent()) {
            roomRepository.delete(roomOpt.get());

            spaceEventPublisher.publishLog(
                    "ELIMINAZIONE_STANZA",
                    "Eliminata stanza ID: " + roomId,
                    "WARN");
            return true;
        } else {
            spaceEventPublisher.publishLog(
                    "ELIMINAZIONE_STANZA_FALLITA",
                    "Tentativo di eliminazione stanza inesistente ID: " + roomId,
                    "ERROR");
            return false;
        }
    }

    @Transactional
    public boolean deleteTable(UUID roomId, UUID tableId) {
        Optional<Room> roomOpt = roomRepository.findById(roomId);
        if(roomOpt.isEmpty()) return false;
        Room room = roomOpt.get();
        Optional<Tables> tableOpt = tablesRepository.findById(tableId);

        if(tableOpt.isPresent()){
            Tables table = tableOpt.get();

            if(table.getRoom().getId().equals(roomId)){
                room.getTables().remove(table);
                tablesRepository.delete(table);

                spaceEventPublisher.publishLog(
                        "ELIMINAZIONE_TAVOLO",
                        "E' stato eliminato il tavolo con id " + tableId +
                        " dalla stanza con id " + roomId,
                        "WARN");

                return true;
            }
        }

        spaceEventPublisher.publishLog(
                "ELIMINAZIONE_TAVOLO",
                "Il tavolo con id " + tableId +
                        "della stanza con id " + roomId +
                        "non è stato eliminato correttamente",
                "ERROR"
        );

        return false;
    }

    public List<Room> getAllRooms() {
        return roomRepository.findAll();
    }

    public List<Tables> getAllTables() {
        return tablesRepository.findAll();
    }

    public Room getRoomById(UUID id) {
        return roomRepository.findById(id).orElse(null);
    }

    public Tables getTablesById(UUID id) {
        return tablesRepository.findById(id).orElse(null);
    }

    public Room modifyRoom (UUID roomId, RoomRequest roomRequest) {
        Room r = roomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalStateException("Stanza inesistente"));

        r.modifica(
                roomRequest.name,
                roomRequest.capacity
        );

        spaceEventPublisher.publishLog(
                "MODIFICA_STANZA",
                "E' stata modificata la stanza con id " + roomId,
                "INFO");

        return roomRepository.save(r);
    }

    //METODI GESTIONE PRENOTAZIONI
    public List<ReservationResponse> findMyReservations (UUID userId) {
        List<Reservation> userReservations = reservationRepository.findByOwner(userId);

        return userReservations.stream().map(res -> {
            ReservationResponse response = generateReservationResponse(res);
            return response;
        }).collect(Collectors.toList());

    }

    public ReservationResponse findReservationById (UUID reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Prenotazione non trovata"));

        return generateReservationResponse(reservation);


    }

    public List<RoomResponse> findAvailableTables(ReservationRequest request) {

        List<Tables> availableTables = tablesRepository.findAvailableTables(
                request.getDate(),
                request.getSlot(),
                request.getSeats()
        );

        return availableTables.stream()
                .collect(Collectors.groupingBy(Tables::getRoom)) // Raggruppa per oggetto Room
                .entrySet().stream()
                .map(entry -> {
                    Room room = entry.getKey();
                    List<Tables> tablesInRoom = entry.getValue();

                    List<TableResponse> tableDtos = tablesInRoom.stream()
                            .map(t -> new TableResponse(t.getId(), t.getNumber(), t.getCapacity()))
                            .collect(Collectors.toList());

                    return new RoomResponse(
                            room.getId(),
                            room.getName(),
                            room.getCapacity(),
                            tableDtos
                    );
                })
                .collect(Collectors.toList());
    }

    public List<RoomResponse> findAvailableRooms(ReservationRequest request) {
        List<Room> allRooms = roomRepository.findAll();

        return allRooms.stream()
                .filter(room -> isRoomFullyAvailable(room, request)) //Solo stanze con tutti tavoli disponibili
                .filter(room -> room.getCapacity()>=request.seats)
                .map(room -> {
                    //Creazione RoomResponse
                    List<TableResponse> tableDtos = room.getTables().stream()
                            .map(t -> new TableResponse(t.getId(), t.getNumber(), t.getCapacity()))
                            .toList();

                    return new RoomResponse(
                            room.getId(),
                            room.getName(),
                            room.getCapacity(),
                            tableDtos
                    );
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public boolean reserveTable(UUID tableId, ReservationRequest request) {
        Tables tables = tablesRepository.findById(tableId).orElse(null);
        UUID currentUserId = getCurrentUserID();

        if (tables != null && isTableFree(tables, request.getDate(), request.getSlot(), request.getSeats())){
            Reservation reservation = Reservation.builder()
                    .table(tables)
                    .date(request.getDate())
                    .slot(request.getSlot())
                    .owner(currentUserId)
                    .seats(request.getSeats())
                    .event(null)    //non si prenotano i tavoli per gli eventi, ma le stanze
                    .build();

            reservationRepository.save(reservation);

            spaceEventPublisher.publishLog(
                    "PRENOTAZIONE_TAVOLO",
                    "E' stata prenotato il tavolo con id " + tableId +
                    " dall'utente " + reservation.getOwner() +
                    " per la data " + reservation.getDate(),
                    "INFO");

            return true;
        }

        return false;
    }

    @Transactional
    public boolean reserveRoom(UUID roomId, ReservationRequest request) {
        Room room = roomRepository.findById(roomId).orElse(null);
        UUID userId = getCurrentUserID();

        if (room == null || room.getCapacity() < request.getSeats()){
            return false;
        }

        if(isRoomFullyAvailable(room, request)){
            List<Reservation> reservations = room.getTables().stream()
                    .map(t -> Reservation.builder()
                            .table(t)
                            .date(request.getDate())
                            .slot(request.getSlot())
                            .owner(userId)
                            .seats(t.getCapacity())
                            .event(request.getEventId())   //id evento
                            .build())
                    .toList();

            reservationRepository.saveAll(reservations);

            spaceEventPublisher.publishLog(
                    "PRENOTAZIONE_STANZA",
                    "La stanza con id " + room.getId() +
                            " è stata prenotata dall'utente " + reservations.get(0).getOwner() +
                            " per la data " + reservations.get(0).getDate(),
                    "INFO");

            // Se la prenotazione è legata a un evento, dobbiamo avvisare l'EventService
            if (request.getEventId() != null) {
                ReservationResponse response = new ReservationResponse();
                response.setEventId(request.getEventId());
                response.setRoomId(room.getId());
                response.setRoomName(room.getName());
                response.setDate(request.getDate());
                response.setSlot(request.getSlot());

                spaceEventPublisher.publishSpaceEvent("ACCEPTED", response);
            }

            return true;
        }

        return false;
    }

    @Transactional
    public boolean deleteReservation(UUID reservationId) {

        Optional<Reservation> reservationOptional = reservationRepository.findById(reservationId);

        if(reservationOptional.isPresent()){

            Reservation reservation = reservationOptional.get();

            reservationRepository.delete(reservation);

            spaceEventPublisher.publishLog(
                    "ELIMINAZIONE_PRENOTAZIONE",
                    "La prenotazione con id: " + reservation.getId() +
                    " è stata eliminata con successo",
                    "WARN"
            );

            return true;
        } else {

            spaceEventPublisher.publishLog(
                    "ELIMINAZIONE_PRENOTAZIONE",
                    "La prenotazione con id: " + reservationId +
                            " non è stata trovata",
                    "ERROR"
            );


            return false;
        }

    }

    @Transactional
    public Reservation modifyReservation(UUID reservationId, ReservationRequest request){
        Reservation currentRes = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ReservationNotFoundException("Prenotazione non trovata"));

        Tables currentTable = currentRes.getTable();
        System.out.println("DEBUG SERVICE: Data: " + request.getDate() + ", Slot: " + request.getSlot() + ", Seats: " + request.getSeats());
        boolean currentTableValid = isTableFree(currentTable, request.getDate(), request.getSlot(), request.getSeats());

        //tavolo disponibile per le modifiche
        if(currentTableValid){
            currentRes.modifica(request.getDate(), request.getSlot(), request.getSeats(), currentTable);
            reservationRepository.save(currentRes);
            spaceEventPublisher.publishLog(
                    "MODIFICA_PRENOTAZIONE",
                    "La prenotazione con id " + currentRes.getId() + " è stata modificata",
                    "INFO"
            );
            return currentRes;
        }

        //tavolo non disponibile per le modifiche
        //ne cerco un altro e prendo il primo disponibile
        List<Tables> alternatives = tablesRepository.findAvailableTables(
                request.getDate(), request.getSlot(), request.getSeats()
        );

        //se non sono presenti tavoli disponbili
        if (alternatives.isEmpty()) {
            spaceEventPublisher.publishLog(
                    "MODIFICA_PRENOTAZIONE",
                    "Impossibile modificare la prenotazione " + reservationId + ": nessun tavolo alternativo disponibile",
                    "WARN"
            );
            throw new NoTableAvaibleException("Non ci sono tavoli disponibili per la tua richiesta");
        }

        //se so9no presenti tavoli disponibili, prendo il primo e lo
        Tables alternative = alternatives.stream()
                .min(Comparator.comparingInt(Tables::getCapacity))
                .get();

        currentRes.modifica(request.getDate(), request.getSlot(), request.getSeats(), alternative);
        reservationRepository.save(currentRes);

        spaceEventPublisher.publishLog(
                "MODIFICA_PRENOTAZIONE",
                "La prenotazione con id " + currentRes.getId() +
                        " è stata modificata, assegnato nuovo tavolo con id " + currentRes.getTable().getId(),
                "INFO"
        );
        return currentRes;
    }

    //METODI RABBITMQ

    @Transactional
    public void handleIncomingRequest(ReservationRequest request) {
        // 1. Trasformiamo il DTO in una Entity Message per il DB di Space
        Message nuovaRichiesta = new Message();

        // Assicurati che il DTO abbia i setter (@Data in Lombok)
        nuovaRichiesta.setEventId(request.getEventId());
        nuovaRichiesta.setEventName(request.getEventName());
        nuovaRichiesta.setManagerName(request.getManagerName());
        nuovaRichiesta.setDate(request.getDate());
        nuovaRichiesta.setSlot(request.getSlot());
        nuovaRichiesta.setSeats(request.getSeats());

        // Stato iniziale: il Libraio dovrà poi approvare o rifiutare
        nuovaRichiesta.setState(State.PENDING);

        // 2. Salvataggio su database
        messageRepository.save(nuovaRichiesta);

        spaceEventPublisher.publishNotification(
                RoleType.LIBRAIO,
                "Ricezione messaggio",
                "Messaggio ricevuto dall'organizzatore: " +
                        request.getManagerName(),
                NotificationType.EVENT_CREATED
        );

        spaceEventPublisher.publishLog(
                "RICEZIONE_MESSAGGIO",
                "Il messaggio: " +
                        nuovaRichiesta.getId() +
                        "per l'evento: " +
                        nuovaRichiesta.getEventId() +
                        " è stato ricevuto con successo",
                "INFO"
        );
    }

    public List<Message> findRequest() {
        // Recuperiamo tutti i "messaggi" ricevuti da RabbitMQ e salvati nel DB
        // Filtriamo per PENDING perché il Libraio deve vedere solo ciò che è da gestire
        return messageRepository.findAll();
    }

    public List<Message> findRequestByState(State state) {
        // Recuperiamo tutti i "messaggi" ricevuti da RabbitMQ e salvati nel DB
        // Filtriamo per PENDING perché il Libraio deve vedere solo ciò che è da gestire
        return messageRepository.findAllByState(state);
    }

    public Optional<Message> getMessageById(UUID id) {
        return messageRepository.findById(id);
    }

    @Transactional
    public void closeRequest(UUID eventId, State state) {
        if (eventId == null) {
            return;
        }

        Message m = messageRepository.findByEventId(eventId)
                .orElseGet(() -> {
                    return null;
                });

        if (m != null) {
            m.modifyState(state);
            messageRepository.save(m);
        }

        spaceEventPublisher.publishLog(
                "CHIUSURA_RICHIESTA",
                "La richiesta " +
                        m.getId() +
                        " per l'evento: " +
                        m.getEventId() +
                        " è stata accettata",
                "INFO"
        );
    }

    //METODI DI SUPPORTO
    private boolean isRoomFullyAvailable(Room room, ReservationRequest request) {
        return room.getTables().stream()
                .allMatch(t -> isTableFreeForRoom(t, request.getDate(), request.getSlot()));
    }

    private boolean isTableFreeForRoom(Tables tables, LocalDate date, Slot slot) {
        Boolean freeDate = reservationRepository.findCollision(tables.getId(), date, slot).isEmpty();
        return freeDate;
    }

    private boolean isTableFree(Tables tables, LocalDate date, Slot slot, int seats) {
        Boolean freeSeats = tables.getCapacity()>=seats;
        Boolean freeDate = reservationRepository.findCollision(tables.getId(), date, slot).isEmpty();
        return freeDate && freeSeats;
    }



    private UUID getCurrentUserID(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if(authentication == null || !authentication.isAuthenticated()){
            throw new RuntimeException("Utente non Autenticato");
        }

        String userId = authentication.getName();

        return UUID.fromString(userId);
    }

    @Transactional
    public void rejectMessageByEventId(UUID eventID) {
        messageRepository.findByEventId(eventID).ifPresentOrElse(message -> {
            message.setState(State.REJECTED);
            messageRepository.save(message);
            spaceEventPublisher.publishLog(
                    "MESSAGGIO_REJECTED",
                    "L'evento con id " + eventID +
                          " è stato cancellato e il messaggio " + message.getEventId() +
                            " è stato segnato come REJECTED",
                    "WARN"
            );
        }, () -> {
            spaceEventPublisher.publishLog(
                    "MESSAGGIO_REJECTED_ERROR",
                    "Errore imprevisto: messaggio legato all'evento " +
                    eventID + " non trovato",
                    "ERROR"
            );
        });

    }

    public ReservationResponse generateReservationResponse(Reservation res){
        ReservationResponse response = new ReservationResponse();

        // Dati della Stanza
        response.setRoomId(res.getTable().getRoom().getId());
        response.setRoomName(res.getTable().getRoom().getName());
        response.setRoomCapacity(res.getTable().getRoom().getCapacity());

        // Dati della Prenotazione
        response.setDate(res.getDate());
        response.setSlot(res.getSlot());
        response.setSeats(res.getSeats());
        response.setReservationId(res.getId());

        //evento
        response.setEventId(res.getEvent());

        // Dati del Tavolo
        TableResponse tr = new TableResponse(
                res.getTable().getId(),
                res.getTable().getNumber(),
                res.getTable().getCapacity()
        );
        response.setTable(tr);

        return response;
    }

    @Transactional
    public boolean rejectReservation(ReservationRequest request) {

        UUID eventId = request.getEventId();

        try{
            if (eventId != null) {
                spaceEventPublisher.rejectReservation("REJECTED", eventId);
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Transactional
    public void deleteRejectedMessages() {
        try {
            messageRepository.deleteByState(State.REJECTED);
            spaceEventPublisher.publishLog(
                    "DELETED_MESSAGES",
                    "Pulizia messaggi rifiutati da: " +
                            getCurrentUserID(),
                    "WARN"
            );
        } catch (Exception e) {
            throw new RuntimeException("Errore durante la cancellazione dei messaggi");
        }
    }
}
