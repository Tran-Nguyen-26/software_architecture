package backend.hobbiebackend.acl;

import com.cloudinary.Cloudinary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class CloudinaryImageStorage implements ImageStorage {
    private final Cloudinary cloudinary;

    @Autowired
    public CloudinaryImageStorage(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    @Override
    public void deleteResources(List<String> ids, boolean invalidate) throws Exception {
        cloudinary.api().deleteResources(ids, Map.of("invalidate", invalidate));
    }
}