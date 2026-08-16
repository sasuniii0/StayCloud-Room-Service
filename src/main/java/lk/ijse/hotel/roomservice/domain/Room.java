package lk.ijse.hotel.roomservice.domain;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "rooms")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Room {

    @Id
    private String id;

    @NotBlank
    private String roomNumber;

    @NotNull
    private RoomType type;

    @NotBlank
    private String description;

    @NotNull
    @Positive
    private BigDecimal pricePerNight;

    @Builder.Default
    private List<String> amenities = new ArrayList<>();

    @Builder.Default
    private List<String> imageUrls = new ArrayList<>();

    @Builder.Default
    private boolean available = true;

    public enum RoomType {
        SINGLE, DOUBLE, DELUXE, SUITE, FAMILY
    }
}
