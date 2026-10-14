package unito.taass.blackcoffeeevent.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import unito.taass.blackcoffeeevent.domain.model.Event;

import java.util.*;

@Repository
public interface EventRepository extends JpaRepository<Event, UUID> {

    List<Event> findByManagerId(UUID managerId);

    //List<Event> findFutureEvent();
}
