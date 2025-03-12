package com.arhitecture.mvp.model;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class Screenwriter {
    private SimpleIntegerProperty id;
    private SimpleStringProperty name;

    public Screenwriter() {
        this.id = new SimpleIntegerProperty();
        this.name = new SimpleStringProperty();
    }

    public Screenwriter(int id, String name) {
        this.id = new SimpleIntegerProperty(id);
        this.name = new SimpleStringProperty(name);
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

    public String getName() {
        return name.get();
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public SimpleStringProperty nameProperty() {
        return name;
    }
}