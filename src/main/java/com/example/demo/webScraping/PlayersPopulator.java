package com.example.demo.webScraping;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;
import java.util.Arrays;

import com.google.gson.Gson;


public class PlayersPopulator {
    public static void main(String[] args) {
        String jdbcUrl = "exampleURL";
        String username = "exampleUsername";
        String password = "examplePass";
        long programStartTime = System.currentTimeMillis();
        try (Connection connection = DriverManager.getConnection(jdbcUrl, username, password)) {
            populatePlayers(connection);
            System.out.print("Players table populated successfully in ");
            long programEndTime = System.currentTimeMillis(); // Capture end time
            long programElapsedTime = programEndTime - programStartTime; // Calculate elapsed time
            System.out.println(programElapsedTime + " milliseconds");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void populatePlayers(Connection connection) throws SQLException {
        String selectTeamsQuery = "SELECT DISTINCT team_name, player_1, player_2, tournament FROM teams";
        try (PreparedStatement selectTeamsStmt = connection.prepareStatement(selectTeamsQuery);
             ResultSet teamsResultSet = selectTeamsStmt.executeQuery()) {

                String insertPlayerQuery = "INSERT INTO players (player_name, tournaments, most_recent_tournament, " +
                "best_tournament, partners, most_frequent_partner, most_frequent_team, " +
                "overall_game_record, overall_series_record, overall_point_diff, " +
                "greatest_point_diff_game_win, greatest_point_diff_game_loss, teams_played_on) " +
                "VALUES (?, ?::text[], ?::json, ?::json, ?::text[], ?, ?, ?, ?, ?, ?::json, ?::json, ?::text[])";

            String checkPlayerQuery = "SELECT player_name FROM players WHERE player_name = ?";

            try (PreparedStatement insertPlayerStmt = connection.prepareStatement(insertPlayerQuery);
                 PreparedStatement checkPlayerStmt = connection.prepareStatement(checkPlayerQuery)) {
                int i=0;
                while (teamsResultSet.next()) {
                    i++;
                    if(i%100==0){
                        System.out.println("Finished " + i + " teams");
                    }
                    String teamName = teamsResultSet.getString("team_name");
                    String player1 = teamsResultSet.getString("player_1");
                    String player2 = teamsResultSet.getString("player_2");
                    String tournament = teamsResultSet.getString("tournament");

                    String player = (player1 != null) ? player1 : player2;

                    // Check if player already exists in players table
                    checkPlayerStmt.setString(1, player);
                    ResultSet playerCheckResultSet = checkPlayerStmt.executeQuery();
                    if (!playerCheckResultSet.next()) {
                        // Player does not exist, insert a new row
                        insertPlayerStmt.setString(1, player);
                        insertPlayerStmt.setString(2, "{" + tournament + "}"); // Tournaments as text array
                        insertPlayerStmt.setString(3, null);
                        insertPlayerStmt.setString(4, null);
                        insertPlayerStmt.setString(5, "{\"" + (player1 != null ? player2 : player1) + "\"}"); // Partners as JSON array
                        insertPlayerStmt.setString(6, (null));
                        insertPlayerStmt.setString(7, (null));
                        insertPlayerStmt.setString(8, null); // Placeholder for overall_game_record
                        insertPlayerStmt.setString(9, null); // Placeholder for overall_series_record
                        insertPlayerStmt.setString(10, null); // Placeholder for overall_point_diff
                        insertPlayerStmt.setString(11, null); // Placeholder for greatest_point_diff_game_win
                        insertPlayerStmt.setString(12, null); // Placeholder for greatest_point_diff_game_loss
                        insertPlayerStmt.setString(13, "{\"" + teamName + "\"}"); // Teams played on as JSON array
                       // System.out.println(insertPlayerStmt);
                        insertPlayerStmt.executeUpdate();
                    }
                    else {
                        // Player already exists, update partners and tournaments
                        String existingPartnersJson = playerCheckResultSet.getString("partners");
                        String updatePlayerStmt = "UPDATE players SET partners = ?, tournaments = ? WHERE player_name = ?";
                        Set<String> existingPartners = new HashSet<>();
                        if (existingPartnersJson != null) {
                            Gson gson = new Gson();
                            String[] partnersArray = gson.fromJson(existingPartnersJson, String[].class);
                            existingPartners.addAll(Arrays.asList(partnersArray));
                        }

                        existingPartners.add(player1 != null ? player2 : player1);
                        
                        String existingTournamentsJson = playerCheckResultSet.getString("tournaments");
                        Set<String> existingTournaments = new HashSet<>();
                        if (existingTournamentsJson != null) {
                            Gson gson = new Gson();
                            String[] tournamentsArray = gson.fromJson(existingTournamentsJson, String[].class);
                            existingTournaments.addAll(Arrays.asList(tournamentsArray));
                        }

                        existingTournaments.add(tournament);
                        
                        // Update the existing player row with the updated partners and tournaments
                        updatePlayerStmt.setString(1, gson.toJson(existingPartners)); // Partners as text array
                        updatePlayerStmt.setString(2, gson.toJson(existingTournaments)); // Tournaments as JSON array
                        updatePlayerStmt.setString(3, player);
                        updatePlayerStmt.executeUpdate();
                    }

                }
            }
        }
    }
}

