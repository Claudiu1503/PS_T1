package com.arhitecture.mvp.presenter;

import com.arhitecture.mvp.model.Actor;
import com.arhitecture.mvp.model.repository.ActorREPO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.List;

public class ActorPresenter {
    private TableView<Actor> actorTableView;
    private Button addButton;
    private Button updateButton;
    private Button deleteButton;
    private VBox view;

    private ActorREPO actorREPO;

    public ActorPresenter(Stage primaryStage) {
        actorREPO = new ActorREPO();
        initialize();
        Scene scene = new Scene(view, 800, 600);
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.setTitle("Director Management");
        primaryStage.show();
    }

    private Button refreshButton;

    private void initialize() {
        actorTableView = new TableView<>();
        TableColumn<Actor, Integer> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(cellData -> cellData.getValue().idProperty().asObject());
        TableColumn<Actor, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());

        actorTableView.getColumns().addAll(idColumn, nameColumn);

        addButton = new Button("Add Actor");
        updateButton = new Button("Update Actor");
        deleteButton = new Button("Delete Actor");
        refreshButton = new Button("Refresh");

        addButton.setOnAction(e -> handleAddActor());
        updateButton.setOnAction(e -> handleUpdateActor());
        deleteButton.setOnAction(e -> handleDeleteActor());
        refreshButton.setOnAction(e -> loadActors());

        view = new VBox(10, actorTableView, addButton, updateButton, deleteButton, refreshButton);
        loadActors();
    }

    private void loadActors() {
        try {
            List<Actor> actors = actorREPO.getAllActors();
            ObservableList<Actor> actorList = FXCollections.observableArrayList(actors);
            actorTableView.setItems(actorList);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void handleAddActor() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Add Actor");
        dialog.setHeaderText("Add a new actor");
        dialog.setContentText("Name:");

        dialog.showAndWait().ifPresent(name -> {
            Actor actor = new Actor();
            actor.setName(name);
            try {
                actorREPO.addActor(actor);
                loadActors();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    private void handleUpdateActor() {
        Actor selectedActor = actorTableView.getSelectionModel().getSelectedItem();
        if (selectedActor != null) {
            TextInputDialog dialog = new TextInputDialog(selectedActor.getName());
            dialog.setTitle("Update Actor");
            dialog.setHeaderText("Update actor");
            dialog.setContentText("Name:");

            dialog.showAndWait().ifPresent(name -> {
                selectedActor.setName(name);
                try {
                    actorREPO.updateActor(selectedActor);
                    loadActors();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            });
        } else {
            showAlert("No Selection", "No Actor Selected", "Please select an actor in the table.");
        }
    }

    private void handleDeleteActor() {
        Actor selectedActor = actorTableView.getSelectionModel().getSelectedItem();
        if (selectedActor != null) {
            try {
                actorREPO.deleteActor(selectedActor.getId());
                loadActors();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            showAlert("No Selection", "No Actor Selected", "Please select an actor in the table.");
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