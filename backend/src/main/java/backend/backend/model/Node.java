package backend.backend.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "nodes")
public class Node {

    @Id
    private Long id;

    private String player_name;

    public Node() {
    }

    public Node(String playerName) {
        this.player_name = playerName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPlayerName() {
        return player_name;
    }

    public void setPlayerName(String playerName) {
        this.player_name = playerName;
    }

    @Override
    public String toString() {
        return "Node{" +
                "id=" + id +
                ", playerName='" + player_name + '\'' +
                '}';
    }
}
