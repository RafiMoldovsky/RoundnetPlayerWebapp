package com.example.demo.webScraping;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class NameChangeProgram {
    public static void main(String[] args) {
        String originalName = "2k'";
        String newName = "2k\u2022";
        String jdbcUrl = System.getProperty("spring.datasource.url");
        String username = System.getProperty("spring.datasource.username");
        String password = System.getProperty("spring.datasource.password");

        try (Connection connection = DriverManager.getConnection(jdbcUrl, username, password)) {
            updateTeamName(connection, originalName, newName, "games", "team_1");
            updateTeamName(connection, originalName, newName, "games", "team_2");
            updateTeamName(connection, originalName, newName, "series", "team_1");
            updateTeamName(connection, originalName, newName, "series", "team_2");
            updateTeamName(connection, originalName, newName, "records", "team_name");
            updateTeamName(connection, originalName, newName, "teams", "team_name");
            System.out.println("Name changes completed successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void updateTeamName(Connection connection, String originalName, String newName,
                                      String tableName, String columnName) throws SQLException {
        String updateQuery = "UPDATE " + tableName + " SET " + columnName + " = ? WHERE " + columnName + " = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {
            preparedStatement.setString(1, newName);
            preparedStatement.setString(2, originalName);
            preparedStatement.executeUpdate();
        }
    }
}
