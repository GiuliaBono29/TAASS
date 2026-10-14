package unito.taass.blackcoffeespace.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import unito.taass.blackcoffeespace.domain.model.Reservation;
import unito.taass.blackcoffeespace.domain.model.Slot;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, UUID> {

    @Query("SELECT r FROM Reservation r WHERE r.table.id = :tId AND r.date = :d AND (r.slot = :s OR r.slot = 'GIORNATA' OR :s = 'GIORNATA')")
    List<Reservation> findCollision(UUID tId, LocalDate d, Slot s);

    List<Reservation> findByOwner(UUID owner);
}
