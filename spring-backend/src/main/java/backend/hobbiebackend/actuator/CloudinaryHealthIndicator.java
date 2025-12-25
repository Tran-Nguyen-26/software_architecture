package backend.hobbiebackend.actuator;

import com.cloudinary.Cloudinary;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class CloudinaryHealthIndicator implements HealthIndicator {

    private final Cloudinary cloudinary;

    public CloudinaryHealthIndicator(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    @Override
    public Health health() {
        try {
            // Thử gọi API ping đến Cloudinary để kiểm tra kết nối
            Map result = cloudinary.api().ping(Map.of());
            return Health.up().withDetail("cloudinary", result).build();
        } catch (Exception e) {
            return Health.down().withDetail("error", e.getMessage()).build();
        }
    }
}