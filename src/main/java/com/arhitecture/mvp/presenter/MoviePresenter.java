package com.arhitecture.mvp.presenter;

import com.arhitecture.mvp.model.Actor;
import com.arhitecture.mvp.model.Director;
import com.arhitecture.mvp.model.Movie;
import com.arhitecture.mvp.model.Screenwriter;
import com.arhitecture.mvp.model.repository.ActorREPO;
import com.arhitecture.mvp.model.repository.MovieREPO;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
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

    private MovieREPO movieREPO;
    private ActorREPO actorREPO;

    public MoviePresenter(Stage primaryStage) {
        movieREPO = new MovieREPO();
        actorREPO = new ActorREPO(); // Initialize actorDAO
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
            List<Movie> movieList = movieREPO.getMoviesByActorId(actorId);
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

        TableColumn<Movie, String> categoryColumn = new TableColumn<>("Category");
        categoryColumn.setCellValueFactory(cellData -> cellData.getValue().categoryProperty());

        movieTableView.getColumns().addAll(idColumn, titleColumn, yearColumn, directorColumn, screenwriterColumn, actorsColumn, categoryColumn);

        addButton = new Button("Add Movie");
        updateButton = new Button("Update Movie");
        deleteButton = new Button("Delete Movie");
        filterButton = new Button("Filter by Actor");
        resetButton = new Button("Reset Filters");
        Button exportCSVButton = new Button("Export to CSV");
        Button exportDOCButton = new Button("Export to DOC");

        addButton.setOnAction(e -> handleAddMovie());
        updateButton.setOnAction(e -> handleUpdateMovie());
        deleteButton.setOnAction(e -> handleDeleteMovie());
        filterButton.setOnAction(e -> handleFilterByActor());
        resetButton.setOnAction(e -> loadMovies());
        exportCSVButton.setOnAction(e -> handleExportToCSV());
        exportDOCButton.setOnAction(e -> handleExportToDOC());

        HBox buttonBox = new HBox(10, addButton, updateButton, deleteButton, filterButton, resetButton, exportCSVButton, exportDOCButton);
        view = new VBox(10, movieTableView, buttonBox);
        loadMovies();
    }

    private void handleExportToCSV() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Export to CSV");
        dialog.setHeaderText("Enter Category");
        dialog.setContentText("Category:");

        dialog.showAndWait().ifPresent(category -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
            File file = fileChooser.showSaveDialog(view.getScene().getWindow());
            if (file != null) {
                exportMoviesToCSV(movieTableView.getItems(), category, file.getAbsolutePath());
            }
        });
    }

    private void handleExportToDOC() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Export to DOC");
        dialog.setHeaderText("Enter Category");
        dialog.setContentText("Category:");

        dialog.showAndWait().ifPresent(category -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("DOC Files", "*.docx"));
            File file = fileChooser.showSaveDialog(view.getScene().getWindow());
            if (file != null) {
                exportMoviesToDOC(movieTableView.getItems(), category, file.getAbsolutePath());
            }
        });
    }

    private void loadMovies() {
        try {
            List<Movie> movies = movieREPO.getAllMovies();
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
                Actor actor = actorREPO.findActorByName(name);
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

        ButtonType addButtonType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

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
        ComboBox<String> categoryComboBox = new ComboBox<>();
        categoryComboBox.setItems(FXCollections.observableArrayList("Action", "Comedy", "Drama", "Horror", "Sci-Fi"));

        VBox content = new VBox(10, titleField, yearField, directorField, screenwriterField, actorsField, categoryComboBox);
        dialog.getDialogPane().setContent(content);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButtonType) {
                Movie movie = new Movie();
                movie.setTitle(titleField.getText());
                movie.setYear(Integer.parseInt(yearField.getText()));
                movie.setCategory(categoryComboBox.getValue());

                try {
                    Director director = movieREPO.getDirectorByName(directorField.getText());
                    if (director == null) {
                        director = new Director();
                        director.setName(directorField.getText());
                        director.setId(movieREPO.saveDirector(director));
                    }
                    movie.setDirector(director);

                    Screenwriter screenwriter = movieREPO.getScreenwriterByName(screenwriterField.getText());
                    if (screenwriter == null) {
                        screenwriter = new Screenwriter();
                        screenwriter.setName(screenwriterField.getText());
                        screenwriter.setId(movieREPO.saveScreenwriter(screenwriter));
                    }
                    movie.setScreenwriter(screenwriter);

                    ObservableList<Actor> actors = FXCollections.observableArrayList();
                    if (!actorsField.getText().isEmpty()) {
                        String[] actorNames = actorsField.getText().split(",");
                        for (String name : actorNames) {
                            Actor actor = actorREPO.findActorByName(name.trim());
                            if (actor == null) {
                                actor = new Actor();
                                actor.setName(name.trim());
                                actor.setId(actorREPO.addActor(actor));
                            }
                            actors.add(actor);
                        }
                    }
                    movie.setActors(actors);

                    movieREPO.addMovie(movie);
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

            ButtonType updateButtonType = new ButtonType("Update", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(updateButtonType, ButtonType.CANCEL);

            TextField titleField = new TextField(selectedMovie.getTitle());
            TextField yearField = new TextField(String.valueOf(selectedMovie.getYear()));
            TextField directorField = new TextField(selectedMovie.getDirector() != null ? selectedMovie.getDirector().getName() : "");
            TextField screenwriterField = new TextField(selectedMovie.getScreenwriter() != null ? selectedMovie.getScreenwriter().getName() : "");
            TextField actorsField = new TextField(selectedMovie.getActorsAsString());
            ComboBox<String> categoryComboBox = new ComboBox<>();
            categoryComboBox.setItems(FXCollections.observableArrayList("Action", "Comedy", "Drama", "Horror", "Sci-Fi"));
            categoryComboBox.setValue(selectedMovie.getCategory());

            VBox content = new VBox(10, titleField, yearField, directorField, screenwriterField, actorsField, categoryComboBox);
            dialog.getDialogPane().setContent(content);

            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == updateButtonType) {
                    selectedMovie.setTitle(titleField.getText());
                    selectedMovie.setYear(Integer.parseInt(yearField.getText()));
                    selectedMovie.setCategory(categoryComboBox.getValue());

                    try {
                        Director director = movieREPO.getDirectorByName(directorField.getText());
                        if (director == null) {
                            director = new Director();
                            director.setName(directorField.getText());
                            director.setId(movieREPO.saveDirector(director));
                        }
                        selectedMovie.setDirector(director);

                        Screenwriter screenwriter = movieREPO.getScreenwriterByName(screenwriterField.getText());
                        if (screenwriter == null) {
                            screenwriter = new Screenwriter();
                            screenwriter.setName(screenwriterField.getText());
                            screenwriter.setId(movieREPO.saveScreenwriter(screenwriter));
                        }
                        selectedMovie.setScreenwriter(screenwriter);

                        ObservableList<Actor> actors = FXCollections.observableArrayList();
                        if (!actorsField.getText().isEmpty()) {
                            String[] actorNames = actorsField.getText().split(",");
                            for (String name : actorNames) {
                                Actor actor = actorREPO.findActorByName(name.trim());
                                if (actor == null) {
                                    actor = new Actor();
                                    actor.setName(name.trim());
                                    actor.setId(actorREPO.addActor(actor));
                                }
                                actors.add(actor);
                            }
                        }
                        selectedMovie.setActors(actors);

                        movieREPO.updateMovie(selectedMovie);
                        loadMovies();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    return selectedMovie;
                }
                return null;
            });

            dialog.showAndWait();
        } else {
            showAlert("No Selection", "No Movie Selected", "Please select a movie in the table.");
        }
    }


    private void handleDeleteMovie() {
        Movie selectedMovie = movieTableView.getSelectionModel().getSelectedItem();
        if (selectedMovie != null) {
            try {
                movieREPO.deleteMovie(selectedMovie.getId());
                loadMovies();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            showAlert("No Selection", "No Movie Selected", "Please select a movie in the table.");
        }
    }

    public void exportMoviesToCSV(List<Movie> movies, String category, String filePath) {
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.append("ID,Title,Year,Director,Screenwriter,Actors\n");
            for (Movie movie : movies) {
                if (movie.getCategory().equals(category)) {
                    writer.append(String.valueOf(movie.getId())).append(",");
                    writer.append(movie.getTitle()).append(",");
                    writer.append(String.valueOf(movie.getYear())).append(",");
                    writer.append(movie.getDirector().getName()).append(",");
                    writer.append(movie.getScreenwriter().getName()).append(",");
                    writer.append(movie.getActorsAsString()).append("\n");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void exportMoviesToDOC(List<Movie> movies, String category, String filePath) {
        try (XWPFDocument document = new XWPFDocument()) {
            XWPFParagraph paragraph = document.createParagraph();
            XWPFRun run = paragraph.createRun();
            run.setText("Movies in category: " + category);
            run.addBreak();

            for (Movie movie : movies) {
                if (movie.getCategory().equals(category)) {
                    run.setText("ID: " + movie.getId());
                    run.addBreak();
                    run.setText("Title: " + movie.getTitle());
                    run.addBreak();
                    run.setText("Year: " + movie.getYear());
                    run.addBreak();
                    run.setText("Director: " + movie.getDirector().getName());
                    run.addBreak();
                    run.setText("Screenwriter: " + movie.getScreenwriter().getName());
                    run.addBreak();
                    run.setText("Actors: " + movie.getActorsAsString());
                    run.addBreak();
                    run.addBreak();
                }
            }

            try (FileOutputStream out = new FileOutputStream(filePath)) {
                document.write(out);
            }
        } catch (IOException e) {
            e.printStackTrace();
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