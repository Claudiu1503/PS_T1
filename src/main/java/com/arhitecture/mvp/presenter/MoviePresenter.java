package com.arhitecture.mvp.presenter;

import com.arhitecture.mvp.model.Movie;
import com.arhitecture.mvp.model.repository.MovieDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.sql.SQLException;
import java.util.List;

public class MoviePresenter {
    private TableView<Movie> movieTableView;
    private Button addButton;
    private Button updateButton;
    private Button deleteButton;
    private VBox view;

    private MovieDAO movieDAO;

    public MoviePresenter() {
        movieDAO = new MovieDAO();
        initialize();
    }

    private void initialize() {
        movieTableView = new TableView<>();
        TableColumn<Movie, Integer> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(cellData -> cellData.getValue().idProperty().asObject());
        TableColumn<Movie, String> titleColumn = new TableColumn<>("Title");
        titleColumn.setCellValueFactory(cellData -> cellData.getValue().titleProperty());
        TableColumn<Movie, Integer> yearColumn = new TableColumn<>("Year");
        yearColumn.setCellValueFactory(cellData -> cellData.getValue().yearProperty().asObject());

        movieTableView.getColumns().addAll(idColumn, titleColumn, yearColumn);

        addButton = new Button("Add Movie");
        updateButton = new Button("Update Movie");
        deleteButton = new Button("Delete Movie");

        addButton.setOnAction(e -> handleAddMovie());
        updateButton.setOnAction(e -> handleUpdateMovie());
        deleteButton.setOnAction(e -> handleDeleteMovie());

        view = new VBox(10, movieTableView, addButton, updateButton, deleteButton);
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
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Add Movie");
        dialog.setHeaderText("Enter Movie Details");
        dialog.setContentText("Title:");

        dialog.showAndWait().ifPresent(title -> {
            TextInputDialog yearDialog = new TextInputDialog();
            yearDialog.setTitle("Add Movie");
            yearDialog.setHeaderText("Enter Movie Details");
            yearDialog.setContentText("Year:");

            yearDialog.showAndWait().ifPresent(year -> {
                try {
                    Movie movie = new Movie();
                    movie.setTitle(title);
                    movie.setYear(Integer.parseInt(year));
                    // Assuming you have methods to set director, screenwriter, and actors
                    movieDAO.addMovie(movie);
                    loadMovies();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            });
        });
    }

    private void handleUpdateMovie() {
        Movie selectedMovie = movieTableView.getSelectionModel().getSelectedItem();
        if (selectedMovie != null) {
            TextInputDialog dialog = new TextInputDialog(selectedMovie.getTitle());
            dialog.setTitle("Update Movie");
            dialog.setHeaderText("Enter Movie Details");
            dialog.setContentText("Title:");

            dialog.showAndWait().ifPresent(title -> {
                TextInputDialog yearDialog = new TextInputDialog(String.valueOf(selectedMovie.getYear()));
                yearDialog.setTitle("Update Movie");
                yearDialog.setHeaderText("Enter Movie Details");
                yearDialog.setContentText("Year:");

                yearDialog.showAndWait().ifPresent(year -> {
                    try {
                        selectedMovie.setTitle(title);
                        selectedMovie.setYear(Integer.parseInt(year));
                        // Ensure director and screenwriter are not null
                        if (selectedMovie.getDirector() == null) {
                            // Set a default or prompt user to select a director
                            showAlert("No Director", "No Director Selected", "Please select a director for the movie.");
                            return;
                        }
                        if (selectedMovie.getScreenwriter() == null) {
                            // Set a default or prompt user to select a screenwriter
                            showAlert("No Screenwriter", "No Screenwriter Selected", "Please select a screenwriter for the movie.");
                            return;
                        }
                        movieDAO.updateMovie(selectedMovie);
                        loadMovies();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                });
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