package com.arhitecture.mvp.model;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Movie {
    private SimpleIntegerProperty id;
    private SimpleStringProperty title;
    private SimpleIntegerProperty year;
    private SimpleObjectProperty<Director> director;
    private SimpleObjectProperty<Screenwriter> screenwriter;
    private ObservableList<Actor> actors;

    public Movie() {
        this.id = new SimpleIntegerProperty();
        this.title = new SimpleStringProperty();
        this.year = new SimpleIntegerProperty();
        this.director = new SimpleObjectProperty<>();
        this.screenwriter = new SimpleObjectProperty<>();
        this.actors = FXCollections.observableArrayList();
    }

    public Movie(int id, String title, int year, Director director, Screenwriter screenwriter, ObservableList<Actor> actors) {
        this.id = new SimpleIntegerProperty(id);
        this.title = new SimpleStringProperty(title);
        this.year = new SimpleIntegerProperty(year);
        this.director = new SimpleObjectProperty<>(director);
        this.screenwriter = new SimpleObjectProperty<>(screenwriter);
        this.actors = actors;
    }

    public int getId() {
        return id.get();
    }

    public void setId(int id) {
        this.id.set(id);
    }

    public SimpleIntegerProperty idProperty() {
        return id;
    }

    public String getTitle() {
        return title.get();
    }

    public void setTitle(String title) {
        this.title.set(title);
    }

    public SimpleStringProperty titleProperty() {
        return title;
    }

    public int getYear() {
        return year.get();
    }

    public void setYear(int year) {
        this.year.set(year);
    }

    public SimpleIntegerProperty yearProperty() {
        return year;
    }

    public Director getDirector() {
        return director.get();
    }

    public void setDirector(Director director) {
        this.director.set(director);
    }

    public SimpleObjectProperty<Director> directorProperty() {
        return director;
    }

    public Screenwriter getScreenwriter() {
        return screenwriter.get();
    }

    public void setScreenwriter(Screenwriter screenwriter) {
        this.screenwriter.set(screenwriter);
    }

    public SimpleObjectProperty<Screenwriter> screenwriterProperty() {
        return screenwriter;
    }

    public ObservableList<Actor> getActors() {
        return actors;
    }

    public void setActors(ObservableList<Actor> actors) {
        this.actors = actors;
    }
}