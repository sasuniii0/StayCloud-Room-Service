package lk.ijse.hotel.roomservice.controller;

import jakarta.validation.Valid;
import lk.ijse.hotel.roomservice.domain.Room;
import lk.ijse.hotel.roomservice.service.RoomService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/rooms")
public class RoomController {

    private final RoomService roomService;

    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    @GetMapping
    public List<Room> getAll(@RequestParam(required = false) Boolean available) {
        return Boolean.TRUE.equals(available) ? roomService.findAvailable() : roomService.findAll();
    }

    @GetMapping("/{id}")
    public Room getOne(@PathVariable String id) {
        return roomService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Room create(@Valid @RequestBody Room room) {
        return roomService.create(room);
    }

    @PutMapping("/{id}")
    public Room update(@PathVariable String id, @Valid @RequestBody Room room) {
        return roomService.update(id, room);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String id) {
        roomService.delete(id);
    }

    @PatchMapping("/{id}/availability")
    public ResponseEntity<Void> setAvailability(@PathVariable String id, @RequestBody Map<String, Boolean> body) {
        roomService.setAvailability(id, Boolean.TRUE.equals(body.get("available")));
        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "/{id}/images", consumes = "multipart/form-data")
    public Room uploadImage(@PathVariable String id, @RequestParam("file") MultipartFile file) {
        return roomService.addImage(id, file);
    }
}
