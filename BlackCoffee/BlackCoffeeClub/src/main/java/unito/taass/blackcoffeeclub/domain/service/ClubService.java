package unito.taass.blackcoffeeclub.domain.service;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import unito.taass.blackcoffeeclub.domain.dto.ClubRequest;
import unito.taass.blackcoffeeclub.domain.messagging.ClubEventPublisher;
import unito.taass.blackcoffeeclub.domain.model.*;
import unito.taass.blackcoffeeclub.domain.repository.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class ClubService {

    private final ClubRepository repository;
    private final ClubEventPublisher clubEventPublisher;

    public ClubService(ClubRepository clubRepository, ClubEventPublisher clubEventPublisher) {
        this.repository = clubRepository;
        this.clubEventPublisher = clubEventPublisher;
    }

    public Club createClub(ClubRequest clubRequest) {
        UUID currentUserId = getCurrentUserID();

        Club club = Club.builder()
                .nomeclub(clubRequest.nameClub)
                .managerid(currentUserId)
                .description(clubRequest.description)
                .build();

        Club savedClub = repository.save(club);

        clubEventPublisher.publishLog(
                "CREAZIONE_CLUB",
                "Il club " + savedClub.getNomeclub() +
                        " con id " + savedClub.getId() +
                        " è stato creato con successo dall'utente " + currentUserId,
                "INFO"
        );

        return savedClub;
    }

    public List<Club> getAllClubs() {
        return repository.findAll();
    }

    public Club getClubById(UUID id) {
        return repository.findById(id).orElse(null);
    }

    public boolean deleteClub(UUID id) {
        UUID currentUserId = getCurrentUserID();
        Club club = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Club inesistente"));

        if(repository.existsById(id) && currentUserId.equals(club.getManagerid())){
            repository.deleteById(id);

            clubEventPublisher.publishLog(
                    "CANCELLAZIONE_CLUB",
                    "Il club " + club.getNomeclub() +
                            " con id " + club.getId() +
                            " è stato cancellato dall'utente " + currentUserId,
                       "WARN"
            );
            return true;
        }

        clubEventPublisher.publishLog(
                "CANCELLAZIONE_CLUB",
                "Il club " + club.getNomeclub() +
                        " con id " + club.getId() +
                        " non è stato cancellato correttamente",
                "ERROR"
        );

        return false;
    }

    public Club modifyClub(UUID clubId, ClubRequest clubRequest) {
        UUID currentUserId = getCurrentUserID();
        Club club = repository.findById(clubId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Club inesistente"));

        if (!currentUserId.equals(club.getManagerid())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Non hai i permessi di modifica per questo club");
        }

        club.modifyClub(
                clubRequest.nameClub,
                clubRequest.description
        );


        clubEventPublisher.publishLog(
                "MODIFICA_CLUB",
                "Il club con id " + clubId +
                        "  è stato modificato dall'utente " + currentUserId,
                "INFO"
        );

        return repository.save(club);
    }

    @Transactional
    public Club subClub(UUID clubId, UUID userId, String action){
        if (userId == null) throw new IllegalArgumentException("User ID mancante nel token");

        Club club = repository.findById(clubId)
                .orElseThrow(() -> new IllegalStateException("Evento inesistente"));

        if ("addUser".equals(action)) {
            club.iscrivi(userId);

            clubEventPublisher.publishLog(
                    "ISCRIZIONE_CLUB",
                    "L'utente con id " + userId +
                            " si è iscritto al club " + club.getNomeclub() +
                            " con id " + clubId,
                    "INFO"
            );
        } else if ("rmUser".equals(action)) {
            club.disiscrivi(userId);

            clubEventPublisher.publishLog(
                    "ISCRIZIONE_CLUB",
                    "L'utente con id " + userId +
                            " si è disiscritto dal club " + club.getNomeclub() +
                            " con id " + clubId,
                    "INFO"
            );
        }

        return repository.saveAndFlush(club);
    }

    public void notifySubscribers(UUID clubId, String eventName, String date){
        Club club = repository.findById(clubId)
                .orElseThrow(() -> new RuntimeException("Club con id: " + clubId + " non trovato"));
        List<UUID> subscribers = repository.findSubscriberIdsByClubId(clubId);

        for (UUID userId : subscribers) {
            // 2. Risolto: Chiamata al metodo che abbiamo definito prima nel Publisher
            // Assicurati che il metodo nel Publisher sia quello con (UUID, String, String)
            clubEventPublisher.publishNotification(
                    userId,
                    eventName,
                    "Il club " + club.getNomeclub() + " ha organizzato una serata per il giorno " + date
            );
        }
    }

    public boolean isUserSubscribe(UUID clubId, UUID userId){
        Club club = repository.findById(clubId)
                .orElseThrow(() -> new RuntimeException("Club non trovato"));
        return club.getIscritti().contains(userId);
    }

    private UUID getCurrentUserID() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if(authentication == null || !authentication.isAuthenticated()){
            throw new RuntimeException("Utente non autenticato");
        }

        String userId = authentication.getName();
        return UUID.fromString(userId);
    }
}

