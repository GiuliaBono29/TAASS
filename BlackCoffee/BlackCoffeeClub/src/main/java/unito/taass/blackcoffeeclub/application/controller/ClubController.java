package unito.taass.blackcoffeeclub.application.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import unito.taass.blackcoffeeclub.domain.dto.ClubRequest;
import unito.taass.blackcoffeeclub.domain.model.Club;
import unito.taass.blackcoffeeclub.domain.service.ClubService;

import java.util.List;
import java.util.UUID;


@RestController
@RequestMapping("/api/club")
public class ClubController {

    public final ClubService clubService;


    public ClubController(ClubService clubService) {
        this.clubService = clubService;
    }

    @PreAuthorize("hasRole('ORGANIZZATORE')")
    @PostMapping("/create")
    public ResponseEntity<Club> createClub(@RequestBody ClubRequest clubRequest){
        Club created = clubService.createClub(clubRequest);

        return ResponseEntity.ok(created);
    }

    @GetMapping("/all")
    public List<Club> getAllClubs() {
        return clubService.getAllClubs();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getClubByID(@PathVariable("id") UUID id){
        Club club = clubService.getClubById(id);

        if (club == null){
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(club);
    }

    @PreAuthorize("hasRole('ORGANIZZATORE')")
    @DeleteMapping("/delete/{clubId}")
    public ResponseEntity<?> deleteClub(@PathVariable("clubId") UUID clubId) {
        boolean deleted = clubService.deleteClub(clubId);
        if(deleted) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PreAuthorize("hasRole('ORGANIZZATORE')")
    @PutMapping("/modify/{clubId}")
    public ResponseEntity<?> modifyClub(@PathVariable("clubId") UUID clubId, @RequestBody ClubRequest clubRequest) {
        Club modifiedclub = clubService.modifyClub(clubId, clubRequest);
        return ResponseEntity.ok(modifiedclub);
    }

    @PreAuthorize("hasRole('CLIENTE')")
    @PutMapping("/sub/{action}/{clubId}")
    public ResponseEntity<?> subClub(Authentication authentication, @PathVariable("action") String action, @PathVariable("clubId") UUID clubId) {

        UUID userId = UUID.fromString(authentication.getName());

        try{
            Club updated = clubService.subClub(clubId, userId, action);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/is-member/{clubId}/{userId}")
    public ResponseEntity<Boolean> isMember(@PathVariable UUID clubId, @PathVariable UUID userId){
        return ResponseEntity.ok(clubService.isUserSubscribe(clubId, userId));
    }

}
