package backend.hobbiebackend.service.impl;

import backend.hobbiebackend.model.entities.Hobby;
import backend.hobbiebackend.service.CloudinaryService;
import com.cloudinary.Cloudinary;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class CloudinaryServiceImpl implements CloudinaryService {

    private final Cloudinary cloudinary;

    @Override
    @CircuitBreaker(name = "cloudinaryDelete", fallbackMethod = "deleteFallback")
    public void deleteHobbyImages(Hobby hobby) {
        List<String> publicIds = collectPublicIds(hobby);
        if (publicIds.isEmpty()) return;

        try {
            Map result = cloudinary.api().deleteResources(publicIds, Map.of("invalidate", true));
            System.out.println("Cloudinary delete result = " + result);
        } catch (Exception e) {
            throw new RuntimeException("Cloudinary deleteResources failed", e);
        }
    }

    private List<String> collectPublicIds(Hobby hobby) {
        List<String> ids = Arrays.asList(
                hobby.getProfileImg_id(),
                hobby.getGalleryImg1_id(),
                hobby.getGalleryImg2_id(),
                hobby.getGalleryImg3_id()
        );

        List<String> cleaned = new ArrayList<>();
        for (String id : ids) {
            if (id != null && !id.isBlank()) cleaned.add(id);
        }
        return cleaned;
    }

    protected void deleteFallback(Hobby hobby, Throwable ex) {
        System.err.println("[CloudinaryFallback] Could not delete images for hobbyId="
                + hobby.getId() + " reason=" + ex.getMessage());
    }
}
