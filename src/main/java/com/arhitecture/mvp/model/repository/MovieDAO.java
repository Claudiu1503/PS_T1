package com.arhitecture.mvp.model.repository;

import com.arhitecture.mvp.DatabaseConnection;
import com.arhitecture.mvp.model.Movie;
import com.arhitecture.mvp.model.Actor;
import com.arhitecture.mvp.model.Director;
import com.arhitecture.mvp.model.Screenwriter;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MovieDAO {
    public List<Movie> getAllMovies() throws SQLException {
        List<Movie> movies = new ArrayList<>();
        String query = "SELECT * FROM movies";
        try (Connection connection = DatabaseConnection.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            while (resultSet.next()) {
                Movie movie = new Movie();
                movie.setId(resultSet.getInt("id"));
                movie.setTitle(resultSet.getString("title"));
                movie.setYear(resultSet.getInt("year"));
                // Fetch and set director, screenwriter, and actors
                // Assuming you have methods to fetch these entities by their IDs
                movie.setDirector(getDirectorById(resultSet.getInt("director_id")));
                movie.setScreenwriter(getScreenwriterById(resultSet.getInt("screenwriter_id")));
                movie.setActors(getActorsByMovieId(resultSet.getInt("id")));
                movies.add(movie);
            }
        }
        return movies;
    }

    public void addMovie(Movie movie) throws SQLException {
        String query = "INSERT INTO movies (title, year, director_id, screenwriter_id) VALUES (?, ?, ?, ?)";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, movie.getTitle());
            statement.setInt(2, movie.getYear());
            statement.setInt(3, movie.getDirector().getId());
            statement.setInt(4, movie.getScreenwriter().getId());
            statement.executeUpdate();
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    movie.setId(generatedKeys.getInt(1));
                }
            }
            // Insert actors into movie_actors table
            insertMovieActors(movie);
        }
    }

    public void updateMovie(Movie movie) throws SQLException {
        String query = "UPDATE movies SET title = ?, year = ?, director_id = ?, screenwriter_id = ? WHERE id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, movie.getTitle());
            statement.setInt(2, movie.getYear());
            statement.setInt(3, movie.getDirector().getId());
            statement.setInt(4, movie.getScreenwriter().getId());
            statement.setInt(5, movie.getId());
            statement.executeUpdate();
            // Update actors in movie_actors table
            updateMovieActors(movie);
        }
    }

    public void deleteMovie(int id) throws SQLException {
        String deleteMovieActorsQuery = "DELETE FROM movie_actors WHERE movie_id = ?";
        String deleteMovieQuery = "DELETE FROM movies WHERE id = ?";
        try (Connection connection = DatabaseConnection.getConnection()) {
            try (PreparedStatement deleteMovieActorsStmt = connection.prepareStatement(deleteMovieActorsQuery)) {
                deleteMovieActorsStmt.setInt(1, id);
                deleteMovieActorsStmt.executeUpdate();
            }
            try (PreparedStatement deleteMovieStmt = connection.prepareStatement(deleteMovieQuery)) {
                deleteMovieStmt.setInt(1, id);
                deleteMovieStmt.executeUpdate();
            }
        }
    }

    private Director getDirectorById(int id) throws SQLException {
        // Implement this method to fetch a director by ID
        return null;
    }

    private Screenwriter getScreenwriterById(int id) throws SQLException {
        // Implement this method to fetch a screenwriter by ID
        return null;
    }

    private ObservableList<Actor> getActorsByMovieId(int movieId) throws SQLException {
        // Implement this method to fetch actors by movie ID
        return FXCollections.observableArrayList();
    }

    private void insertMovieActors(Movie movie) throws SQLException {
        String query = "INSERT INTO movie_actors (movie_id, actor_id) VALUES (?, ?)";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            for (Actor actor : movie.getActors()) {
                statement.setInt(1, movie.getId());
                statement.setInt(2, actor.getId());
                statement.executeUpdate();
            }
        }
    }

    private void updateMovieActors(Movie movie) throws SQLException {
        String deleteQuery = "DELETE FROM movie_actors WHERE movie_id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement deleteStatement = connection.prepareStatement(deleteQuery)) {
            deleteStatement.setInt(1, movie.getId());
            deleteStatement.executeUpdate();
        }
        insertMovieActors(movie);
    }
}