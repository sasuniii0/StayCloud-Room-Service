package lk.ijse.hotel.roomservice.config;

import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

@Configuration
@EnableConfigurationProperties(GcsProperties.class)
public class StorageConfig {

    /**
     * Uses Application Default Credentials, so on a GCP VM this picks up the
     * attached Service Account automatically (no key file needed). Locally,
     * set GOOGLE_APPLICATION_CREDENTIALS to a service account key file.
     */
    @Bean
    public Storage storage(GcsProperties properties) {
        StorageOptions.Builder builder = StorageOptions.newBuilder();
        if (StringUtils.hasText(properties.getProjectId())) {
            builder.setProjectId(properties.getProjectId());
        }
        return builder.build().getService();
    }
}
