package backend.backend.model;

import jakarta.persistence.*;
import lombok.Data;


@Entity
@Data
@Table(name = "players")
public class Player {
    public Player(){}

    @Id
    @Column(name = "player_name")
    private String player_name;

    @Column(columnDefinition = "jsonb")
    private String greatest_point_diff_game_win;

    @Column(columnDefinition = "jsonb")
    private String greatest_point_diff_game_loss;

    @Column(columnDefinition = "jsonb")
    private String past_tournaments;

    @Column(columnDefinition = "jsonb")
    private String longest_overtime_game;

    private int games_won;
    private int games_lost;
    private int series_won;
    private int series_lost;
    private int points_won;
    private int points_lost;

    @Column(columnDefinition = "jsonb")
    private String closest_series;

    private Integer points;

    // Constructors, getters, setters, and other methods

    // Getters and setters...

    @Override
    public String toString() {
        return "Player{" +
                "player_name='" + player_name + 
                ", greatest_point_diff_game_win='" + greatest_point_diff_game_win + 
                ", greatest_point_diff_game_loss='" + greatest_point_diff_game_loss + 
                ", past_tournaments='" + past_tournaments +
                ", longest_overtime_game='" + longest_overtime_game +
                ", games_won=" + games_won +
                ", games_lost=" + games_lost +
                ", series_won=" + series_won +
                ", series_lost=" + series_lost +
                ", points_won=" + points_won +
                ", points_lost=" + points_lost +
                ", closest_series='" + closest_series  +
                '}';
    }
}
