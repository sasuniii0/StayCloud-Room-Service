package lk.ijse.hotel.roomservice.config;

import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StorageConfig {

    @Bean
    public Storage storage(GcsProperties properties) {
        StorageOptions.Builder builder = StorageOptions.newBuilder();
        if (properties.getProjectId() != null && !properties.getProjectId().isBlank()) {
            builder.setProjectId(properties.getProjectId());
        }
        return builder.build().getService();
    }
}
