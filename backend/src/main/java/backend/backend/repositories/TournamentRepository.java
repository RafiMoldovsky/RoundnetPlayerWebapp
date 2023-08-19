package backend.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import backend.backend.model.Tournament;
import java.util.List;

@Repository
public interface TournamentRepository extends JpaRepository<Tournament, String> {
    

    // Custom query using @Query
    @Query("SELECT t FROM Tournament t")
    List<Tournament> getAllTournaments();
}
