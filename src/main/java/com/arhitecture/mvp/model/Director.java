package com.arhitecture.mvp.model;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class Director {

    private SimpleIntegerProperty id;
    private SimpleStringProperty name;

    public Director() {
        this.id = new SimpleIntegerProperty();
        this.name = new SimpleStringProperty();
    }

    public Director(SimpleIntegerProperty id, SimpleStringProperty name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id.get();
    }

    public SimpleIntegerProperty idProperty() {
        return id;
    }

    public void setId(int id) {
        this.id.set(id);
    }

    public String getName() {
        return name.get();
    }

    public SimpleStringProperty nameProperty() {
        return name;
    }

    public void setName(String name) {
        this.name.set(name);
    }
}
