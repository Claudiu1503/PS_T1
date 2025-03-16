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
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.List;

public class MoviePresenter {
    private TableView<Movie> movieTableView;
    private Button addButton;
    private Button updateButton;
    private Button deleteButton;
    private Button filterButton;
    private Button resetButton;
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

    public ObservableList<Movie> getMoviesByActor(int actorId) {
        ObservableList<Movie> movies = FXCollections.observableArrayList();
        try {
            List<Movie> movieList = movieDAO.getMoviesByActorId(actorId);
            movies.addAll(movieList);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return movies;
    }

    private void initialize() {
        movieTableView = new TableView<>();
        TableColumn<Movie, Integer> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(cellData -> cellData.getValue().idProperty().asObject());
        TableColumn<Movie, String> titleColumn = new TableColumn<>("Title");
        titleColumn.setCellValueFactory(cellData -> cellData.getValue().titleProperty());
        TableColumn<Movie, Integer> yearColumn = new TableColumn<>("Year");
        yearColumn.setCellValueFactory(cellData -> cellData.getValue().yearProperty().asObject());
        TableColumn<Movie, String> directorColumn = new TableColumn<>("Director");
        directorColumn.setCellValueFactory(cellData -> {
            Director director = cellData.getValue().getDirector();
            return new SimpleStringProperty(director != null ? director.getName() : "Unknown");
        });

        TableColumn<Movie, String> screenwriterColumn = new TableColumn<>("Screenwriter");
        screenwriterColumn.setCellValueFactory(cellData -> {
            Screenwriter screenwriter = cellData.getValue().getScreenwriter();
            return new SimpleStringProperty(screenwriter != null ? screenwriter.getName() : "Unknown");
        });
        TableColumn<Movie, String> actorsColumn = new TableColumn<>("Actors");
        actorsColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getActorsAsString()));

        movieTableView.getColumns().addAll(idColumn, titleColumn, yearColumn, directorColumn, screenwriterColumn, actorsColumn);

        addButton = new Button("Add Movie");
        updateButton = new Button("Update Movie");
        deleteButton = new Button("Delete Movie");
        filterButton = new Button("Filter by Actor");
        resetButton = new Button("Reset Filters");

        addButton.setOnAction(e -> handleAddMovie());
        updateButton.setOnAction(e -> handleUpdateMovie());
        deleteButton.setOnAction(e -> handleDeleteMovie());
        filterButton.setOnAction(e -> handleFilterByActor());
        resetButton.setOnAction(e -> loadMovies());

        HBox buttonBox = new HBox(10, addButton, updateButton, deleteButton, filterButton, resetButton);
        view = new VBox(10, movieTableView, buttonBox);
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

    private void handleFilterByActor() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Filter Movies");
        dialog.setHeaderText("Enter Actor Name");
        dialog.setContentText("Actor:");

        dialog.showAndWait().ifPresent(name -> {
            try {
                Actor actor = actorDAO.findActorByName(name);
                if (actor != null) {
                    ObservableList<Movie> movies = getMoviesByActor(actor.getId());
                    movieTableView.setItems(movies);
                } else {
                    showAlert("No Actor Found", "No actor found with the name: " + name);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
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

                try {
                    // Check if director exists
                    Director director = movieDAO.getDirectorByName(directorField.getText());
                    if (director == null) {
                        director = new Director();
                        director.setName(directorField.getText());
                        director.setId(movieDAO.saveDirector(director));
                    }
                    movie.setDirector(director);

                    // Check if screenwriter exists
                    Screenwriter screenwriter = movieDAO.getScreenwriterByName(screenwriterField.getText());
                    if (screenwriter == null) {
                        screenwriter = new Screenwriter();
                        screenwriter.setName(screenwriterField.getText());
                        screenwriter.setId(movieDAO.saveScreenwriter(screenwriter));
                    }
                    movie.setScreenwriter(screenwriter);

                    // Check if actors exist
                    ObservableList<Actor> actors = FXCollections.observableArrayList();
                    if (!actorsField.getText().isEmpty()) {
                        String[] actorNames = actorsField.getText().split(",");
                        for (String name : actorNames) {
                            Actor actor = actorDAO.findActorByName(name.trim());
                            if (actor == null) {
                                actor = new Actor();
                                actor.setName(name.trim());
                                actor.setId(actorDAO.addActor(actor));
                            }
                            actors.add(actor);
                        }
                    }
                    movie.setActors(actors);

                    movieDAO.addMovie(movie);
                    loadMovies();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                return movie;
            }
            return null;
        });

        dialog.showAndWait();
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