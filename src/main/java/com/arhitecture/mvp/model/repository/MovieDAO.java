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
    String query = "SELECT m.*, d.name AS director_name, s.name AS screenwriter_name " +
            "FROM movies m " +
            "JOIN directors d ON m.director_id = d.id " +
            "JOIN screenwriters s ON m.screenwriter_id = s.id";
    try (Connection connection = DatabaseConnection.getConnection();
         Statement statement = connection.createStatement();
         ResultSet resultSet = statement.executeQuery(query)) {
        while (resultSet.next()) {
            Movie movie = new Movie();
            movie.setId(resultSet.getInt("id"));
            movie.setTitle(resultSet.getString("title"));
            movie.setYear(resultSet.getInt("year"));
            // Fetch and set director and screenwriter by name
            Director director = new Director();
            director.setId(resultSet.getInt("director_id"));
            director.setName(resultSet.getString("director_name"));
            movie.setDirector(director);

            Screenwriter screenwriter = new Screenwriter();
            screenwriter.setId(resultSet.getInt("screenwriter_id"));
            screenwriter.setName(resultSet.getString("screenwriter_name"));
            movie.setScreenwriter(screenwriter);

            // Fetch and set actors
            movie.setActors(getActorsByMovieId(resultSet.getInt("id")));
            movies.add(movie);
        }
    }
    return movies;
}
    public List<Movie> getMoviesByActorId(int actorId) throws SQLException {
        List<Movie> movies = new ArrayList<>();
        String query = "SELECT m.* FROM movies m JOIN movie_actors ma ON m.id = ma.movie_id WHERE ma.actor_id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, actorId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Movie movie = new Movie();
                movie.setId(resultSet.getInt("id"));
                movie.setTitle(resultSet.getString("title"));
                // Set other movie properties as needed
                movies.add(movie);
            }
        }
        return movies;
    }

    public Director getDirectorByName(String name) throws SQLException {
        String query = "SELECT * FROM directors WHERE name = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, name);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                Director director = new Director();
                director.setId(resultSet.getInt("id"));
                director.setName(resultSet.getString("name"));
                return director;
            }
        }
        return null;
    }

    public Screenwriter getScreenwriterByName(String name) throws SQLException {
        String query = "SELECT * FROM screenwriters WHERE name = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, name);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                Screenwriter screenwriter = new Screenwriter();
                screenwriter.setId(resultSet.getInt("id"));
                screenwriter.setName(resultSet.getString("name"));
                return screenwriter;
            }
        }
        return null;
    }

    private Director getDirectorById(int id) throws SQLException {
        String query = "SELECT * FROM directors WHERE id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    Director director = new Director();
                    director.setId(resultSet.getInt("id"));
                    director.setName(resultSet.getString("name"));
                    return director;
                }
            }
        }
        return null;
    }

    private Screenwriter getScreenwriterById(int id) throws SQLException {
        String query = "SELECT * FROM screenwriters WHERE id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    Screenwriter screenwriter = new Screenwriter();
                    screenwriter.setId(resultSet.getInt("id"));
                    screenwriter.setName(resultSet.getString("name"));
                    return screenwriter;
                }
            }
        }
        return null;
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

    public int saveDirector(Director director) throws SQLException {
        String query = "INSERT INTO directors (name) VALUES (?)";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, director.getName());
            statement.executeUpdate();
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                }
            }
        }
        return 0;
    }

    public int saveScreenwriter(Screenwriter screenwriter) throws SQLException {
        String query = "INSERT INTO screenwriters (name) VALUES (?)";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, screenwriter.getName());
            statement.executeUpdate();
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                }
            }
        }
        return 0;
    }



    private ObservableList<Actor> getActorsByMovieId(int movieId) throws SQLException {
        String query = "SELECT a.* FROM actors a JOIN movie_actors ma ON a.id = ma.actor_id WHERE ma.movie_id = ?";
        ObservableList<Actor> actors = FXCollections.observableArrayList();
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, movieId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Actor actor = new Actor();
                    actor.setId(resultSet.getInt("id"));
                    actor.setName(resultSet.getString("name"));
                    actors.add(actor);
                }
            }
        }
        return actors;
    }

    private void insertMovieActors(Movie movie) throws SQLException {
        String insertActorQuery = "INSERT INTO actors (name) VALUES (?)";
        String insertMovieActorQuery = "INSERT INTO movie_actors (movie_id, actor_id) VALUES (?, ?)";

        try (Connection connection = DatabaseConnection.getConnection()) {
            for (Actor actor : movie.getActors()) {
                if (actor.getId() == 0) {
                    // Save actor and get generated ID
                    try (PreparedStatement insertActorStmt = connection.prepareStatement(insertActorQuery, Statement.RETURN_GENERATED_KEYS)) {
                        insertActorStmt.setString(1, actor.getName());
                        insertActorStmt.executeUpdate();
                        try (ResultSet generatedKeys = insertActorStmt.getGeneratedKeys()) {
                            if (generatedKeys.next()) {
                                actor.setId(generatedKeys.getInt(1));
                            }
                        }
                    }
                }
                // Insert into movie_actors table
                try (PreparedStatement insertMovieActorStmt = connection.prepareStatement(insertMovieActorQuery)) {
                    insertMovieActorStmt.setInt(1, movie.getId());
                    insertMovieActorStmt.setInt(2, actor.getId());
                    insertMovieActorStmt.executeUpdate();
                }
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