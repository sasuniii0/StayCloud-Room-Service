package lk.ijse.hotel.roomservice.service;

import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import lk.ijse.hotel.roomservice.config.GcsProperties;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.UUID;

@Service
public class ImageStorageService {

    private final Storage storage;
    private final GcsProperties properties;

    public ImageStorageService(Storage storage, GcsProperties properties) {
        this.storage = storage;
        this.properties = properties;
    }

    public String upload(String roomId, MultipartFile file) {
        String objectName = "rooms/%s/%s-%s".formatted(
                roomId, UUID.randomUUID(), file.getOriginalFilename());

        BlobInfo blobInfo = BlobInfo.newBuilder(properties.getBucketName(), objectName)
                .setContentType(file.getContentType())
                .build();

        try {
            storage.create(blobInfo, file.getBytes());
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to read uploaded file", e);
        }

        return "https://storage.googleapis.com/%s/%s".formatted(properties.getBucketName(), objectName);
    }
}
