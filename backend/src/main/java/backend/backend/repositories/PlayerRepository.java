package backend.backend;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PlayerRepository extends JpaRepository<Player, String> {
    

    // Custom query using @Query
    @Query("SELECT p FROM Player p WHERE p.player_name = :playerName")
    Player findPlayerByName(@Param("playerName") String playerName);
}
