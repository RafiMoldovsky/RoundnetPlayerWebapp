package backend.backend.repositories;

import backend.backend.model.TeammateEdge;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TeammateEdgeRepository extends JpaRepository<TeammateEdge, Long> {
    @Query("SELECT e FROM TeammateEdge e WHERE e.node1 = :id OR e.node2 = :id")
    List<TeammateEdge> getAllEdgesWithID(@Param("id") Long id);
}
