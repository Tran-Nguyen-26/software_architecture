package backend.hobbiebackend.acl;

import java.util.List;

public interface ImageStorage {
    void deleteResources(List<String> ids, boolean invalidate) throws Exception;
}