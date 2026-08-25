package lk.ijse.hotel.roomservice.repository;

import lk.ijse.hotel.roomservice.domain.Room;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface RoomRepository extends MongoRepository<Room, String> {
}
