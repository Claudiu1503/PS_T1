package com.arhitecture.mvp.model.repository;

import com.arhitecture.mvp.DatabaseConnection;
import com.arhitecture.mvp.model.Director;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DirectorREPO {
    public List<Director> getAllDirectors() throws SQLException {
        List<Director> directors = new ArrayList<>();
        String query = "SELECT * FROM directors";
        try (Connection connection = DatabaseConnection.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            while (resultSet.next()) {
                Director director = new Director();
                director.setId(resultSet.getInt("id"));
                director.setName(resultSet.getString("name"));
                directors.add(director);
            }
        }
        return directors;
    }

    public void addDirector(Director director) throws SQLException {
        String query = "INSERT INTO directors (name) VALUES (?)";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, director.getName());
            statement.executeUpdate();
        }
    }

    public void updateDirector(Director director) throws SQLException {
        String query = "UPDATE directors SET name = ? WHERE id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, director.getName());
            statement.setInt(2, director.getId());
            statement.executeUpdate();
        }
    }

    public void deleteDirector(int directorId) throws SQLException {
        // First, delete or update related movies
        String updateMoviesQuery = "UPDATE movies SET director_id = NULL WHERE director_id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement updateStatement = connection.prepareStatement(updateMoviesQuery)) {
            updateStatement.setInt(1, directorId);
            updateStatement.executeUpdate();
        }

        // Then, delete the director
        String deleteDirectorQuery = "DELETE FROM directors WHERE id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement deleteStatement = connection.prepareStatement(deleteDirectorQuery)) {
            deleteStatement.setInt(1, directorId);
            deleteStatement.executeUpdate();
        }
    }
}