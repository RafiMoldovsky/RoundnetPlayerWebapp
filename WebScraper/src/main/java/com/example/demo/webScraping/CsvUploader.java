package com.example.demo.webScraping;

import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import com.opencsv.CSVReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.sql.Date;


public class CsvUploader {
    public static void main(String[] args) {
        String csvFilePath = "games.csv"; // Change this to the desired CSV file
        String tableName = "games"; // Change this to the desired table name
        String jdbcUrl = System.getProperty("spring.datasource.url");
        String username = System.getProperty("spring.datasource.username");
        String password = System.getProperty("spring.datasource.password");
        long programStartTime = System.currentTimeMillis();

        try (Connection connection = DriverManager.getConnection(jdbcUrl, username, password);
             CSVReader reader = new CSVReader(new FileReader(csvFilePath))) {
            String[] nextLine;
            String insertQuery = generateInsertQuery(tableName);
            PreparedStatement preparedStatement = connection.prepareStatement(insertQuery);

            // Skip the header row
            try{
                reader.readNext();
                int i = 0;
                while ((nextLine = reader.readNext()) != null) {
                    i++;
                    setPreparedStatementValues(preparedStatement, nextLine, tableName);
                    preparedStatement.executeUpdate();
                    
                    if (i % 200 == 0) {
                        System.out.println("Uploaded " + i + " lines");
                    }
                }
            }
            catch(Exception e){}
            preparedStatement.close();
            System.out.println("Data uploaded successfully.");
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }

        long programEndTime = System.currentTimeMillis();
        long programElapsedTime = programEndTime - programStartTime;
        System.out.println("Finished uploading " + csvFilePath + " in " + programElapsedTime + " milliseconds");
    }

    private static String generateInsertQuery(String tableName) {
        // Modify this based on your table structure
        // You may need to adjust the column names and placeholders
        switch (tableName) {
            case "records":
                return "INSERT INTO records (team_name, wins, losses, result, division, tournament) VALUES (?, ?, ?, ?, ?, ?)";
            case "games":
                return "INSERT INTO games (team_1, team_2, players_1, players_2, score_1, score_2, tournament_stage, tournament_name, division) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            case "teams":
                return "INSERT INTO teams (team_name, player_1, player_2, tournament) VALUES (?, ?, ?, ?)";
            case "tournaments":
                return "INSERT INTO tournaments (tournament_name, url, date) VALUES (?, ?, ?)";
            case "series":
                return "INSERT INTO series (team_1, team_2, round, tournament, team_1_scores, team_2_scores, division) VALUES (?, ?, ?, ?, ?, ?, ?)";
            // Add cases for other tables as needed
            default:
                throw new IllegalArgumentException("Unsupported table name: " + tableName);
        }
    }

    private static void setPreparedStatementValues(PreparedStatement preparedStatement, String[] nextLine, String tableName) throws SQLException {
        // Modify this based on your table structure
        // For example, you can use a switch statement to map columns to indices
        switch (tableName) {
            case "records":
                preparedStatement.setString(1, nextLine[0]); // Team Name
                preparedStatement.setInt(2, Integer.parseInt(nextLine[1])); // Wins
                preparedStatement.setInt(3, Integer.parseInt(nextLine[2])); // Losses
                preparedStatement.setInt(4, Integer.parseInt(nextLine[3])); // Result
                preparedStatement.setString(5, nextLine[4]); // Division
                preparedStatement.setString(6, nextLine[5]); // Tournament
                break;
            case "games":
                preparedStatement.setString(1, nextLine[0]); // Team 1
                preparedStatement.setString(2, nextLine[1]); // Team 2
                preparedStatement.setString(3, nextLine[2]); // Players 1
                preparedStatement.setString(4, nextLine[3]); // Players 2
                preparedStatement.setInt(5, Integer.parseInt(nextLine[4])); // Score 1
                preparedStatement.setInt(6, Integer.parseInt(nextLine[5])); // Score 2
                preparedStatement.setString(7, nextLine[6]); // Tournament Stage
                preparedStatement.setString(8, nextLine[7]); // Tournament Name
                preparedStatement.setString(9, nextLine[8]); // Division
                break;
            case "teams":
                preparedStatement.setString(1, nextLine[2]); // Team Name
                preparedStatement.setString(2, nextLine[0]); // Player 1
                preparedStatement.setString(3, nextLine[1]); // Player 2
                preparedStatement.setString(4, nextLine[3]); // Tournament
                break;
            case "tournaments":
                preparedStatement.setString(1, nextLine[0]); // Tournament name
                preparedStatement.setString(2, nextLine[1]); // url
                preparedStatement.setDate(3, dateNameToDate(nextLine[2])); // date
                break;
            case "series":
                preparedStatement.setString(1, nextLine[0]); // Team 1
                preparedStatement.setString(2, nextLine[1]); // Team 2
                preparedStatement.setString(3, nextLine[2]); // Round
                preparedStatement.setString(4, nextLine[3]); // Tournament
                preparedStatement.setString(5, nextLine[4]); // Team 1 Scores
                preparedStatement.setString(6, nextLine[5]); // Team 2 Scores
                preparedStatement.setString(7, nextLine[6]); // Division
                break;
            // Add cases for other tables as needed
            default:
                throw new IllegalArgumentException("Unsupported table name: " + tableName);
        }
    }

    public static Date dateNameToDate(String dateString) {
        // This string is in the format: June 17th, 2023 – June 18th, 2023
        SimpleDateFormat format = new SimpleDateFormat("MMMM d'th', yyyy");
        String[] dateParts = dateString.split(" \uFFFD ");
        try {
            Date startDate = new Date(format.parse(dateParts[0]).getTime());
            return startDate;
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
}
