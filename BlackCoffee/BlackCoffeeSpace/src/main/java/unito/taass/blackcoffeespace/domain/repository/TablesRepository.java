package unito.taass.blackcoffeespace.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import unito.taass.blackcoffeespace.domain.model.Slot;
import unito.taass.blackcoffeespace.domain.model.Tables;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface TablesRepository extends JpaRepository<Tables, UUID> {

    @Query("""
        SELECT DISTINCT t 
        FROM Tables t
        WHERE t.capacity >= :capacity
        AND NOT EXISTS (
            SELECT r FROM Reservation r
            WHERE r.table = t
            AND FUNCTION('DATE', r.date) = FUNCTION('DATE', :date)
            AND (
                (:slot <> 'GIORNATA' AND (r.slot = :slot OR r.slot = 'GIORNATA'))
                OR (:slot = 'GIORNATA')
            )
        )
""")
    List<Tables> findAvailableTables(@Param("date") LocalDate date, @Param("slot") Slot slot, @Param("capacity") int capacity);

}
