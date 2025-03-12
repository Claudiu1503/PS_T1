package com.arhitecture.mvp.model.repository;

import com.arhitecture.mvp.DatabaseConnection;
import com.arhitecture.mvp.model.Director;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DirectorDAO {
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

    public void deleteDirector(int id) throws SQLException {
        String query = "DELETE FROM directors WHERE id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, id);
            statement.executeUpdate();
        }
    }
}