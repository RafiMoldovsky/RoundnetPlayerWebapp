package com.example.demo.webScraping;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class NameChangeProgram {
    public static void main(String[] args) {
        String originalName = "GFD Rogue";
        String newName = "Rogue";
        String jdbcUrl = "jdbc:postgresql://mahmud.db.elephantsql.com:5432/qwjqdpav";
        String username = "qwjqdpav";
        String password = "RUgzDe5vDvda80Y1hv2RUhU8XVo75M-D";

        try (Connection connection = DriverManager.getConnection(jdbcUrl, username, password)) {
            updateTeamName(connection, originalName, newName, "games", "team_1");
            updateTeamName(connection, originalName, newName, "games", "team_2");
            updateTeamName(connection, originalName, newName, "series", "team_1");
            updateTeamName(connection, originalName, newName, "series", "team_2");
            updateTeamName(connection, originalName, newName, "records", "team_name");
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
