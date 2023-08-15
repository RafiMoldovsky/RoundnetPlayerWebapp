package com.example.demo.webScraping;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;
import java.util.ArrayList;
import com.google.gson.JsonObject;
import java.sql.Array;


import com.google.gson.Gson;


public class PlayersPopulator {
    public static void main(String[] args) {
        String jdbcUrl = System.getProperty("spring.datasource.url");
        String username = System.getProperty("spring.datasource.username");
        String password = System.getProperty("spring.datasource.password");
        long programStartTime = System.currentTimeMillis();
        try (Connection connection = DriverManager.getConnection(jdbcUrl, username, password)) {
           // populatePlayers(connection);
           // populatePlayersFromGames(connection);
           populatePlayersFromSeries(connection);
            System.out.print("Players table populated successfully in ");
            long programEndTime = System.currentTimeMillis(); // Capture end time
            long programElapsedTime = programEndTime - programStartTime; // Calculate elapsed time
            System.out.println(programElapsedTime + " milliseconds");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void populatePlayers(Connection connection) throws SQLException {
        String selectRecordsQuery = "SELECT * FROM records";
        ArrayList<String> badQueries = new ArrayList<>();
        String mostRecentBadQuery = "";
        try (PreparedStatement selectRecordsStmt = connection.prepareStatement(selectRecordsQuery);
             ResultSet recordsResultSet = selectRecordsStmt.executeQuery()) {

            String insertPlayerQuery = "INSERT INTO players (player_name, past_tournaments) " +
                    "VALUES (?, array[?]::json[])";

            String checkPlayerQuery = "SELECT * FROM players WHERE player_name = ?";

            try (PreparedStatement insertPlayerStmt = connection.prepareStatement(insertPlayerQuery);
                 PreparedStatement checkPlayerStmt = connection.prepareStatement(checkPlayerQuery)) {
                int i = 0;
                while (recordsResultSet.next()) {
                    try{
                        i++;
                        if (i % 100 == 0) {
                            System.out.println("Processed " + i + " records");
                        }
                        mostRecentBadQuery = recordsResultSet.getString("player_1") + " or " + recordsResultSet.getString("player_2");
                        String player1 = recordsResultSet.getString("player_1");
                        String player2 = recordsResultSet.getString("player_2");
                        String tournament = recordsResultSet.getString("tournament");
                        String tournamentDate = recordsResultSet.getString("tournament_date");
                        String teamName = recordsResultSet.getString("team_name");
                        int wins = recordsResultSet.getInt("wins");
                        int losses = recordsResultSet.getInt("losses");
                        String result = recordsResultSet.getString("result");
                        String division = recordsResultSet.getString("division");

                        Set<String> players = new HashSet<>();
                        if (player1 != null) {
                            players.add(player1);
                        }
                        if (player2 != null) {
                            players.add(player2);
                        }

                        for (String player : players) {
                            // Check if player already exists in players table
                            checkPlayerStmt.setString(1, player);
                            ResultSet playerCheckResultSet = checkPlayerStmt.executeQuery();
                            if (!playerCheckResultSet.next()) {
                                // Player does not exist, insert a new row
                                insertPlayerStmt.setString(1, player);
                                String json = "{\"tournament_name\":\"" + tournament + "\",\"tournament_date\":\"" +
                                        tournamentDate + "\",\"team_name\":\"" + teamName + "\",\"partner_name\":\"" +
                                        (player.equals(player1) ? player2 : player1) + "\",\"wins\":" + wins +
                                        ",\"losses\":" + losses + ",\"result\":\"" + result + "\",\"division\":\"" +
                                        division + "\"}";
                                insertPlayerStmt.setString(2, json); // Past tournaments as JSON array
                                insertPlayerStmt.executeUpdate();
                            } else {
                                // Player already exists, update past_tournaments
                                Set<JsonObject> existingPastTournaments = new HashSet<>();
                                Gson gson = new Gson();;
                                JsonObject newTournament = new JsonObject();
                                newTournament.addProperty("tournament_name", tournament);
                                newTournament.addProperty("tournament_date", tournamentDate);
                                newTournament.addProperty("team_name", teamName);
                                newTournament.addProperty("partner_name", (player.equals(player1) ? player2 : player1));
                                newTournament.addProperty("wins", wins);
                                newTournament.addProperty("losses", losses);
                                newTournament.addProperty("result", result);
                                newTournament.addProperty("division", division);

                                existingPastTournaments.add(newTournament);

                                // Update the existing player row with the updated past_tournaments
                                String updatePlayerQuery = "UPDATE players SET past_tournaments = array[?]::json[] WHERE player_name = ?";
                                PreparedStatement updatePlayerStmt = connection.prepareStatement(updatePlayerQuery);
                                updatePlayerStmt.setString(1, gson.toJson(existingPastTournaments)); // Past tournaments as JSON array
                                updatePlayerStmt.setString(2, player);
                                updatePlayerStmt.executeUpdate();
                            }
                        }
                    } catch(Exception e){
                        badQueries.add(mostRecentBadQuery);
                     }    
                }
            }    
        }
    }
    private static void populatePlayersFromGames(Connection connection) throws SQLException {
        String selectPlayersQuery = "SELECT DISTINCT player_name FROM players";
        String selectGamesQuery = "SELECT * FROM games WHERE players_1 LIKE ? OR players_2 LIKE ?";
        String updatePlayerQuery = "UPDATE players SET points_won = ?, points_lost = ?, games_won = ?, " +
                                   "games_lost = ?, greatest_point_diff_game_win = ?::json, greatest_point_diff_game_loss = ?::json, " +
                                   "longest_overtime_game = ?::json " +
                                   "WHERE player_name = ?";
    
        try (PreparedStatement selectPlayersStmt = connection.prepareStatement(selectPlayersQuery);
             PreparedStatement selectGamesStmt = connection.prepareStatement(selectGamesQuery);
             PreparedStatement updatePlayerStmt = connection.prepareStatement(updatePlayerQuery)) {
    
            ResultSet playersResultSet = selectPlayersStmt.executeQuery();
            int i=0;
            while (playersResultSet.next()) {
                i++;
                if (i % 100 == 0) {
                    System.out.println("Processed " + i + " players");
                }
                String player = playersResultSet.getString("player_name");
    
                selectGamesStmt.setString(1, "%" + player + "%");
                selectGamesStmt.setString(2, "%" + player + "%");
                ResultSet gamesResultSet = selectGamesStmt.executeQuery();
    
                int pointsFor = 0;
                int pointsAgainst = 0;
                int gameWins = 0;
                int gameLosses = 0;
                int greatestPointWin = Integer.MIN_VALUE;
                int greatestPointLoss = Integer.MAX_VALUE;
                int largestTotalScore = Integer.MIN_VALUE;
                JsonObject longestOvertimeGameObj = new JsonObject();
                JsonObject greatestPointWinGameObj = new JsonObject();
                JsonObject greatestPointLossGameObj = new JsonObject();
    
                while (gamesResultSet.next()) {
                    String team1 = gamesResultSet.getString("team_1");
                    String team2 = gamesResultSet.getString("team_2");
                    String players1String = gamesResultSet.getString("players_1");
                    String players2String = gamesResultSet.getString("players_2");
                    int score1 = gamesResultSet.getInt("score_1");
                    int score2 = gamesResultSet.getInt("score_2");
                    String tournamentStage = gamesResultSet.getString("tournament_stage");
                    String tournamentName = gamesResultSet.getString("tournament_name");
                    String division = gamesResultSet.getString("division");
                    System.out.println(player + " : " + tournamentName + " : " + tournamentStage);

                    String[] players1 = players1String.split(",");
                    String[] players2 = players2String.split(",");
                    boolean playerInTeam1 = players1String.contains(player);
                    boolean playerInTeam2 = players2String.contains(player);

                    if (playerInTeam1 || playerInTeam2) {
                        JsonObject gameObj = new JsonObject();
                        gameObj.addProperty("team_1", team1);
                        gameObj.addProperty("team_2", team2);
                        gameObj.addProperty("players_1", new Gson().toJson(players1));
                        gameObj.addProperty("players_2", new Gson().toJson(players2));
                        gameObj.addProperty("score_1", score1);
                        gameObj.addProperty("score_2", score2);
                        gameObj.addProperty("tournament_stage", tournamentStage);
                        gameObj.addProperty("tournament_name", tournamentName);
                        gameObj.addProperty("division", division);
                        
                        int gamePointDiff = playerInTeam1 ? (score1 - score2) : (score2 - score1);
                        int totalScore = score1 + score2;

                        pointsFor += playerInTeam1 ? score1 : score2;
                        pointsAgainst += playerInTeam1 ? score2 : score1;

                        if (gamePointDiff > 0) {
                            gameWins++;
                        } else {
                            gameLosses++;
                        }
                        if (gamePointDiff > greatestPointWin) {
                            greatestPointWin = gamePointDiff;
                            greatestPointWinGameObj = gameObj;
                        }

                        if (gamePointDiff < greatestPointLoss) {
                            greatestPointLoss = gamePointDiff;
                            greatestPointLossGameObj = gameObj;
                        }

                        if (Math.abs(score1 - score2) == 2) {
                            if (totalScore > largestTotalScore) {
                                largestTotalScore = totalScore;
                                longestOvertimeGameObj = gameObj;
                            }
                        }
                    }
                    else{
                        System.out.println("WTF");
                        System.out.println(player);
                        System.out.println(players1[0] + " " + players1[1]);
                        System.out.println(players2[0] + " " + players2[1]);
                    }
                }
    
                updatePlayerStmt.setInt(1, pointsFor);
                updatePlayerStmt.setInt(2, pointsAgainst);
                updatePlayerStmt.setInt(3, gameWins);
                updatePlayerStmt.setInt(4, gameLosses);
                updatePlayerStmt.setString(5, greatestPointWinGameObj.toString());
                updatePlayerStmt.setString(6, greatestPointLossGameObj.toString());
                updatePlayerStmt.setString(7, longestOvertimeGameObj.toString());
                updatePlayerStmt.setString(8, player);
            
                updatePlayerStmt.executeUpdate();
            }
        }
        catch(Exception e){
            System.out.println("Something's wrong");
        }
    }
    private static void populatePlayersFromSeries(Connection connection) throws SQLException {
        String selectPlayersQuery = "SELECT DISTINCT player_name FROM players";
        String selectGamesQuery = "SELECT * FROM series WHERE players_1 LIKE ? OR players_2 LIKE ?";
        String updatePlayerQuery = "UPDATE players SET series_won = ?, series_lost = ?, closest_series = ?::json " +
                                   "WHERE player_name = ?";
    
        try (PreparedStatement selectPlayersStmt = connection.prepareStatement(selectPlayersQuery);
             PreparedStatement selectGamesStmt = connection.prepareStatement(selectGamesQuery);
             PreparedStatement updatePlayerStmt = connection.prepareStatement(updatePlayerQuery)) {
    
            ResultSet playersResultSet = selectPlayersStmt.executeQuery();
            int i=0;
            while (playersResultSet.next()) {
                i++;
                if (i % 100 == 0) {
                    System.out.println("Processed " + i + " players");
                }
                String player = playersResultSet.getString("player_name");
    
                selectGamesStmt.setString(1, "%" + player + "%");
                selectGamesStmt.setString(2, "%" + player + "%");
                ResultSet seriesResultSet = selectGamesStmt.executeQuery();
    
                int seriesWins = 0;
                int seriesLosses = 0;
                int closestSeriesTotalScore = 0;
                JsonObject closestSeriesObj = new JsonObject();
                while (seriesResultSet.next()) {
                    try{
                        String team1 = seriesResultSet.getString("team_1");
                        String team2 = seriesResultSet.getString("team_2");
                        String players1String = seriesResultSet.getString("players_1");
                        String players2String = seriesResultSet.getString("players_2");
                        Array team1Scores = seriesResultSet.getArray("team_1_scores");
                        Array team2Scores = seriesResultSet.getArray("team_2_scores");
                        int[] team_1_scores = convertStringToIntArray(team1Scores.toString());
                        int[] team_2_scores = convertStringToIntArray(team2Scores.toString());
                        String tournamentName = seriesResultSet.getString("tournament");
                        String division = seriesResultSet.getString("division");
                        String round = seriesResultSet.getString("round");
                        System.out.println(player + " : " + tournamentName + " : " + round);

                        String[] players1 = players1String.split(",");
                        String[] players2 = players2String.split(",");
                        boolean playerInTeam1 = players1String.contains(player);
                        boolean playerInTeam2 = players2String.contains(player);


                        if (playerInTeam1 || playerInTeam2) {
                            JsonObject seriesObj = new JsonObject();
                            seriesObj.addProperty("team_1", team1);
                            seriesObj.addProperty("team_2", team2);
                            seriesObj.addProperty("players_1", new Gson().toJson(players1));
                            seriesObj.addProperty("players_2", new Gson().toJson(players2));
                            seriesObj.addProperty("team_1_scores", new Gson().toJson(team_1_scores));
                            seriesObj.addProperty("team_2_scores", new Gson().toJson(team_2_scores));
                            seriesObj.addProperty("round", round);
                            seriesObj.addProperty("tournament_name", tournamentName);
                            seriesObj.addProperty("division", division);
                            
                            int totalScore = 0;
                            int team1wins = 0;
                            int team2wins = 0;
                            for(int j=0; j<team_1_scores.length; j++){
                                totalScore += team_1_scores[j];
                                totalScore += team_2_scores[j];
                                if(team_1_scores[j] > team_2_scores[j]){
                                    team1wins++;
                                }
                                else{
                                    team2wins++;
                                }
                            }
                            if(playerInTeam1 && team1wins>team2wins){
                                seriesWins++;
                            }
                            else if(playerInTeam1){
                                seriesLosses++;
                            }
                            if(playerInTeam2 && team1wins<team2wins){
                                seriesWins++;
                            }
                            else if(playerInTeam2){
                                seriesLosses++;
                            }

                            if (totalScore > closestSeriesTotalScore) {
                                closestSeriesTotalScore = totalScore;
                                closestSeriesObj = seriesObj;
                            }
                        }
                        else{
                            System.out.println("WTF");
                            System.out.println(player);
                            System.out.println(players1[0] + " " + players1[1]);
                            System.out.println(players2[0] + " " + players2[1]);
                        }
                    } catch(Exception e){}
                }
    
                updatePlayerStmt.setInt(1, seriesWins);
                updatePlayerStmt.setInt(2, seriesLosses);
                updatePlayerStmt.setString(3, closestSeriesObj.toString());
                updatePlayerStmt.setString(4, player);
               // System.out.println(updatePlayerStmt);
                updatePlayerStmt.executeUpdate();
            }
        }
        catch(Exception e){
            System.out.println("Something's wrong");
            e.printStackTrace();
        }
    }
    private static int[] convertStringToIntArray(String input) {
        // Remove brackets and split the string by commas
        String[] stringValues = input.substring(1, input.length() - 1).split(", ");
        
        // Convert string values to integers and store in the int array
        int[] intArray = new int[stringValues.length];
        for (int i = 0; i < stringValues.length; i++) {
            intArray[i] = Integer.parseInt(stringValues[i]);
        }
        
        return intArray;
    }
}
