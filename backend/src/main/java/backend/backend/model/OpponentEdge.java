package backend.backend.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;

@Entity
@Table(name = "opponent_edges")
public class OpponentEdge extends GraphEdge {

    @Id
    private Long id;
    @Column(columnDefinition = "jsonb")
    private String game;

    public OpponentEdge(){
        super(null, null);
    }

    public OpponentEdge(Long node1, Long node2, String game) {
        super(node1, node2);
        this.game = game;
    }

    public String getGame() {
        return game;
    }

    @Override
    public String toString() {
        return "OpponentEdge{" +
                "node1='" + getNode1() + '\'' +
                ", node2='" + getNode2() + '\'' +
                ", game='" + game + '\'' +
                '}';
    }
}
