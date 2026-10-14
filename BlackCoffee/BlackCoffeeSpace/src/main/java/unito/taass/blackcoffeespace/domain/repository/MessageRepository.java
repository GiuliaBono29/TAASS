package unito.taass.blackcoffeespace.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import unito.taass.blackcoffeespace.domain.model.Message;
import unito.taass.blackcoffeespace.domain.model.State;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MessageRepository  extends JpaRepository<Message, UUID> {
    Optional<Message> findByEventId(UUID eventId);
    List<Message> findAllByState(State state);
    List<Message> findAll();

    void deleteByState(State state);
}
