package com.example.demo.webScraping;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class GraphPopulator {

    public static void main(String[] args) {
        String jdbcUrl = "";
        String username = "";
        String password = "-";
        long programStartTime = System.currentTimeMillis();
        try (Connection connection = DriverManager.getConnection(jdbcUrl, username, password)) {
            createNodesTable(connection);
            populateNodesTable(connection);
            createTeammateEdgesTable(connection);
            populateTeammateEdges(connection);
            createOpponentEdgesTable(connection);
            populateOpponentEdges(connection);
            long programEndTime = System.currentTimeMillis(); // Capture end time
            long programElapsedTime = programEndTime - programStartTime; // Calculate elapsed time
            System.out.println("program took " + programElapsedTime + " milliseconds");
            System.out.println("Database populated successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void createNodesTable(Connection connection) throws SQLException {
        String createNodesTableSQL = "CREATE TABLE IF NOT EXISTS nodes (id SERIAL PRIMARY KEY, player_name VARCHAR(255));";
        try (PreparedStatement statement = connection.prepareStatement(createNodesTableSQL)) {
            statement.execute();
        }
    }
    private static void populateNodesTable(Connection connection) throws SQLException {
        String selectPlayersQuery = "SELECT player_name FROM players;";
        try (PreparedStatement selectPlayersStmt = connection.prepareStatement(selectPlayersQuery);
             ResultSet playersResultSet = selectPlayersStmt.executeQuery()) {

            String insertNodeQuery = "INSERT INTO nodes (player_name) " +
                    "VALUES (?)";

            try (PreparedStatement insertNodeStmt = connection.prepareStatement(insertNodeQuery);) {
                int i = 0;
                while (playersResultSet.next()) {
                    try{
                        i++;
                        if (i % 100 == 0) {
                            System.out.println("Processed " + i + " player nodes");
                        }
                        String player = playersResultSet.getString("player_name");
                        insertNodeStmt.setString(1, player);
                        insertNodeStmt.executeUpdate();
                    } catch(Exception e){
                     }    
                }
            }    
        }
    }

    public static void createTeammateEdgesTable(Connection connection) {
        try {
            String createTeammateEdgesTableSQL = "CREATE TABLE IF NOT EXISTS teammate_edges (" +
                    "id SERIAL PRIMARY KEY," +
                    "node1 INT REFERENCES nodes(id)," +
                    "node2 INT REFERENCES nodes(id)," +
                    "tournament VARCHAR(255)" +
                    ");";

            try (PreparedStatement preparedStatement = connection.prepareStatement(createTeammateEdgesTableSQL)) {
                preparedStatement.executeUpdate();
                System.out.println("teammate_edges table created successfully");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void populateTeammateEdges(Connection connection) throws SQLException {
        // Iterate through teams and populate teammate_edges table
        String selectTeamsQuery = "SELECT * FROM teams;";
        try (PreparedStatement selectTeamsStmt = connection.prepareStatement(selectTeamsQuery);
        ResultSet teamsResultSet = selectTeamsStmt.executeQuery()){
             String insertTeammateEdgeSQL = "INSERT INTO teammate_edges (node1, node2, tournament) VALUES (?, ?, ?);";
            try (PreparedStatement statement = connection.prepareStatement(insertTeammateEdgeSQL)) {
                int i = 0;
                while (teamsResultSet.next()) {
                    i++;
                    if (i % 100 == 0) {
                        System.out.println("Processed " + i + " teams");
                    }
                    // Check if an edge already exists and insert if not
                    String player_1 = teamsResultSet.getString("player_1");
                    String player_2 = teamsResultSet.getString("player_2");
                    long id1 = getIDforPlayer(player_1, connection);
                    long id2 = getIDforPlayer(player_2, connection);
                    if(id1==-1 || id2 == -1){
                        continue;
                    }
                    String tournament = teamsResultSet.getString("tournament");
                    if (!teammateEdgeExists(connection, id1, id2)) {
                        statement.setLong(1, id1);
                        statement.setLong(2, id2);
                        statement.setString(3, tournament);
                        statement.executeUpdate();
                    }
                }
            }
        }
    }
    public static long getIDforPlayer(String player_name, Connection connection) throws SQLException {
        String getPlayerIdQuery = "SELECT id FROM nodes WHERE player_name = ?";
        try (PreparedStatement getPlayerIdStmt = connection.prepareStatement(getPlayerIdQuery)) {
            getPlayerIdStmt.setString(1, player_name);
            try (ResultSet playerIdResultSet = getPlayerIdStmt.executeQuery()) {
                if (playerIdResultSet.next()) {
                    return playerIdResultSet.getLong("id");
                }
            }
        }
        return -1; // Return -1 if player not found
    }

    private static boolean teammateEdgeExists(Connection connection, long id1, long id2) throws SQLException {
        // Check if an edge between player1 and player2 already exists in teammate_edges
        String querySQL = "SELECT * FROM teammate_edges WHERE (node1 = ? AND node2 = ?) OR (node1 = ? AND node2 = ?);";
        try (PreparedStatement statement = connection.prepareStatement(querySQL)) {
            statement.setLong(1, id1);
            statement.setLong(2, id2);
            statement.setLong(3, id2);
            statement.setLong(4, id1);
            return statement.executeQuery().next();
        }
    }


    public static void createOpponentEdgesTable(Connection connection) {
        try {
            String createOpponentEdgesTableSQL = "CREATE TABLE IF NOT EXISTS opponent_edges (" +
                    "id SERIAL PRIMARY KEY," +
                    "node1 INT REFERENCES nodes(id)," +
                    "node2 INT REFERENCES nodes(id)," +
                    "game JSON" +
                    ");";

            try (PreparedStatement preparedStatement = connection.prepareStatement(createOpponentEdgesTableSQL)) {
                preparedStatement.executeUpdate();
                System.out.println("opponent_edges table created successfully");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private static void populateOpponentEdges(Connection connection) throws SQLException {
        // Iterate through games and populate opponent_edges table
        String selectGamesQuery = "SELECT * FROM games;";
        try (PreparedStatement selectGamesStmt = connection.prepareStatement(selectGamesQuery);
             ResultSet gamesResultSet = selectGamesStmt.executeQuery()) {
    
            String insertOpponentEdgeSQL = "INSERT INTO opponent_edges (node1, node2, game) VALUES (?, ?, ?);";
            try (PreparedStatement statement = connection.prepareStatement(insertOpponentEdgeSQL)) {
                int i = 0;
                while (gamesResultSet.next()) {
                    i++;
                    if (i % 100 == 0) {
                        System.out.println("Processed " + i + " games");
                    }
                    // Parse players_1 and players_2 to get team1_player1, team1_player2, team2_player1, team2_player2
                    // Check if an edge already exists and insert if not     
                    String[] team1Players = parseTeamPlayers(gamesResultSet.getString("players_1")).toArray(new String[0]);
                    String[] team2Players = parseTeamPlayers(gamesResultSet.getString("players_2")).toArray(new String[0]);  
                    for (String player1 : team1Players) {
                        for (String player2 : team2Players) {
                            long id1 = getIDforPlayer(player1, connection);
                            long id2 = getIDforPlayer(player2, connection);
                            if(id1==-1 || id2 == -1){
                                continue;
                            }
    
                            if (!opponentEdgeExists(connection, id1, id2)) {
                                JsonObject gameObj = new JsonObject();
                                gameObj.addProperty("team_1", gamesResultSet.getString("team_1"));
                                gameObj.addProperty("team_2", gamesResultSet.getString("team_2"));
                                gameObj.addProperty("players_1", gamesResultSet.getString("players_1"));
                                gameObj.addProperty("players_2", gamesResultSet.getString("players_2"));
                                gameObj.addProperty("score_1", gamesResultSet.getString("score_1"));
                                gameObj.addProperty("score_2", gamesResultSet.getString("score_2"));
                                gameObj.addProperty("tournament_stage", gamesResultSet.getString("tournament_stage"));
                                gameObj.addProperty("tournament_name", gamesResultSet.getString("tournament_name"));
                                gameObj.addProperty("division", gamesResultSet.getString("division"));   
                                statement.setLong(1, id1);
                                statement.setLong(2, id2);
                                statement.setObject(3, gameObj, Types.OTHER);
                                statement.executeUpdate();
                            }
                        }
                    }
                }
            }
        }
    }

    private static boolean opponentEdgeExists(Connection connection, long id1, long id2) throws SQLException {
        // Check if an edge between player1 and player2 already exists in opponent_edges
        String querySQL = "SELECT * FROM opponent_edges WHERE (node1 = ? AND node2 = ?) OR (node1 = ? AND node2 = ?);";
        try (PreparedStatement statement = connection.prepareStatement(querySQL)) {
            statement.setLong(1, id1);
            statement.setLong(2, id2);
            statement.setLong(3, id2);
            statement.setLong(4, id1);
            return statement.executeQuery().next();
        }
    }

    // Other utility methods for fetching and parsing data should be implemented here

    private static Set<String> parseTeamPlayers(String players) {
        Set<String> playerNames = new HashSet<>();
        String[] playerArray = players.split(",");
        
        for (String player : playerArray) {
            playerNames.add(player.trim()); // Trim to remove leading/trailing spaces
        }

        return playerNames;
    }

}
