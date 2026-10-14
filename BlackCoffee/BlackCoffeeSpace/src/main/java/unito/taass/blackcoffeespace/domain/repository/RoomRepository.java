package unito.taass.blackcoffeespace.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import unito.taass.blackcoffeespace.domain.model.Room;

import java.util.*;

@Repository
public interface RoomRepository extends JpaRepository<Room, UUID> {

}
