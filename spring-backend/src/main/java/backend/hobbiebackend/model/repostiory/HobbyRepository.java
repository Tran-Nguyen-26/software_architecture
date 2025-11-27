package backend.hobbiebackend.model.repostiory;

import backend.hobbiebackend.model.entities.Hobby;
import backend.hobbiebackend.model.entities.Location;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface HobbyRepository extends JpaRepository<Hobby, Long> {
    Set<Hobby> findAllByCreator(String creator);

    List<Hobby> findAllByLocation(Location location);

    @Query("""
        SELECT h 
        FROM AppClient c 
        JOIN c.saved_hobbies h 
        WHERE c.id = :userId
        """)
    Page<Hobby> findSavedHobbiesByUser(@Param("userId") Long userId, Pageable pageable);

}
