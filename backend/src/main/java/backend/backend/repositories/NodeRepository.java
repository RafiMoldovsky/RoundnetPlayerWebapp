package backend.backend.repositories;

import backend.backend.model.Node;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface NodeRepository extends JpaRepository<Node, Long> {
    // Define custom query methods if needed
    @Query("SELECT id FROM Node n WHERE n.player_name = :player_name")
    Long getIdForNode(@Param("player_name") String player_name);

    @Query("SELECT player_name FROM Node n WHERE n.id = :id")
    String getPlayerNameForNode(@Param("id") Long id);

}
