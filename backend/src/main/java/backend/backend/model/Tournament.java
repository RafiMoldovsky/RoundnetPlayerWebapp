package backend.backend.model;

import jakarta.persistence.*;
import lombok.Data;


@Entity
@Data
@Table(name = "tournaments")
public class Tournament {
    public Tournament(){}

    @Id
    Long id;

    private String tournament_name;
    private String url;
    private String date;

    @Override
    public String toString() {
        return "Tournament{" +
                "tournament_name='" + tournament_name + 
                ", url='" + url + 
                ", date='" + date + 
                '}';
    }
}
