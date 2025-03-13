package com.arhitecture.mvp.model.repository;


import com.arhitecture.mvp.DatabaseConnection;
import com.arhitecture.mvp.model.Screenwriter;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ScreenwriterDAO {
    public List<Screenwriter> getAllScreenwriters() throws SQLException {
        List<Screenwriter> screenwriters = new ArrayList<>();
        String query = "SELECT * FROM screenwriters";
        try (Connection connection = DatabaseConnection.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            while (resultSet.next()) {
                Screenwriter screenwriter = new Screenwriter();
                screenwriter.setId(resultSet.getInt("id"));
                screenwriter.setName(resultSet.getString("name"));
                screenwriters.add(screenwriter);
            }
        }
        return screenwriters;
    }

    public void addScreenwriter(Screenwriter screenwriter) throws SQLException {
        String query = "INSERT INTO screenwriters (name) VALUES (?)";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, screenwriter.getName());
            statement.executeUpdate();
        }
    }

    public void updateScreenwriter(Screenwriter screenwriter) throws SQLException {
        String query = "UPDATE screenwriters SET name = ? WHERE id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, screenwriter.getName());
            statement.setInt(2, screenwriter.getId());
            statement.executeUpdate();
        }
    }

    public void deleteScreenwriter(int screenwriterId) throws SQLException {
        // First, delete or update related movies
        String updateMoviesQuery = "UPDATE movies SET screenwriter_id = NULL WHERE screenwriter_id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement updateStatement = connection.prepareStatement(updateMoviesQuery)) {
            updateStatement.setInt(1, screenwriterId);
            updateStatement.executeUpdate();
        }

        // Then, delete the screenwriter
        String deleteScreenwriterQuery = "DELETE FROM screenwriters WHERE id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement deleteStatement = connection.prepareStatement(deleteScreenwriterQuery)) {
            deleteStatement.setInt(1, screenwriterId);
            deleteStatement.executeUpdate();
        }
    }
}