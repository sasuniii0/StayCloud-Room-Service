package lk.ijse.hotel.roomservice.service;

import lk.ijse.hotel.roomservice.domain.Room;
import lk.ijse.hotel.roomservice.exception.ResourceNotFoundException;
import lk.ijse.hotel.roomservice.repository.RoomRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class RoomService {

    private final RoomRepository roomRepository;
    private final ImageStorageService imageStorageService;

    public RoomService(RoomRepository roomRepository, ImageStorageService imageStorageService) {
        this.roomRepository = roomRepository;
        this.imageStorageService = imageStorageService;
    }

    public List<Room> findAll() {
        return roomRepository.findAll();
    }

    public List<Room> findAvailable() {
        return roomRepository.findByAvailableTrue();
    }

    public Room findById(String id) {
        return roomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found: " + id));
    }

    public Room create(Room room) {
        room.setId(null);
        return roomRepository.save(room);
    }

    public Room update(String id, Room updated) {
        Room existing = findById(id);
        existing.setRoomNumber(updated.getRoomNumber());
        existing.setType(updated.getType());
        existing.setDescription(updated.getDescription());
        existing.setPricePerNight(updated.getPricePerNight());
        existing.setAmenities(updated.getAmenities());
        existing.setAvailable(updated.isAvailable());
        return roomRepository.save(existing);
    }

    public void delete(String id) {
        Room existing = findById(id);
        roomRepository.delete(existing);
    }

    public Room addImage(String id, MultipartFile file) {
        Room room = findById(id);
        String url = imageStorageService.upload(id, file);
        room.getImageUrls().add(url);
        return roomRepository.save(room);
    }

    public void setAvailability(String id, boolean available) {
        Room room = findById(id);
        room.setAvailable(available);
        roomRepository.save(room);
    }
}
