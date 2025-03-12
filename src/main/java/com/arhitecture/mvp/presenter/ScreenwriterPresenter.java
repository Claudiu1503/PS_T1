package com.arhitecture.mvp.presenter;

import com.arhitecture.mvp.model.Screenwriter;
import com.arhitecture.mvp.model.repository.ScreenwriterDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.sql.SQLException;
import java.util.List;

public class ScreenwriterPresenter {
    private TableView<Screenwriter> screenwriterTableView;
    private Button addButton;
    private Button updateButton;
    private Button deleteButton;
    private VBox view;

    private ScreenwriterDAO screenwriterDAO;

    public ScreenwriterPresenter() {
        screenwriterDAO = new ScreenwriterDAO();
        initialize();
    }

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

        addButton.setOnAction(e -> handleAddScreenwriter());
        updateButton.setOnAction(e -> handleUpdateScreenwriter());
        deleteButton.setOnAction(e -> handleDeleteScreenwriter());

        view = new VBox(10, screenwriterTableView, addButton, updateButton, deleteButton);
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
        // Logic for adding a screenwriter
    }

    private void handleUpdateScreenwriter() {
        // Logic for updating a screenwriter
    }

    private void handleDeleteScreenwriter() {
        // Logic for deleting a screenwriter
    }

    public VBox getView() {
        return view;
    }
}