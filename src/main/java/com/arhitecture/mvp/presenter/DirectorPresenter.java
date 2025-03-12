package com.arhitecture.mvp.presenter;

import com.arhitecture.mvp.model.Director;
import com.arhitecture.mvp.model.repository.DirectorDAO;
import com.arhitecture.mvp.model.repository.DirectorDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.sql.SQLException;
import java.util.List;

public class DirectorPresenter {
    private TableView<Director> directorTableView;
    private Button addButton;
    private Button updateButton;
    private Button deleteButton;
    private VBox view;

    private DirectorDAO directorDAO;

    public DirectorPresenter() {
        directorDAO = new DirectorDAO();
        initialize();
    }

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

        addButton.setOnAction(e -> handleAddDirector());
        updateButton.setOnAction(e -> handleUpdateDirector());
        deleteButton.setOnAction(e -> handleDeleteDirector());

        view = new VBox(10, directorTableView, addButton, updateButton, deleteButton);
        loadDirectors();
    }

    private void loadDirectors() {
        try {
            List<Director> directors = directorDAO.getAllDirectors();
            ObservableList<Director> directorList = FXCollections.observableArrayList(directors);
            directorTableView.setItems(directorList);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void handleAddDirector() {
        // Logic for adding a director
    }

    private void handleUpdateDirector() {
        // Logic for updating a director
    }

    private void handleDeleteDirector() {
        // Logic for deleting a director
    }

    public VBox getView() {
        return view;
    }
}