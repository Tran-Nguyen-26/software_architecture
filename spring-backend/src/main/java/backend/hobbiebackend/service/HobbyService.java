package backend.hobbiebackend.service;

import backend.hobbiebackend.model.dto.HobbyInfoDto;
import backend.hobbiebackend.model.dto.HobbyInfoUpdateDto;
import backend.hobbiebackend.model.entities.AppClient;
import backend.hobbiebackend.model.entities.Hobby;
import backend.hobbiebackend.model.dto.HobbyViewDto;
import java.io.IOException;
import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;

public interface HobbyService {
    Hobby findHobbieById(Long id);

    void saveUpdatedHobby(Hobby hobby) throws Exception;

    boolean deleteHobby(long id) throws Exception;

    Set<Hobby> findHobbyMatches(String username);

    boolean saveHobbyForClient(Hobby hobby, String username);

    boolean removeHobbyForClient(Hobby hobby, String username);

    boolean isHobbySaved(Long hobbyId, String username);

    public Page<HobbyViewDto> findSavedHobbies(AppClient currentAppClient, int page, int size);

    Set<Hobby> getAllHobbiesForBusiness(String username);
    
    Set<Hobby> getAllHobbieMatchesForClient(String username);

    void createHobby(Hobby offer);

    void saveHobby(HobbyInfoDto info);

    Hobby updateHobby(HobbyInfoUpdateDto info);
}
