package unito.taass.blackcoffeeclub.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import unito.taass.blackcoffeeclub.domain.model.Club;

import java.util.List;
import java.util.UUID;

@Repository
public interface ClubRepository extends JpaRepository<Club, UUID> {

    List<Club> findByManagerid(UUID managerid);

    @Query("SELECT i FROM Club c JOIN c.iscritti i WHERE c.id = :clubId")
    List<UUID> findSubscriberIdsByClubId(@Param("clubId") UUID clubId);
}
