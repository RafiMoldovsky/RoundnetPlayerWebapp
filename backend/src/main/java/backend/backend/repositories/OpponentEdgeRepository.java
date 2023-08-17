package backend.backend.repositories;

import backend.backend.model.OpponentEdge;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OpponentEdgeRepository extends JpaRepository<OpponentEdge, Long> {
    @Query("SELECT o FROM OpponentEdge o WHERE o.node1 = :id OR o.node2 = :id")
    List<OpponentEdge> getAllEdgesWithID(@Param("id") Long id);
}

