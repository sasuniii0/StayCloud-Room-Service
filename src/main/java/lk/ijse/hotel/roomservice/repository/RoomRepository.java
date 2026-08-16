package lk.ijse.hotel.roomservice.repository;

import lk.ijse.hotel.roomservice.domain.Room;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface RoomRepository extends MongoRepository<Room, String> {

    List<Room> findByAvailableTrue();

    List<Room> findByType(Room.RoomType type);
}
