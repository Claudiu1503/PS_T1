package com.arhitecture.mvp.view;

import com.arhitecture.mvp.presenter.ActorPresenter;
import com.arhitecture.mvp.presenter.DirectorPresenter;
import com.arhitecture.mvp.presenter.MoviePresenter;
import com.arhitecture.mvp.presenter.ScreenwriterPresenter;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;

public class DesktopGUI extends Application {
    @Override
    public void start(Stage stage) {
        TabPane tabPane = new TabPane();

        Tab actorTab = new Tab("Actors", new ActorPresenter().getView());
        Tab directorTab = new Tab("Directors", new DirectorPresenter().getView());
        Tab screenwriterTab = new Tab("Screenwriters", new ScreenwriterPresenter().getView());
        Tab movieTab = new Tab("Movies", new MoviePresenter().getView());

        tabPane.getTabs().addAll(actorTab, directorTab, screenwriterTab, movieTab);

        Scene scene = new Scene(tabPane, 800, 600);
        stage.setTitle("Film Production Management");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}