package com.arhitecture.mvp.presenter;

import com.arhitecture.mvp.model.Actor;
import com.arhitecture.mvp.model.Director;
import com.arhitecture.mvp.model.Movie;
import com.arhitecture.mvp.model.Screenwriter;
import com.arhitecture.mvp.model.repository.ActorDAO;
import com.arhitecture.mvp.model.repository.MovieDAO;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.List;

public class MoviePresenter {
    private TableView<Movie> movieTableView;
    private Button addButton;
    private Button updateButton;
    private Button deleteButton;
    private VBox view;

    private MovieDAO movieDAO;
    private ActorDAO actorDAO;

    public MoviePresenter(Stage primaryStage) {
        movieDAO = new MovieDAO();
        actorDAO = new ActorDAO(); // Initialize actorDAO
        initialize();
        Scene scene = new Scene(view, 800, 600);
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.setTitle("Movie Management");
        primaryStage.show();
    }
    private Button refreshButton;

    private void initialize() {
        movieTableView = new TableView<>();
        TableColumn<Movie, Integer> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(cellData -> cellData.getValue().idProperty().asObject());
        TableColumn<Movie, String> titleColumn = new TableColumn<>("Title");
        titleColumn.setCellValueFactory(cellData -> cellData.getValue().titleProperty());
        TableColumn<Movie, Integer> yearColumn = new TableColumn<>("Year");
        yearColumn.setCellValueFactory(cellData -> cellData.getValue().yearProperty().asObject());
        TableColumn<Movie, String> directorColumn = new TableColumn<>("Director");
        directorColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDirector().getName()));
        TableColumn<Movie, String> screenwriterColumn = new TableColumn<>("Screenwriter");
        screenwriterColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getScreenwriter().getName()));
        TableColumn<Movie, String> actorsColumn = new TableColumn<>("Actors");
        actorsColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getActorsAsString()));

        movieTableView.getColumns().addAll(idColumn, titleColumn, yearColumn, directorColumn, screenwriterColumn, actorsColumn);

        addButton = new Button("Add Movie");
        updateButton = new Button("Update Movie");
        deleteButton = new Button("Delete Movie");
        refreshButton = new Button("Refresh");

        addButton.setOnAction(e -> handleAddMovie());
        updateButton.setOnAction(e -> handleUpdateMovie());
        deleteButton.setOnAction(e -> handleDeleteMovie());
        refreshButton.setOnAction(e -> loadMovies());

        view = new VBox(10, movieTableView, addButton, updateButton, deleteButton, refreshButton);
        loadMovies();
    }

    private void loadMovies() {
        try {
            List<Movie> movies = movieDAO.getAllMovies();
            ObservableList<Movie> movieList = FXCollections.observableArrayList(movies);
            movieTableView.setItems(movieList);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void handleAddMovie() {
        Dialog<Movie> dialog = new Dialog<>();
        dialog.setTitle("Add Movie");
        dialog.setHeaderText("Enter Movie Details");

        // Set the button types
        ButtonType addButtonType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

        // Create the fields for movie details
        TextField titleField = new TextField();
        titleField.setPromptText("Title");
        TextField yearField = new TextField();
        yearField.setPromptText("Year");
        TextField directorField = new TextField();
        directorField.setPromptText("Director");
        TextField screenwriterField = new TextField();
        screenwriterField.setPromptText("Screenwriter");
        TextField actorsField = new TextField();
        actorsField.setPromptText("Actors (comma separated)");

        VBox content = new VBox(10, titleField, yearField, directorField, screenwriterField, actorsField);
        dialog.getDialogPane().setContent(content);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButtonType) {
                Movie movie = new Movie();
                movie.setTitle(titleField.getText());
                movie.setYear(Integer.parseInt(yearField.getText()));

                // Set director, screenwriter, and actors
                Director director = new Director();
                director.setName(directorField.getText().isEmpty() ? "Default Director" : directorField.getText());
                movie.setDirector(director);

                Screenwriter screenwriter = new Screenwriter();
                screenwriter.setName(screenwriterField.getText().isEmpty() ? "Default Screenwriter" : screenwriterField.getText());
                movie.setScreenwriter(screenwriter);

                ObservableList<Actor> actors = FXCollections.observableArrayList();
                if (!actorsField.getText().isEmpty()) {
                    String[] actorNames = actorsField.getText().split(",");
                    for (String name : actorNames) {
                        Actor actor = new Actor();
                        actor.setName(name.trim());
                        actors.add(actor);
                    }
                } else {
                    Actor defaultActor = new Actor();
                    defaultActor.setName("Default Actor");
                    actors.add(defaultActor);
                }
                movie.setActors(actors);

                return movie;
            }
            return null;
        });

        dialog.showAndWait().ifPresent(movie -> {
            try {
                // Ensure director and screenwriter are saved in the database
                if (movie.getDirector().getId() == 0) {
                    // Save director and get generated ID
                    movie.getDirector().setId(movieDAO.saveDirector(movie.getDirector()));
                }
                if (movie.getScreenwriter().getId() == 0) {
                    // Save screenwriter and get generated ID
                    movie.getScreenwriter().setId(movieDAO.saveScreenwriter(movie.getScreenwriter()));
                }
                movieDAO.addMovie(movie);
                loadMovies();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

private void handleUpdateMovie() {
    Movie selectedMovie = movieTableView.getSelectionModel().getSelectedItem();
    if (selectedMovie != null) {
        Dialog<Movie> dialog = new Dialog<>();
        dialog.setTitle("Update Movie");
        dialog.setHeaderText("Update Movie Details");

        // Set the button types
        ButtonType updateButtonType = new ButtonType("Update", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(updateButtonType, ButtonType.CANCEL);

        // Create the fields for movie details
        TextField titleField = new TextField(selectedMovie.getTitle());
        TextField yearField = new TextField(String.valueOf(selectedMovie.getYear()));
        TextField directorField = new TextField(selectedMovie.getDirector() != null ? selectedMovie.getDirector().getName() : "");
        TextField screenwriterField = new TextField(selectedMovie.getScreenwriter() != null ? selectedMovie.getScreenwriter().getName() : "");
        TextField actorsField = new TextField(selectedMovie.getActorsAsString());

        VBox content = new VBox(10, titleField, yearField, directorField, screenwriterField, actorsField);
        dialog.getDialogPane().setContent(content);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == updateButtonType) {
                selectedMovie.setTitle(titleField.getText());
                selectedMovie.setYear(Integer.parseInt(yearField.getText()));

                // Update director, screenwriter, and actors
                Director director = new Director();
                director.setName(directorField.getText().isEmpty() ? "Default Director" : directorField.getText());
                selectedMovie.setDirector(director);

                Screenwriter screenwriter = new Screenwriter();
                screenwriter.setName(screenwriterField.getText().isEmpty() ? "Default Screenwriter" : screenwriterField.getText());
                selectedMovie.setScreenwriter(screenwriter);

                ObservableList<Actor> actors = FXCollections.observableArrayList();
                if (!actorsField.getText().isEmpty()) {
                    String[] actorNames = actorsField.getText().split(",");
                    for (String name : actorNames) {
                        Actor actor;
                        try {
                            actor = actorDAO.findActorByName(name.trim());
                            if (actor == null) {
                                actor = new Actor();
                                actor.setName(name.trim());
                                actor.setId(actorDAO.addActor(actor)); // Save new actor and get ID
                            }
                            actors.add(actor);
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    Actor defaultActor = new Actor();
                    defaultActor.setName("Default Actor");
                    actors.add(defaultActor);
                }
                selectedMovie.setActors(actors);

                return selectedMovie;
            }
            return null;
        });

        dialog.showAndWait().ifPresent(movie -> {
            try {
                // Ensure director and screenwriter are saved in the database
                if (movie.getDirector().getId() == 0) {
                    // Save director and get generated ID
                    movie.getDirector().setId(movieDAO.saveDirector(movie.getDirector()));
                }
                if (movie.getScreenwriter().getId() == 0) {
                    // Save screenwriter and get generated ID
                    movie.getScreenwriter().setId(movieDAO.saveScreenwriter(movie.getScreenwriter()));
                }
                movieDAO.updateMovie(movie);
                loadMovies();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    } else {
        showAlert("No Selection", "No Movie Selected", "Please select a movie in the table.");
    }
}

    private void handleDeleteMovie() {
        Movie selectedMovie = movieTableView.getSelectionModel().getSelectedItem();
        if (selectedMovie != null) {
            try {
                movieDAO.deleteMovie(selectedMovie.getId());
                loadMovies();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            showAlert("No Selection", "No Movie Selected", "Please select a movie in the table.");
        }
    }

    private void showAlert(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public VBox getView() {
        return view;
    }
}