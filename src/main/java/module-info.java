module com.arhitecture.mvp {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.apache.poi.ooxml;

    exports com.arhitecture.mvp;
    exports com.arhitecture.mvp.view;
    exports com.arhitecture.mvp.presenter;
    exports com.arhitecture.mvp.model;
    exports com.arhitecture.mvp.model.repository;
    exports com.arhitecture.mvp.presenter.impl;
}