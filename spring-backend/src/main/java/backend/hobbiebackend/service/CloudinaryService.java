package backend.hobbiebackend.service;
import backend.hobbiebackend.model.entities.Hobby;

public interface CloudinaryService {
    void deleteHobbyImages(Hobby hobby);
}
