package com.arhitecture.mvp.model.repository;

import com.arhitecture.mvp.DatabaseConnection;
import com.arhitecture.mvp.model.Actor;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ActorDAO {
    public List<Actor> getAllActors() throws SQLException {
        List<Actor> actors = new ArrayList<>();
        String query = "SELECT * FROM actors";
        try (Connection connection = DatabaseConnection.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            while (resultSet.next()) {
                Actor actor = new Actor();
                actor.setId(resultSet.getInt("id"));
                actor.setName(resultSet.getString("name"));
                actors.add(actor);
            }
        }
        return actors;
    }

    public Actor findActorByName(String name) throws SQLException {
        String query = "SELECT * FROM actors WHERE name = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, name);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                Actor actor = new Actor();
                actor.setId(resultSet.getInt("id"));
                actor.setName(resultSet.getString("name"));
                return actor;
            }
        }
        return null;
    }

    public int addActor(Actor actor) throws SQLException {
        String query = "INSERT INTO actors (name) VALUES (?)";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, actor.getName());
            statement.executeUpdate();
            ResultSet generatedKeys = statement.getGeneratedKeys();
            if (generatedKeys.next()) {
                return generatedKeys.getInt(1);
            } else {
                throw new SQLException("Creating actor failed, no ID obtained.");
            }
        }
    }

    public void updateActor(Actor actor) throws SQLException {
        String query = "UPDATE actors SET name = ? WHERE id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, actor.getName());
            statement.setInt(2, actor.getId());
            statement.executeUpdate();
        }
    }

    public void deleteActor(int id) throws SQLException {

        String deleteMovieActorsQuery = "DELETE FROM movie_actors WHERE actor_id = ?";
        String deleteActorQuery = "DELETE FROM actors WHERE id = ?";

        try (Connection connection = DatabaseConnection.getConnection()) {
            // Delete related entries in movie_actors table
            try (PreparedStatement deleteMovieActorsStmt = connection.prepareStatement(deleteMovieActorsQuery)) {
                deleteMovieActorsStmt.setInt(1, id);
                deleteMovieActorsStmt.executeUpdate();
            }

            // Delete actor
            try (PreparedStatement deleteActorStmt = connection.prepareStatement(deleteActorQuery)) {
                deleteActorStmt.setInt(1, id);
                deleteActorStmt.executeUpdate();
            }
        }
    }
}
