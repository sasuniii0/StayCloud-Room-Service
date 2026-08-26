package lk.ijse.hotel.roomservice.controller;

import lk.ijse.hotel.roomservice.domain.Room;
import lk.ijse.hotel.roomservice.repository.RoomRepository;
import lk.ijse.hotel.roomservice.service.ImageStorageService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/rooms")
public class RoomController {

    private final RoomRepository rooms;
    private final ImageStorageService images;

    public RoomController(RoomRepository rooms, ImageStorageService images) {
        this.rooms = rooms;
        this.images = images;
    }

    @GetMapping
    public List<Room> getAll() {
        return rooms.findAll();
    }

    @GetMapping("/{id}")
    public Room getOne(@PathVariable String id) {
        return rooms.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Room not found"));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Room create(@RequestBody Room room) {
        room.setId(null);
        if (room.getType() == null || room.getType().isBlank()) {
            room.setType("DOUBLE");
        }
        room.setAvailable(true);
        if (room.getImageUrls() == null) {
            room.setImageUrls(new ArrayList<>());
        }
        return rooms.save(room);
    }

    @PutMapping("/{id}")
    public Room update(@PathVariable String id, @RequestBody Room body) {
        Room room = getOne(id);
        room.setRoomNumber(body.getRoomNumber());
        room.setType(body.getType());
        room.setDescription(body.getDescription());
        room.setPricePerNight(body.getPricePerNight());
        room.setAvailable(body.isAvailable());
        return rooms.save(room);
    }

    @PatchMapping("/{id}/availability")
    public Room setAvailability(@PathVariable String id, @RequestBody Map<String, Boolean> body) {
        Room room = getOne(id);
        room.setAvailable(Boolean.TRUE.equals(body.get("available")));
        return rooms.save(room);
    }

    /** Upload room photo to GCS and store the object reference on the room. */
    @PostMapping("/{id}/images")
    public Room uploadImage(@PathVariable String id, @RequestParam("file") MultipartFile file) {
        Room room = getOne(id);
        try {
            String objectName = images.upload(file);
            if (room.getImageUrls() == null) {
                room.setImageUrls(new ArrayList<>());
            }
            // Keep a single primary photo for the UI.
            room.getImageUrls().clear();
            room.getImageUrls().add(objectName);
            return rooms.save(room);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Image upload failed: " + e.getMessage());
        }
    }

    /**
     * Stream the room photo through the API (bucket has public access prevention).
     * Frontend should use this URL for &lt;img src&gt;.
     */
    @GetMapping("/{id}/photo")
    public ResponseEntity<byte[]> getPhoto(@PathVariable String id) {
        Room room = getOne(id);
        if (room.getImageUrls() == null || room.getImageUrls().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No photo for this room");
        }
        try {
            ImageStorageService.StoredImage image = images.read(room.getImageUrls().get(0));
            return ResponseEntity.ok()
                    .header(HttpHeaders.CACHE_CONTROL, "public, max-age=3600")
                    .contentType(MediaType.parseMediaType(
                            image.contentType() != null ? image.contentType() : "image/jpeg"))
                    .body(image.bytes());
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Photo not found: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String id) {
        if (!rooms.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Room not found");
        }
        rooms.deleteById(id);
    }
}
