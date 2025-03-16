package com.arhitecture.mvp.presenter;

import com.arhitecture.mvp.model.Director;
import com.arhitecture.mvp.model.repository.DirectorREPO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.List;

public class DirectorPresenter {
    private TableView<Director> directorTableView;
    private Button addButton;
    private Button updateButton;
    private Button deleteButton;
    private VBox view;

    private DirectorREPO directorREPO;

    public DirectorPresenter(Stage primaryStage) {
        directorREPO = new DirectorREPO();
        initialize();
        Scene scene = new Scene(view, 800, 600);
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.setTitle("Director Management");
        primaryStage.show();
    }

    private Button refreshButton;

    private void initialize() {
        directorTableView = new TableView<>();
        TableColumn<Director, Integer> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(cellData -> cellData.getValue().idProperty().asObject());
        TableColumn<Director, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());

        directorTableView.getColumns().addAll(idColumn, nameColumn);

        addButton = new Button("Add Director");
        updateButton = new Button("Update Director");
        deleteButton = new Button("Delete Director");
        refreshButton = new Button("Refresh");

        addButton.setOnAction(e -> handleAddDirector());
        updateButton.setOnAction(e -> handleUpdateDirector());
        deleteButton.setOnAction(e -> handleDeleteDirector());
        refreshButton.setOnAction(e -> loadDirectors());

        view = new VBox(10, directorTableView, addButton, updateButton, deleteButton, refreshButton);
        loadDirectors();
    }

    private void loadDirectors() {
        try {
            List<Director> directors = directorREPO.getAllDirectors();
            ObservableList<Director> directorList = FXCollections.observableArrayList(directors);
            directorTableView.setItems(directorList);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void handleAddDirector() {
        Dialog<Director> dialog = new Dialog<>();
        dialog.setTitle("Add Director");
        dialog.setHeaderText("Enter Director Details");

        ButtonType addButtonType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

        TextField nameField = new TextField();
        nameField.setPromptText("Name");

        VBox content = new VBox(10, nameField);
        dialog.getDialogPane().setContent(content);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButtonType) {
                Director director = new Director();
                director.setName(nameField.getText());
                return director;
            }
            return null;
        });

        dialog.showAndWait().ifPresent(director -> {
            try {
                directorREPO.addDirector(director);
                loadDirectors();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    private void handleUpdateDirector() {
        Director selectedDirector = directorTableView.getSelectionModel().getSelectedItem();
        if (selectedDirector != null) {
            Dialog<Director> dialog = new Dialog<>();
            dialog.setTitle("Update Director");
            dialog.setHeaderText("Update Director Details");

            ButtonType updateButtonType = new ButtonType("Update", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(updateButtonType, ButtonType.CANCEL);

            TextField nameField = new TextField(selectedDirector.getName());

            VBox content = new VBox(10, nameField);
            dialog.getDialogPane().setContent(content);

            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == updateButtonType) {
                    selectedDirector.setName(nameField.getText());
                    return selectedDirector;
                }
                return null;
            });

            dialog.showAndWait().ifPresent(director -> {
                try {
                    directorREPO.updateDirector(director);
                    loadDirectors();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            });
        } else {
            showAlert("No Selection", "No Director Selected", "Please select a director in the table.");
        }
    }

    private void handleDeleteDirector() {
        Director selectedDirector = directorTableView.getSelectionModel().getSelectedItem();
        if (selectedDirector != null) {
            try {
                directorREPO.deleteDirector(selectedDirector.getId());
                loadDirectors();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            showAlert("No Selection", "No Director Selected", "Please select a director in the table.");
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