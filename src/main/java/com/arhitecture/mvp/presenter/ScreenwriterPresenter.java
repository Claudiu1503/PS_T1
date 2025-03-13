package com.arhitecture.mvp.presenter;

import com.arhitecture.mvp.model.Screenwriter;
import com.arhitecture.mvp.model.repository.ScreenwriterDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.List;

public class ScreenwriterPresenter {
    private TableView<Screenwriter> screenwriterTableView;
    private Button addButton;
    private Button updateButton;
    private Button deleteButton;
    private VBox view;

    private ScreenwriterDAO screenwriterDAO;

    public ScreenwriterPresenter(Stage primaryStage) {
        screenwriterDAO = new ScreenwriterDAO();
        initialize();
        Scene scene = new Scene(view, 800, 600);
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.setTitle("Screenwriter Management");
        primaryStage.show();
    }

    private Button refreshButton;

    private void initialize() {
        screenwriterTableView = new TableView<>();
        TableColumn<Screenwriter, Integer> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(cellData -> cellData.getValue().idProperty().asObject());
        TableColumn<Screenwriter, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());

        screenwriterTableView.getColumns().addAll(idColumn, nameColumn);

        addButton = new Button("Add Screenwriter");
        updateButton = new Button("Update Screenwriter");
        deleteButton = new Button("Delete Screenwriter");
        refreshButton = new Button("Refresh");

        addButton.setOnAction(e -> handleAddScreenwriter());
        updateButton.setOnAction(e -> handleUpdateScreenwriter());
        deleteButton.setOnAction(e -> handleDeleteScreenwriter());
        refreshButton.setOnAction(e -> loadScreenwriters());

        view = new VBox(10, screenwriterTableView, addButton, updateButton, deleteButton, refreshButton);
        loadScreenwriters();
    }

    private void loadScreenwriters() {
        try {
            List<Screenwriter> screenwriters = screenwriterDAO.getAllScreenwriters();
            ObservableList<Screenwriter> screenwriterList = FXCollections.observableArrayList(screenwriters);
            screenwriterTableView.setItems(screenwriterList);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void handleAddScreenwriter() {
        Dialog<Screenwriter> dialog = new Dialog<>();
        dialog.setTitle("Add Screenwriter");
        dialog.setHeaderText("Enter Screenwriter Details");

        ButtonType addButtonType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

        TextField nameField = new TextField();
        nameField.setPromptText("Name");

        VBox content = new VBox(10, nameField);
        dialog.getDialogPane().setContent(content);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButtonType) {
                Screenwriter screenwriter = new Screenwriter();
                screenwriter.setName(nameField.getText());
                return screenwriter;
            }
            return null;
        });

        dialog.showAndWait().ifPresent(screenwriter -> {
            try {
                screenwriterDAO.addScreenwriter(screenwriter);
                loadScreenwriters();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    private void handleUpdateScreenwriter() {
        Screenwriter selectedScreenwriter = screenwriterTableView.getSelectionModel().getSelectedItem();
        if (selectedScreenwriter != null) {
            Dialog<Screenwriter> dialog = new Dialog<>();
            dialog.setTitle("Update Screenwriter");
            dialog.setHeaderText("Update Screenwriter Details");

            ButtonType updateButtonType = new ButtonType("Update", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(updateButtonType, ButtonType.CANCEL);

            TextField nameField = new TextField(selectedScreenwriter.getName());

            VBox content = new VBox(10, nameField);
            dialog.getDialogPane().setContent(content);

            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == updateButtonType) {
                    selectedScreenwriter.setName(nameField.getText());
                    return selectedScreenwriter;
                }
                return null;
            });

            dialog.showAndWait().ifPresent(screenwriter -> {
                try {
                    screenwriterDAO.updateScreenwriter(screenwriter);
                    loadScreenwriters();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            });
        } else {
            showAlert("No Selection", "No Screenwriter Selected", "Please select a screenwriter in the table.");
        }
    }

    private void handleDeleteScreenwriter() {
        Screenwriter selectedScreenwriter = screenwriterTableView.getSelectionModel().getSelectedItem();
        if (selectedScreenwriter != null) {
            try {
                screenwriterDAO.deleteScreenwriter(selectedScreenwriter.getId());
                loadScreenwriters();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            showAlert("No Selection", "No Screenwriter Selected", "Please select a screenwriter in the table.");
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