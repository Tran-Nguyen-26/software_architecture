package backend.hobbiebackend.model.repostiory;

import backend.hobbiebackend.model.entities.AppClient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AppClientRepository extends JpaRepository<AppClient, Long> {
    Optional<AppClient> findByUsername(String username);

    @Query("""
        select distinct c
        from AppClient c
        join c.saved_hobbies h
        where h.id = :hobbyId
    """)
    List<AppClient> findAllClientsWhoSavedHobby(@Param("hobbyId") Long hobbyId);
}
