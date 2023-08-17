package backend.backend.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;


@Entity
@Table(name = "teammate_edges")
public class TeammateEdge extends GraphEdge {

    @Id
    private Long id;

    @Column(columnDefinition = "jsonb")
    private String tournament;

    public TeammateEdge(){
        super(null, null);
    }

    public TeammateEdge(Long node1, Long node2, String tournament) {
        super(node1, node2);
        this.tournament = tournament;
    }

    public String getTournament() {
        return tournament;
    }

    @Override
    public String toString() {
        return "TeammateEdge{" +
                "node1='" + getNode1() + '\'' +
                ", node2='" + getNode2() + '\'' +
                ", tournament='" + tournament + '\'' +
                '}';
    }
}
