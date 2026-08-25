package lk.ijse.hotel.roomservice.service;

import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import lk.ijse.hotel.roomservice.config.GcsProperties;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
public class ImageStorageService {

    private final Storage storage;
    private final GcsProperties properties;

    public ImageStorageService(Storage storage, GcsProperties properties) {
        this.storage = storage;
        this.properties = properties;
    }

    public String upload(MultipartFile file) throws IOException {
        String objectName = "rooms/" + UUID.randomUUID() + "-" + file.getOriginalFilename();
        BlobInfo blobInfo = BlobInfo.newBuilder(properties.getBucketName(), objectName)
                .setContentType(file.getContentType())
                .build();
        storage.create(blobInfo, file.getBytes());
        return "https://storage.googleapis.com/" + properties.getBucketName() + "/" + objectName;
    }
}
