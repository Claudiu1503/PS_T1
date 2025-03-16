package com.arhitecture.mvp.presenter;

import com.arhitecture.mvp.model.Movie;
import javafx.collections.ObservableList;
import javafx.scene.layout.VBox;

public interface MoviePresenterInterface {
    void handleAddMovie();
    void handleUpdateMovie();
    void handleDeleteMovie();
    void handleFilterByActor();
//    void handleExportToCSV();
//    void handleExportToDOC();
    void loadMovies();
    ObservableList<Movie> getMoviesByActor(int actorId);
    VBox getView();
    void handleExportToCSV();
    void handleExportToDOC();
}
