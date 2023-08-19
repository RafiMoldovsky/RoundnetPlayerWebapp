package backend.backend.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;


@MappedSuperclass
public class GraphEdge {

    
    protected Long node1;
    protected Long node2;
    @Transient
    protected String player_1;
    @Transient
    protected String player_2;

    @JsonCreator
    public GraphEdge(
            @JsonProperty("node1") Long node1,
            @JsonProperty("node2") Long node2) {
        this.node1 = node1;
        this.node2 = node2;
    }

    public Long getNode1() {
        return node1;
    }

    public Long getNode2() {
        return node2;
    }

    public String getPlayer1() {
        return player_1;
    }

    public String getPlayer2() {
        return player_2;
    }

    public void setPlayer1(String player1){
        this.player_1 = player1;
    }

    public void setPlayer2(String player2){
        this.player_2 = player2;
    }

    @Override
    public String toString() {
        return "GraphEdge{" +
                "node1='" + node1 + '\'' +
                ", node2='" + node2 + '\'' +
                '}';
    }
}
