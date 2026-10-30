package com.fu.ch08.t1;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class StudentApp extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(StudentApp.class.getResource("student-view.fxml"));
        stage.setScene(new Scene(loader.load()));
        stage.setTitle("T1 - Quản lý sinh viên");
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
