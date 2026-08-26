package lk.ijse.hotel.roomservice.service;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import lk.ijse.hotel.roomservice.config.GcsProperties;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.UUID;

@Service
public class ImageStorageService {

    public record StoredImage(byte[] bytes, String contentType) {}

    private final Storage storage;
    private final GcsProperties properties;

    public ImageStorageService(Storage storage, GcsProperties properties) {
        this.storage = storage;
        this.properties = properties;
    }

    /** Upload file to GCS and return the object name (not a public URL). */
    public String upload(MultipartFile file) throws IOException {
        String safeName = file.getOriginalFilename() == null ? "photo" : file.getOriginalFilename().replaceAll("[^a-zA-Z0-9._-]", "_");
        String objectName = "rooms/" + UUID.randomUUID() + "-" + safeName;
        BlobInfo blobInfo = BlobInfo.newBuilder(properties.getBucketName(), objectName)
                .setContentType(file.getContentType() != null ? file.getContentType() : "application/octet-stream")
                .build();
        storage.create(blobInfo, file.getBytes());
        return objectName;
    }

    public StoredImage read(String stored) {
        String objectName = toObjectName(stored);
        Blob blob = storage.get(BlobId.of(properties.getBucketName(), objectName));
        if (blob == null || !blob.exists()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Image object missing: " + objectName);
        }
        return new StoredImage(blob.getContent(), blob.getContentType());
    }

    private String toObjectName(String stored) {
        if (stored == null || stored.isBlank()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Empty image reference");
        }
        String prefix = "https://storage.googleapis.com/" + properties.getBucketName() + "/";
        if (stored.startsWith(prefix)) {
            return stored.substring(prefix.length());
        }
        // Already an object path like rooms/uuid-name.jpg
        return stored.startsWith("/") ? stored.substring(1) : stored;
    }
}
